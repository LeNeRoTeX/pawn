package sh.insane.pawn;

import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;

@Log4j2
public class Script {
    private final String fileLocation;
    private final LocalDateTime loadTime;
    private final AmxHeader amxHeader;

    public Script(String fileLocation, byte[] scriptCode) {
        this.fileLocation = fileLocation;
        loadTime = LocalDateTime.now();
        amxHeader = readAmxHeader(scriptCode);
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
}
