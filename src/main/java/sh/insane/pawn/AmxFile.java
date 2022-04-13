package sh.insane.pawn;

import lombok.SneakyThrows;

import javax.naming.NameAlreadyBoundException;
import javax.print.attribute.standard.MediaSize;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class AmxFile {
    private static final int PUBLIC_TABLE_OFFSET = 56;
    private static final int NATIVE_TABLE_OFFSET = 60;

    private final byte[] fileContent;
    private final Header header;
    private final AmxRuntime amxRuntime;

    @SneakyThrows
    public AmxFile(String resource) {
        fileContent = Files.readAllBytes(Paths.get(this.getClass().getClassLoader().getResource(resource).toURI()));

        header = readHeader();

        amxRuntime = new AmxRuntime(header, this);
    }

    public Header getHeader() {
        return header;
    }

    public RecordEntry getPublic(int publicId) {
        int offset = readInt(readInt(PUBLIC_TABLE_OFFSET) + publicId + 4);

        return RecordEntry.builder()
                        .variable(readInt(offset))
                        .offset(readInt(offset + 4))
                        .build();
    }

    private Header readHeader() {
        return Header.builder()
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
        return (getHeader().getNatives() - getHeader().getPublics()) / getHeader().getDefSize();
    }

    public int getNativeCount() {
        return (getHeader().getLibraries() - getHeader().getNatives()) / getHeader().getDefSize();
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
