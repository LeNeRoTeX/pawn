package sh.insane.pawn.callback.builtin;

import sh.insane.pawn.ExecutionContext;
import sh.insane.pawn.callback.NativeCallback;

import java.util.List;

public class GetTickCount implements NativeCallback {

    @Override
    public int call(ExecutionContext executionContext, List<Integer> callArguments) {
        return 1337;
    }
}
