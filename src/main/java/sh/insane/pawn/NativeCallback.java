package sh.insane.pawn;

import java.util.List;

//TODO register native with name and NativeCallback
public interface NativeCallback {
    int execute(Execution execution, List<Integer> callArguments);
}