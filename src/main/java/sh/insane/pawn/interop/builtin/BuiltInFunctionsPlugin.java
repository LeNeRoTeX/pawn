package sh.insane.pawn.interop.builtin;

import sh.insane.pawn.interop.AmxContext;
import sh.insane.pawn.interop.Plugin;

public class BuiltInFunctionsPlugin implements Plugin {

    @Override
    public void onPluginLoad(AmxContext amxContext) {
        amxContext.addNative("random", new Random());
        amxContext.addNative("timestamp", new Timestamp());
        amxContext.addNative("GetTime", new GetTime());
        amxContext.addNative("GetDate", new GetDate());
        amxContext.addNative("strlen", new Strlen());
        amxContext.addNative("print", new Print());
        amxContext.addNative("format", new Format());
    }
}
