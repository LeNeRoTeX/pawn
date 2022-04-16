package sh.insane.pawn;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ByteUtils {
    private ByteUtils() {}

    public static byte readByte(byte[] bytes, int offset) {
        return ByteBuffer.wrap(bytes, offset, 1).order(ByteOrder.LITTLE_ENDIAN).get();
    }

    public static int readInt(byte[] bytes, int offset) {
        return ByteBuffer.wrap(bytes, offset, 4).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get();
    }

    public static short readShort(byte[] bytes, int offset) {
        return ByteBuffer.wrap(bytes, offset, 2).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get();
    }

    public static void writeInt(byte[] bytes, int offset, int value) {
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).putInt(offset, value);
    }

    public static String readAnsiString(byte[] bytes, int offset) {
        String result = "";

        while(true) {
            if(offset < 0 || offset >= bytes.length) {
                break;
            }

            byte b = readByte(bytes, offset);

            if(b != 0) {
                offset++;
                result = result + (char)b;
            } else {
                break;
            }
        }

        return result;
    }
}
