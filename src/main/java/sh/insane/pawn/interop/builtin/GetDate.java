package sh.insane.pawn.interop.builtin;

import sh.insane.pawn.ExecutionContext;
import sh.insane.pawn.interop.NativeCallback;

import java.time.LocalDate;
import java.util.List;

public class GetDate implements NativeCallback {

    @Override
    public int call(ExecutionContext executionContext, List<Integer> callArguments) {
        LocalDate localDate = LocalDate.now();

        executionContext.writeInt(callArguments.get(0), localDate.getDayOfMonth());
        executionContext.writeInt(callArguments.get(1), localDate.getMonthValue());
        executionContext.writeInt(callArguments.get(2), localDate.getYear());
        return 0;
    }
}
