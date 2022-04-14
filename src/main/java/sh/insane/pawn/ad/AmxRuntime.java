package sh.insane.pawn.ad;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import sh.insane.pawn.Header;
import sh.insane.pawn.NativeTableEntry;
import sh.insane.pawn.PublicTableEntry;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

@Log4j2
public class AmxRuntime {
    public static final int CELL_SIZE = 4;

    private static final int PUBLIC_TABLE_OFFSET = 56;
    private static final int NATIVE_TABLE_OFFSET = 60;

    @Getter
    private final AmxHeader amxHeader;
    private byte[] fileContent;

    private int pri;
    private int alt;
    private int cod;
    private int dat;
    private int cip;
    private int stp;
    private int stk;
    private int frm;
    private int hea;

    @SneakyThrows
    public AmxRuntime(String path) {
        fileContent = Files.readAllBytes(Paths.get("E:\\Users\\Lukas\\Desktop\\samp03x\\pawno\\timertest.amx"));

        amxHeader = readAmxHeader();

        log.info("initial runtime size is {}", fileContent.length);

        log.info("Flags {} {}", amxHeader.getFlags(), getBit((byte)amxHeader.getFlags(), 2));

        byte[] b = new byte[amxHeader.getStp()];
        System.arraycopy(fileContent, 0, b, 0, fileContent.length);

        fileContent = b;

        byte[] b2 = new byte[amxHeader.getStp()];

        log.info("expanding runtime size to stp {}", amxHeader.getStp());
    }

    public byte getBit(byte input, int position)
    {
        return (byte) ((input >> position) & 1);
    }

    private boolean isContinuation(byte value) {
        return getBit(value, 8) == 1;
    }

    public void executeMain() {
        if(!getAmxHeader().hasMainFunction()) {
            return;
        }

        hea = getAmxHeader().getHea();
        stp = getAmxHeader().getStp();
        cip = getAmxHeader().getCip();

        stk = stp;

        verboseExecute2(cip);
    }

    private void verboseExecute(int fromCip) {
        cip = fromCip;

        while(true) {
            OpCode opCode = readInstruction(cip);

            if(opCode == null) {
                break;
            }

            if(opCode.operandSize > 0) {
                int operand = readInt(amxHeader.getCod() + cip + 4);
                log.info("[{}] [{}] ({})", opCode.instruction, opCode.name(), Integer.toHexString(operand));
            } else log.info("[{}] [{}]", opCode.instruction, opCode.name());

            if(opCode.equals(OpCode.CONST_PRI)) {
                break;
            }

            advanceToNextInstruction(opCode);
        }
    }

    private void verboseExecute2(int fromCip) {
        log.info(amxHeader);

        cip = fromCip;

        while(true && cip + amxHeader.getCod() < amxHeader.getDat()) {
            OpCode opCode = readInstruction(cip);

            if(opCode == null) {
                break;
            }

            if(opCode.operandSize > 0) {
                int operand = readInt(amxHeader.getCod() + cip + 4);
                log.info("[{}] [{}] ({}) (({}))", opCode.instruction, opCode.name(), Integer.toHexString(operand), readString(amxHeader.getDat() + operand));
            } else log.info("[{}] [{}]", opCode.instruction, opCode.name());

            advanceToNextInstruction(opCode);
        }
    }

    private void writeDat(int address, int value) {
        writeInt(readInt(getAmxHeader().getDat() + address), value);
    }

    private int nextAndGet() {
        cip += 4;
        int value = readInt(cip + amxHeader.getCod());
        return value;
    }

    private OpCode readInstruction(int fromCip) {
        int instruction = readByte(amxHeader.getCod() + fromCip) & 0XFF;

        OpCode opCode = OpCode.getFromInstruction(instruction);

        if(opCode == null) {
            log.fatal("Could not get opcode from cip {} for instruction {}", cip, instruction);
        }

        return opCode;
    }

