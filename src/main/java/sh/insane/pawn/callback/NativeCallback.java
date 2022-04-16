package sh.insane.pawn.callback;

import sh.insane.pawn.ExecutionContext;

import java.util.List;

//TODO register native with name and NativeCallback
public interface NativeCallback {
    int call(ExecutionContext executionContext, List<Integer> callArguments);
}