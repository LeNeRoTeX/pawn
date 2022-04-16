package sh.insane.pawn;

import lombok.AllArgsConstructor;

import java.util.function.Function;

@AllArgsConstructor
public class ExecutionContext {
    private Function<Integer, String> readStringFn;

    public String readString(int address) {
        return readStringFn.apply(address);
    }
}
