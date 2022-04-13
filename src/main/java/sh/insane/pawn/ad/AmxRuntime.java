package sh.insane.pawn.ad;

import lombok.Getter;
import lombok.SneakyThrows;
import sh.insane.pawn.Header;
import sh.insane.pawn.NativeTableEntry;
import sh.insane.pawn.PublicTableEntry;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class AmxRuntime {
    private static final int PUBLIC_TABLE_OFFSET = 56;
    private static final int NATIVE_TABLE_OFFSET = 60;

    @Getter
    private final AmxHeader amxHeader;
    private final byte[] fileContent;

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
        fileContent = Files.readAllBytes(Paths.get(this.getClass().getClassLoader().getResource(path).toURI()));

        amxHeader = readAmxHeader();
    }

    public void executeMain() {
        if(!getAmxHeader().hasMainFunction()) {
            return;
        }

        hea = getAmxHeader().getHea();
        stp = getAmxHeader().getStp();
        cip = getAmxHeader().getCip() + amxHeader.getCod();

        stk = stp;

        System.out.println("C " + cip);

        execute(cip);
    }

    private void writeDat(int address, int value) {
        writeInt(readInt(getAmxHeader().getDat() + address), value);
    }

    private int nextAndGet() {
        cip += 4;
        int value = readInt(cip);
        return value;
    }

    private void execute(int offset) {
        cip = offset;

        while(true) {
            int opcode = readByte(cip);

            System.out.println("OP: " + opcode + ", " + cip);

            if(opcode == 524292) {
                return;
            }

            /* STK = STK - cell size, [STK] = FRM, FRM = STK */

            switch(opcode) {
                case 11: {
                    //CONST.pri
                    int value = nextAndGet();
                    pri = value;
                    next();
                }
                case 14: {
                    //14 	ADDR.alt 	offset 	ALT = FRM + offset
                    int value = nextAndGet();
                    alt = frm + value;
                }
                case 23: {
                    //23 	STOR.I 		[ALT] = PRI (full cell)
                    writeInt(alt, pri);
                    next();
                }
                case 37: {
                    //37 	PUSH.alt 		STK = STK - cell size, [STK] = ALT
                    stk = stk - 4;
                    writeInt(stk, alt);
                    next();
                }
                case 39: {
                    //PUSH.C 	value 	STK = STK - cell size, [STK] = value
                    int value = nextAndGet();
                    stk = stk - 4;
                    writeInt(readInt(stk), value);
                    next();
                }
                case 44: {
                    //44 	STACK 	value 	ALT = STK, STK = STK + value
                    alt = stk;
                    int value = nextAndGet();
                    stk = stk + value;
                    next();
                }
                case 45: {
                    //45 	HEAP 	value 	ALT = HEA, HEA = HEA + value
                    alt = hea;
                    int value = nextAndGet();
                    hea = hea + value;
                    next();
                }
                case 46: {
                    //PROC
                    stk = stk - 4;
                    writeInt(stk, frm);
                    frm = stk;
                    next();
                }
                case 48: {
                    //48 	RETN 		FRM = [STK], STK = STK + cell size, CIP = [STK],
                    // STK = STK + cell size, STK = STK + [STK] + cell size
                    int value = readInt(amxHeader.getDat() + stk);
                    frm = value;
                    stk = stk + 4;
                    cip = readInt(amxHeader.getDat() + stk);
                    stk = stk + 4;
                    stk = stk + readInt(amxHeader.getDat() + stk) + 4;
                    next();
                }
                case 89: {
                    //ZERO.pri
                    pri = 0;
                    next();
                }
                case 119: {
                    //119 	FILL 	number 	fill 'number' bytes of memory from [ALT]
                    // with value in PRI (number must be multiple of cell size)

                    int value = nextAndGet();

                    int altOff = readInt(amxHeader.getDat() + alt);

                    for(int i = 0; i < value; i++) {
                        ByteBuffer.wrap(fileContent).order(ByteOrder.LITTLE_ENDIAN).putInt(amxHeader.getDat() + i * 4, altOff);
                    }

                    next();
                }
                case 120: {
                    //HALT
                    next();
                    System.out.println("HALT");
                    return;
                }
                case 123: {
                    //123 	SYSREQ.C 	value 	call system service
                    int value = nextAndGet();
                    System.out.println("SYSREQ.C call");
                    next();
                }
                case 133: {
                   //133 	PUSH.ADR 	offset 	STK = STK - cell size, [STK] = FRM + offset
                    stk = stk - 4;
                    int value = nextAndGet();
                    writeInt(stk, frm + value);
                }
                case 137: {
                    //BREAK
                    next();
                }
            }
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
        System.out.println("READ " + offset);
        return ByteBuffer.wrap(fileContent, offset, 4).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get();
    }

    public short readShort(int offset) {
        return ByteBuffer.wrap(fileContent, offset, 2).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get();
    }

    public void writeInt(int offset, int value) {
        System.out.println("WRITE " + offset);
        ByteBuffer.wrap(fileContent).order(ByteOrder.LITTLE_ENDIAN).putInt(offset, value);
    }

    public String readString(int offset) {
        String result = "";

        while(true) {
            byte b = readByte(offset);

            if(b != 0) {
                offset += 1;

                result = result + (char)b;
            } else {
                break;
            }
        }

        return result;
    }
}
