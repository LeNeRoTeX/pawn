package sh.insane.pawn.interop.builtin;

import lombok.extern.log4j.Log4j2;
import sh.insane.pawn.ExecutionContext;
import sh.insane.pawn.interop.NativeCallback;

import java.util.List;

@Log4j2
public class Print implements NativeCallback {

    @Override
    public int call(ExecutionContext executionContext, List<Integer> callArguments) {
        log.info("{}", executionContext.readString(callArguments.get(0)));
        return 0;
    }

    public int test(int x, int y, int z) {
        return 0;
    }
}
