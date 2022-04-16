package sh.insane.pawn.builtin;

import sh.insane.pawn.builtin.callback.PrintNative;
import sh.insane.pawn.extension.AmxContext;
import sh.insane.pawn.extension.Plugin;

public class BuiltInRuntimePlugin implements Plugin {

    @Override
    public void onLoad(AmxContext amxContext) {
        amxContext.addNative("print", new PrintNative());
    }
}
