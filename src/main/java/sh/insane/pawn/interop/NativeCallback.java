package sh.insane.pawn.interop;

import sh.insane.pawn.ExecutionContext;

import java.util.List;

public interface NativeCallback {
    int call(ExecutionContext executionContext, List<Integer> callArguments);
}