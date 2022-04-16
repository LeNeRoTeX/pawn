package sh.insane.pawn.extension;

import lombok.extern.log4j.Log4j2;
import sh.insane.pawn.AmxRuntimeException;
import sh.insane.pawn.callback.NativeCallback;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Log4j2
public class AmxContext {
    private final Map<String, NativeCallback> natives;

    public AmxContext() {
        natives = new HashMap<>();
    }

    public boolean isNativeExisting(String name) {
        return natives.containsKey(name);
    }

    public void addNative(String name, NativeCallback nativeCallback) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(nativeCallback);

        if(isNativeExisting(name)) {
            throw new AmxRuntimeException(String.format("native with name %s already registered", name));
        }

        natives.put(name, nativeCallback);

        log.debug("registered native with name {}", name);
    }

    public Map<String, NativeCallback> getNatives() {
        return Collections.unmodifiableMap(natives);
    }
}