    private OpCode readInstruction2(int fromCip) {
        byte instruction = readByte(amxHeader.getCod() + fromCip);

        int count = 0;

        while(isContinuation(instruction)) {
            BitSet b = new BitSet();
            b.set(0, getBit(instruction, 0));
            b.set(1, getBit(instruction, 1));
            b.set(2, getBit(instruction, 2));
            b.set(3, getBit(instruction, 3));
            b.set(4, getBit(instruction, 4));
            b.set(5, getBit(instruction, 5));
            b.set(6, getBit(instruction, 6));

            count++;

            instruction = readByte(amxHeader.getCod() + fromCip + count);
        }

        OpCode opCode = OpCode.getFromInstruction(instruction);

        if(opCode == null) {
            log.fatal("Could not get opcode from cip {} for instruction {}", cip, instruction);
        }

        return opCode;
    }

    private int readOperand(int fromCip) {
        ByteBuffer b = ByteBuffer.allocate(8);

        b.put(readByte(amxHeader.getCod() + fromCip + 4));
        b.put(readByte(amxHeader.getCod() + fromCip + 5));
        b.put(readByte(amxHeader.getCod() + fromCip + 6));
        b.put(readByte(amxHeader.getCod() + fromCip + 7));

        return b.asIntBuffer().get();
    }

    private void advanceToNextInstruction(OpCode current) {
        int bytesToMove = current.operandSize;
        bytesToMove += 4;
        cip += bytesToMove;
    }

    private void execute(int offset) {
        cip = offset;

        //TODO: next() durch advanceToNextInstruction ersetzen

        while(true) {
            OpCode opCode = readInstruction(cip);

            log.info("current instruction is [{}]", opCode.name());

            switch(opCode) {
                case CONST_PRI: {
                    //CONST.pri
                    int value = nextAndGet();
                    pri = value;
                }
                case ADDR_ALT: {
                    //14 	ADDR.alt 	offset 	ALT = FRM + offset
                    int value = nextAndGet();
                    alt = frm + value;
                }
                case STOR_I: {
                    //23 	STOR.I 		[ALT] = PRI (full cell)
                    writeInt(alt, pri);
                }
                case PUSH_ALT: {
                    //37 	PUSH.alt 		STK = STK - cell size, [STK] = ALT
                    stk = stk - 4;
                    writeInt(stk, alt);
                }
                case PUSH_C: {
                    //PUSH.C 	value 	STK = STK - cell size, [STK] = value
                    int value = nextAndGet();

                    log.info("Value fetched is {}", value);

                    stk = stk - 4;
                    writeInt(readInt(stk), value);
                }
                case STACK: {
                    //44 	STACK 	value 	ALT = STK, STK = STK + value
                    alt = stk;
                    int value = nextAndGet();
                    stk = stk + value;
                }
                case HEAP: {
                    //45 	HEAP 	value 	ALT = HEA, HEA = HEA + value
                    alt = hea;
                    int value = nextAndGet();
                    hea = hea + value;
                }
                case PROC: {
                    //PROC
                    stk = stk - 4;
                    writeInt(stk, frm);
                    frm = stk;
                }
                case RETN: {
                    //48 	RETN 		FRM = [STK], STK = STK + cell size, CIP = [STK],
                    // STK = STK + cell size, STK = STK + [STK] + cell size
                    int value = readInt(amxHeader.getDat() + stk);
                    frm = value;
                    stk = stk + 4;
                    cip = readInt(amxHeader.getDat() + stk);
                    stk = stk + 4;
                    stk = stk + readInt(amxHeader.getDat() + stk) + 4;
                }
                case ZERO_PRI: {
                    //ZERO.pri
                    pri = 0;
                }
                case FILL: {
                    //119 	FILL 	number 	fill 'number' bytes of memory from [ALT]
                    // with value in PRI (number must be multiple of cell size)

                    int value = nextAndGet();

                    int altOff = readInt(amxHeader.getDat() + alt);

                    for(int i = 0; i < value; i++) {
                        ByteBuffer.wrap(fileContent).order(ByteOrder.LITTLE_ENDIAN).putInt(amxHeader.getDat() + i * 4, altOff);
                    }
                }
                case HALT: {
                    //HALT
                    System.out.println("HALT");
                    return;
                }
                case SYSREQ_C: {
                    //123 	SYSREQ.C 	value 	call system service
                    int value = nextAndGet();
                    System.out.println("SYSREQ.C call");
                }
                case PUSH_ADDR: {
                   //133 	PUSH.ADR 	offset 	STK = STK - cell size, [STK] = FRM + offset
                    stk = stk - 4;
                    int value = nextAndGet();
                    writeInt(stk, frm + value);
                }
                case BREAK: {
                    //BREAK
                }
            }

            advanceToNextInstruction(opCode);
        }
    }

