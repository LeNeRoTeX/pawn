package sh.insane.pawn;

import java.util.List;

//TODO register native with name and NativeCallback
public interface NativeCallback {
    int call(ExecutionContext executionContext, List<Integer> callArguments);
}