package sh.insane.pawn.interop.builtin;

import sh.insane.pawn.ExecutionContext;
import sh.insane.pawn.interop.NativeCallback;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Random implements NativeCallback {

    @Override
    public int call(ExecutionContext executionContext, List<Integer> callArguments) {
        return ThreadLocalRandom.current().nextInt(0, callArguments.get(0));
    }
}
