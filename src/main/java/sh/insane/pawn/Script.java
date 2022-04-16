package sh.insane.pawn;

import lombok.extern.log4j.Log4j2;
import sh.insane.pawn.code.AmxHeader;
import sh.insane.pawn.code.NativeTableEntry;
import sh.insane.pawn.code.PublicTableEntry;

import java.time.LocalDateTime;
import java.util.*;

@Log4j2
public class Script {
    private final String fileLocation;
    private final LocalDateTime loadTime;
    private final AmxHeader amxHeader;
    private final byte[] scriptCode;

    public Script(String fileLocation, byte[] scriptCode) {
        this.fileLocation = fileLocation;
        loadTime = LocalDateTime.now();
        amxHeader = readAmxHeader(scriptCode);

        this.scriptCode = prepareAndExpand(scriptCode);
    }

    private byte[] prepareAndExpand(byte[] bytes) {
        byte[] expandedBytes = new byte[getAmxHeader().getStp()];
        System.arraycopy(bytes, 0, expandedBytes, 0, expandedBytes.length);
        return expandedBytes;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public AmxHeader getAmxHeader() {
        return amxHeader;
    }

    public LocalDateTime getLoadTime() {
        return loadTime;
    }

    private AmxHeader readAmxHeader(byte[] scriptCode) {
        return AmxHeader.builder()
                .size(ByteUtils.readInt(scriptCode, 0))
                .magic(ByteUtils.readShort(scriptCode, 4))
                .fileVersion(ByteUtils.readByte(scriptCode, 6))
                .amxVersion(ByteUtils.readByte(scriptCode, 7))
                .flags(ByteUtils.readShort(scriptCode, 8))
                .defSize(ByteUtils.readShort(scriptCode, 10))
                .cod(ByteUtils.readInt(scriptCode, 12))
                .dat(ByteUtils.readInt(scriptCode, 16))
                .hea(ByteUtils.readInt(scriptCode, 20))
                .stp(ByteUtils.readInt(scriptCode, 24))
                .cip(ByteUtils.readInt(scriptCode, 28))
                .publics(ByteUtils.readInt(scriptCode, 32))
                .natives(ByteUtils.readInt(scriptCode, 36))
                .libraries(ByteUtils.readInt(scriptCode, 40))
                .pubvars(ByteUtils.readInt(scriptCode, 44))
                .tags(ByteUtils.readInt(scriptCode, 48))
                .nameTable(ByteUtils.readInt(scriptCode, 52))
                .build();
    }

    public AmxError validate() {
        if(getAmxHeader().getMagic() != AmxHeader.MAGIC_32) {
            return AmxError.AMX_ERR_FORMAT;
        }

        if(getAmxHeader().getFileVersion() != 8) {
            return AmxError.AMX_ERR_VERSION;
        }

        return AmxError.AMX_ERR_NONE;
    }

    public void executeMain() {
        if(!getAmxHeader().hasMainFunction()) {
            return;
        }

        //TODO
    }

    private int getPublicCount() {
        return (getAmxHeader().getNatives() - getAmxHeader().getPublics()) / getAmxHeader().getDefSize();
    }

    private int getNativeCount() {
        return (getAmxHeader().getLibraries() - getAmxHeader().getNatives()) / getAmxHeader().getDefSize();
    }

    public Set<PublicTableEntry> getPublics() {
        Set<PublicTableEntry> recordEntries = new HashSet<>();

        int publicCount = getPublicCount();

        for(int i = 0; i < publicCount; i++) {
            int address = ByteUtils.readInt(scriptCode,AmxHeader.PUBLIC_TABLE_OFFSET + (i * 8));
            int nameAddress = ByteUtils.readInt(scriptCode,AmxHeader.PUBLIC_TABLE_OFFSET + 4 + (i * 8));

            recordEntries.add(new PublicTableEntry(i, address, ByteUtils.readAnsiString(scriptCode, nameAddress)));
        }

        return Collections.unmodifiableSet(recordEntries);
    }

    public Set<NativeTableEntry> getNatives() {
        Set<NativeTableEntry> recordEntries = new HashSet<>();

        int nativeCount = getNativeCount();

        for(int i = getPublicCount(); i < nativeCount + getPublicCount(); i++) { //TODO: seems to work, but is definitely not the intended way
            int nameAddress = ByteUtils.readInt(scriptCode,AmxHeader.NATIVE_TABLE_OFFSET + (i * 8));

            recordEntries.add(new NativeTableEntry(i - getPublicCount(), ByteUtils.readAnsiString(scriptCode, nameAddress)));
        }

        return Collections.unmodifiableSet(recordEntries);
    }
}
