package sh.insane.pawn;

import lombok.AllArgsConstructor;

import java.util.function.BiConsumer;
import java.util.function.Function;

@AllArgsConstructor
public class ExecutionContext {
    private BiConsumer<Integer, Integer> writeIntFn;
    private Function<Integer, String> readStringFn;
    private Function<Integer, Integer> readIntFn;
    private final AmxHeader header;

    public String readString(int address) {
        return readStringFn.apply(address);
    }

    public int readInt(int address) {
        return readIntFn.apply(address);
    }

    public void writeInt(int address, int value) {
        writeIntFn.accept(address, value);
    }

    public AmxHeader getHeader() {
        return header;
    }
}
