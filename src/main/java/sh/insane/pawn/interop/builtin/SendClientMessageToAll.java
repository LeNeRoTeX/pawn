package sh.insane.pawn.interop.builtin;

import sh.insane.pawn.ExecutionContext;
import sh.insane.pawn.interop.NativeCallback;

import java.util.List;

public class SendClientMessageToAll implements NativeCallback {

    @Override
    public int call(ExecutionContext executionContext, List<Integer> callArguments) {
        return 0;
    }
}