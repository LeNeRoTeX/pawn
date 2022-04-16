package sh.insane.pawn.callback.builtin;

import sh.insane.pawn.extension.AmxContext;
import sh.insane.pawn.extension.Plugin;

public class BuiltInRuntimePlugin implements Plugin {

    @Override
    public void onPluginLoad(AmxContext amxContext) {
        amxContext.addNative("AddPlayerClass", new AddPlayerClass());
        amxContext.addNative("GetTickCount", new GetTickCount());
        amxContext.addNative("print", new Print());
        amxContext.addNative("SetGameModeText", new SetGameModeText());
        amxContext.addNative("SetTimer", new SetTimer());
    }
}