    private void next() {
        cip += 4;
    }

    private AmxHeader readAmxHeader() {
        return AmxHeader.builder()
                .size(readInt(0))
                .magic(readShort(4))
                .fileVersion(readByte(6))
                .amxVersion(readByte(7))
                .flags(readShort(8))
                .defSize(readShort(10))
                .cod(readInt(12))
                .dat(readInt(16))
                .hea(readInt(20))
                .stp(readInt(24))
                .cip(readInt(28))
                .publics(readInt(32))
                .natives(readInt(36))
                .libraries(readInt(40))
                .pubvars(readInt(44))
                .tags(readInt(48))
                .nameTable(readInt(52))
                .build();
    }

    public int getPublicCount() {
        return (getAmxHeader().getNatives() - getAmxHeader().getPublics()) / getAmxHeader().getDefSize();
    }

    public int getNativeCount() {
        return (getAmxHeader().getLibraries() - getAmxHeader().getNatives()) / getAmxHeader().getDefSize();
    }

    public List<PublicTableEntry> getPublicTable() {
        List<PublicTableEntry> recordEntries = new ArrayList<>();

        int publicCount = getPublicCount();

        for(int i = 0; i < publicCount; i++) {
            int address = readInt(PUBLIC_TABLE_OFFSET + (i * 8));
            int nameAddress = readInt(PUBLIC_TABLE_OFFSET + 4 + (i * 8));

            recordEntries.add(new PublicTableEntry(i, address, readString(nameAddress)));
        }

        return recordEntries;
    }

    public List<NativeTableEntry> getNativeTable() {
        List<NativeTableEntry> recordEntries = new ArrayList<>();

        int nativeCount = getNativeCount();

        for(int i = getPublicCount(); i < nativeCount; i++) { //TODO: seems to work, but is definitely not the intended way
            int nameAddress = readInt(NATIVE_TABLE_OFFSET + (i * 8));

            recordEntries.add(new NativeTableEntry(i - getPublicCount(), readString(nameAddress)));
        }

        return recordEntries;
    }

    public byte readByte(int offset) {
        return ByteBuffer.wrap(fileContent, offset, 1).order(ByteOrder.LITTLE_ENDIAN).get();
    }

    public int readInt(int offset) {
        return ByteBuffer.wrap(fileContent, offset, 4).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get();
    }

    public short readShort(int offset) {
        return ByteBuffer.wrap(fileContent, offset, 2).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get();
    }

    public void writeInt(int offset, int value) {
        ByteBuffer.wrap(fileContent).order(ByteOrder.LITTLE_ENDIAN).putInt(offset, value);
    }

    public String readString(int offset) {
        String result = "";

        while(true) {
            if(offset < 0 || offset >= fileContent.length) {
                break;
            }

            byte b = readByte(offset);

            if(b != 0) {
                offset += 4;

                result = result + (char)b;
            } else {
                break;
            }
        }

        result = result.replace("\n", "\\n");

        return result;
    }
}
