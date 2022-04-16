package sh.insane.pawn;

import java.util.HashMap;
import java.util.Map;

public class Amx {
    private final Map<String, NativeCallback> nativeCallbacks;

    public Amx() {
        nativeCallbacks = new HashMap<>();
    }
}
