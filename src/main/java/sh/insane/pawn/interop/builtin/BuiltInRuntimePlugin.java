package sh.insane.pawn.interop.builtin;

import sh.insane.pawn.interop.AmxContext;
import sh.insane.pawn.interop.Plugin;

public class BuiltInRuntimePlugin implements Plugin {

    /*

    bool:IsPublicDefined(name[])
    CallLocalPublic(name[], argumentFormat[], args...)

    random(max)
    format
    print(text[])

    timestamp()

    GetTime(&hour, &minute, &second)
    GetDate(&day, &month, &year)

    OnScriptInit()
    OnScriptExit()

    =================================
    Atan

    CallLocalFunction
    CallRemoteFunction

    clamp

    Float
    Floatabs
    Floatadd
    Floatcmp
    Floatcos
    Floatdiv
    Floatfract
    Floatlog
    Floatmul
    Floatpower
    Floatround
    Floatsin
    Floatsqroot
    Floatstr
    Floatsub
    Floattan

    funcidx
    getarg
    numargs
    setarg
    getdate
    Gettime
    gettime
    heapspace

    printf

    random

    SetTimer
    SetTimerEx
     */

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
