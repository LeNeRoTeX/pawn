package sh.insane.pawn.natives;

import lombok.extern.log4j.Log4j2;
import sh.insane.pawn.Execution;
import sh.insane.pawn.NativeCallback;

import java.util.List;

@Log4j2
public class PrintNative implements NativeCallback {

    @Override
    public int execute(Execution execution, List<Integer> callArguments) {
        log.info("print called with string '{}'", execution.readString(callArguments.get(0)));
        return 0;
    }
}
