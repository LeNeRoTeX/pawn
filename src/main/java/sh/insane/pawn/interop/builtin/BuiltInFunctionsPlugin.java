package sh.insane.pawn.interop.builtin;

import sh.insane.pawn.interop.AmxContext;
import sh.insane.pawn.interop.Plugin;

public class BuiltInFunctionsPlugin implements Plugin {

    /*
    bool:IsPublicDefined(name[])
    CallLocalPublic(name[], argumentFormat[], args...)

    GetTime(&hour, &minute, &second)
    GetDate(&day, &month, &year)
     */

    @Override
    public void onPluginLoad(AmxContext amxContext) {
        amxContext.addNative("random", new Random());
        amxContext.addNative("timestamp", new Timestamp());
        amxContext.addNative("GetTime", new GetTime());
        amxContext.addNative("GetDate", new GetDate());
        //amxContext.addNative("print", new Print());
        //amxContext.addNative("format", new Format());
    }
}
