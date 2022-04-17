package sh.insane.pawn.interop.builtin;

import sh.insane.pawn.interop.AmxContext;
import sh.insane.pawn.interop.Plugin;

public class BuiltInRuntimePlugin implements Plugin {

    @Override
    public void onPluginLoad(AmxContext amxContext) {
        amxContext.addNative("AddPlayerClass", new AddPlayerClass());
        amxContext.addNative("GetTickCount", new GetTickCount());
        amxContext.addNative("print", new Print());
        amxContext.addNative("SetGameModeText", new SetGameModeText());
        amxContext.addNative("SetTimer", new SetTimer());
        amxContext.addNative("format", new Format());
        amxContext.addNative("SendClientMessageToAll", new SendClientMessageToAll());
    }
}
