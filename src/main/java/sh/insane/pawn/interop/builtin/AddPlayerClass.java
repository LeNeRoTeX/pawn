package sh.insane.pawn.interop.builtin;

import lombok.extern.log4j.Log4j2;
import sh.insane.pawn.ExecutionContext;
import sh.insane.pawn.interop.NativeCallback;

import java.util.List;

@Log4j2
public class AddPlayerClass implements NativeCallback {

    @Override
    public int call(ExecutionContext executionContext, List<Integer> callArguments) {
        //AddPlayerClass(0, 1958.3783, 1343.1572, 15.3746, 269.1425, 0, 0, 0, 0, 0, 0);

        int posX = callArguments.get(1);

        log.info("Float: {}", Float.intBitsToFloat(posX));
        log.info("Float: {}", Float.intBitsToFloat(callArguments.get(2)));
        log.info("Float: {}", Float.intBitsToFloat(callArguments.get(3)));
        return 0;
    }
}
