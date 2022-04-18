package sh.insane.pawn.interop.builtin;

import lombok.extern.log4j.Log4j2;
import sh.insane.pawn.ExecutionContext;
import sh.insane.pawn.interop.NativeCallback;

import java.time.LocalTime;
import java.util.List;

@Log4j2
public class GetTime implements NativeCallback {

    @Override
    public int call(ExecutionContext executionContext, List<Integer> callArguments) {
        LocalTime localTime = LocalTime.now();

        executionContext.writeInt(callArguments.get(0), localTime.getHour());
        executionContext.writeInt(callArguments.get(1), localTime.getMinute());
        executionContext.writeInt(callArguments.get(2), localTime.getSecond());
        return 0;
    }
}
