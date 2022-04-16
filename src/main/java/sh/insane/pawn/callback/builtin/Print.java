package sh.insane.pawn.callback.builtin;

import lombok.extern.log4j.Log4j2;
import sh.insane.pawn.ExecutionContext;
import sh.insane.pawn.callback.NativeCallback;

import java.util.List;

@Log4j2
public class Print implements NativeCallback {

    @Override
    public int call(ExecutionContext executionContext, List<Integer> callArguments) {
        log.info("Arg: " + callArguments.get(0));

        log.info("print called with string '{}'", executionContext.readString(callArguments.get(0)));
        return 0;
    }
}
