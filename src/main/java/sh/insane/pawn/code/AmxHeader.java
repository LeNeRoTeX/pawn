package sh.insane.pawn.code;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class AmxHeader {
    public static final short MAGIC_32 = (short)0xF1E0;

    public static final int PUBLIC_TABLE_OFFSET = 56;
    public static final int NATIVE_TABLE_OFFSET = 60;

    private int size;
    private short magic;
    private byte fileVersion;
    private byte amxVersion;
    private short flags;
    private short defSize;
    private int cod;
    private int dat;
    private int hea;
    private int stp;
    private int cip;
    private int publics;
    private int natives;
    private int libraries;
    private int pubvars;
    private int tags;
    private int nameTable;

    public boolean hasMainFunction() {
        return cip != -1;
    }

}
