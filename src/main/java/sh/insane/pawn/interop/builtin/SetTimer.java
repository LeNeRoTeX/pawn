package sh.insane.pawn.interop.builtin;

import lombok.extern.log4j.Log4j2;
import sh.insane.pawn.ExecutionContext;
import sh.insane.pawn.interop.NativeCallback;

import java.util.List;

@Log4j2
public class SetTimer implements NativeCallback {

    @Override
    public int call(ExecutionContext executionContext, List<Integer> callArguments) {
        String timerName = executionContext.readString(callArguments.get(0));
        int interval = callArguments.get(1);
        int repeating = callArguments.get(2);

        log.info("SetTimer called '{}' '{}' '{}'", timerName, interval, repeating);
        return 0;
    }
}