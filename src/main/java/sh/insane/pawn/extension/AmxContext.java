package sh.insane.pawn.extension;

import sh.insane.pawn.NativeCallback;

import java.util.Objects;

public class AmxContext {

    public AmxContext() {
    }

    public void addNative(String name, NativeCallback nativeCallback) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(nativeCallback);
    }
}
