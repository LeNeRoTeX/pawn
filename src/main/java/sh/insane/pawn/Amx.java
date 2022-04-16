package sh.insane.pawn;

import sh.insane.pawn.callback.NativeCallback;

import java.util.HashMap;
import java.util.Map;

public class Amx {
    private final Map<String, NativeCallback> nativeCallbacks;

    public Amx() {
        nativeCallbacks = new HashMap<>();
    }
}
