package sh.insane.pawn;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Header {
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
