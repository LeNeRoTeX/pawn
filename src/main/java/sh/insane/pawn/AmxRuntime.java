package sh.insane.pawn;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import sh.insane.pawn.callback.builtin.*;
import sh.insane.pawn.callback.NativeCallback;
import sh.insane.pawn.code.NativeTableEntry;
import sh.insane.pawn.code.OpCode;
import sh.insane.pawn.code.PublicTableEntry;
import sh.insane.pawn.extension.AmxContext;
import sh.insane.pawn.extension.Plugin;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

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
    private int cip;
    private int stp;
    private int stk;
    private int frm;
    private int hea;

    private final Map<String, NativeCallback> registeredNatives = new HashMap<>();

    @SneakyThrows
    public AmxRuntime(String path) {
        fileContent = Files.readAllBytes(Paths.get("/Users/lukas/Desktop/timertest.amx"));

        amxHeader = readAmxHeader();

        log.info("initial runtime size is {}", fileContent.length);

        log.info("Flags {} {}", amxHeader.getFlags(), getBit((byte)amxHeader.getFlags(), 2));

        byte[] b = new byte[amxHeader.getStp()];
        System.arraycopy(fileContent, 0, b, 0, fileContent.length);

        fileContent = b;

        log.info("expanding runtime size to stp {}", amxHeader.getStp());

        for(PublicTableEntry entry : getPublicTable()) {
            log.info(entry);
        }

        for(NativeTableEntry entry : getNativeTable()) {
            log.info(entry);
        }

        registerNative("print", new Print());
        registerNative("SetTimer", new SetTimer());
        registerNative("SetGameModeText", new SetGameModeText());
        registerNative("AddPlayerClass", new AddPlayerClass());
        registerNative("GetTickCount", new GetTickCount());
        registerNative("format", new Format());
        registerNative("SendClientMessageToAll", new SendClientMessageToAll());

        Plugin p = new BuiltInRuntimePlugin();

        p.onPluginLoad(new AmxContext());
    }

    private void registerNative(String nativeName, NativeCallback nativeCallback) {
        Objects.requireNonNull(nativeName);
        Objects.requireNonNull(nativeCallback);

        if(registeredNatives.containsKey(nativeName)) {
            throw new AmxRuntimeException(String.format("native with name '%s' is already registered", nativeName));
        }

        registeredNatives.put(nativeName, nativeCallback);
    }

    private NativeCallback getNativeCallback(String nativeName) {
        return registeredNatives.get(nativeName);
    }

    public byte getBit(byte input, int position)
    {
        return (byte) ((input >> position) & 1);
    }

    private boolean isContinuation(byte value) {
        return getBit(value, 8) == 1;
    }

    public void executePublic(int publicId) {
        PublicTableEntry publicTableEntry = getPublicTable().stream().filter(p -> p.getId() == publicId).findFirst().get();

        hea = getAmxHeader().getHea();
        stp = getAmxHeader().getStp();
        cip = getAmxHeader().getCip();

        stk = stp;

        stk -= 8;

        frm = stk;

        //verboseExecute2(cip);

        cip = publicTableEntry.getAddress();

        log.info("STP: " + stp);
        log.info("STK: " + stk);

        exec(cip);

        log.info("STK2: " + stk);
    }

    public void executeMain() {
        if(!getAmxHeader().hasMainFunction()) {
            return;
        }

        hea = getAmxHeader().getHea();
        stp = getAmxHeader().getStp();
        cip = getAmxHeader().getCip();

        stk = stp;

        stk -= 8;

        frm = stk;

        //verboseExecute2(cip);

        cip = getAmxHeader().getCip();

        log.info("STP: " + stp);
        log.info("STK: " + stk);

        exec(cip);

        log.info("STK2: " + stk);
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

            if(opCode.equals(OpCode.RETN)) {
                return;
            }

            advanceToNextInstruction(opCode);
        }
    }

    public int getOperand(int instructionCip) {
        return readInt(amxHeader.getCod() + instructionCip + 4);
    }

    public NativeTableEntry getNative(int nativeId) {
        return getNativeTable().stream().filter(n -> n.getId() == nativeId).findFirst().get();
    }

    private void exec(int fromCip) {
        cip = fromCip;

        while(true && cip + amxHeader.getCod() < amxHeader.getDat()) {
            OpCode opCode = readInstruction(cip);

            if(opCode == null) {
                break;
            }

            /*if(opCode.operandSize > 0) {
                int operand = readInt(amxHeader.getCod() + cip + 4);
                log.info("[{}] [{}] ({}) (({}))", opCode.instruction, opCode.name(), Integer.toHexString(operand), readString(amxHeader.getDat() + operand));
            } else log.info("[{}] [{}]", opCode.instruction, opCode.name());
*/
            switch(opCode) {
                case PROC: {
                    writeInt(stk, frm);
                    stk = stk - CELL_SIZE;
                    frm = stk;

                    advanceToNextInstruction(opCode);
                    break;
                }
                case BREAK: {
                    advanceToNextInstruction(opCode);
                    break;
                }
                case ZERO_PRI: {
                    pri = 0;

                    advanceToNextInstruction(opCode);
                    break;
                }
                case PUSH_PRI: {
                    stk = stk - CELL_SIZE;
                    writeInt(stk, pri);

                    advanceToNextInstruction(opCode);
                    break;
                } case PUSH_C: {
                    stk = stk - CELL_SIZE;
                    writeInt(stk, getOperand(cip));

                    advanceToNextInstruction(opCode);
                    break;
                } case SYSREQ_C: {
                    NativeTableEntry nativeTableEntry = getNative(getOperand(cip));

                    if(!registeredNatives.containsKey(nativeTableEntry.getName())) {
                        throw new AmxRuntimeException(String.format("native with name '%s' not registered", nativeTableEntry.getName()));
                    }

                    NativeCallback nativeCallback = getNativeCallback(nativeTableEntry.getName());

                    int argumentCount = readInt(stk) / CELL_SIZE;

                    List<Integer> callArguments;

                    if(argumentCount > 0) {
                        callArguments = new ArrayList<>();

                        if(argumentCount > 0) {
                            for(int i = 0; i < argumentCount; i++) {
                                callArguments.add(readInt(stk + 4 + i * 4));
                            }
                        }
                    } else {
                        callArguments = Collections.emptyList();
                    }

                    pri = nativeCallback.call(new ExecutionContext((x, value) -> writeInt(x, value), (x) -> readString(x), (x) -> readInt(x) , amxHeader), callArguments);

                    advanceToNextInstruction(opCode);
                    break;
                } case STACK: {
                    alt = stk;
                    stk = stk + getOperand(cip);

                    advanceToNextInstruction(opCode);
                    break;
                } case CONST_PRI: {
                    pri = getOperand(cip);

                    advanceToNextInstruction(opCode);
                    break;
                } case RETN: {
                    frm = readInt(stk);
                    stk = stk + CELL_SIZE;
                    cip = readInt(stk);
                    stk = stk + CELL_SIZE;
                    stk = stk + readInt(stk) + CELL_SIZE;

                    log.info("RETN called"); //TODO Stack hat 4 zu viel aufgeräumt, ursprung unbekannt
                    return;
                    //advanceToNextInstruction(opCode);
                } case ADDR_ALT: {
                    alt = frm + getOperand(cip);
                    advanceToNextInstruction(opCode);
                    break;
                } case FILL: {
                    for(int i = 0; i < getOperand(cip)/4; i++) {
                        writeInt(amxHeader.getDat() + readInt(alt) + i, readInt(pri));
                    }

                    advanceToNextInstruction(opCode);
                    break;
                } case HEAP: {
                    alt = hea;
                    hea = hea + getOperand(cip);
                    advanceToNextInstruction(opCode);
                    break;
                } case STOR_I: {
                    alt = pri;
                    advanceToNextInstruction(opCode);
                    break;
                } case MOV_PRI: {
                    pri = alt;

                    advanceToNextInstruction(opCode);
                    break;
                } case ADDR_PRI: {
                    pri = frm + getOperand(cip);

                    advanceToNextInstruction(opCode);
                    break;
                }
            }
        }
    }

    private int readRef(int refAddress) {
        int address = readInt(refAddress);
        return readInt(address);
    }

    private void writeRef(int refAddress, int value) {
        int address = readInt(refAddress);
        ByteBuffer.wrap(fileContent).asIntBuffer().put(address, value);
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

    private void advanceToNextInstruction(OpCode current) {
        int bytesToMove = current.operandSize;
        bytesToMove += CELL_SIZE;
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

            recordEntries.add(new PublicTableEntry(i, address, readAnsiString(nameAddress)));
        }

        return recordEntries;
    }

    public List<NativeTableEntry> getNativeTable() {
        List<NativeTableEntry> recordEntries = new ArrayList<>();

        int nativeCount = getNativeCount();

        for(int i = getPublicCount(); i < nativeCount + getPublicCount(); i++) { //TODO: seems to work, but is definitely not the intended way
            int nameAddress = readInt(NATIVE_TABLE_OFFSET + (i * 8));

            recordEntries.add(new NativeTableEntry(i - getPublicCount(), readAnsiString(nameAddress)));
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

    public String readAnsiString(int offset) {
        String result = "";

        while(true) {
            if(offset < 0 || offset >= fileContent.length) {
                break;
            }

            byte b = readByte(offset);

            if(b != 0) {
                offset += 1;

                result = result + (char)b;
            } else {
                break;
            }
        }

        result = result.replace("\n", "\\n");

        return result;
    }

    public String readString(int offset) {
        String result = "";

        if(offset < amxHeader.getDat()) {
            offset += amxHeader.getDat();
        }

        while(true) {
            if(offset < 0 || offset >= fileContent.length) {
                break;
            }

            byte b = readByte(offset);

            if(b != 0) {
                offset += CELL_SIZE;

                result = result + (char)b;
            } else {
                break;
            }
        }

        result = result.replace("\n", "\\n");

        return result;
    }
}
