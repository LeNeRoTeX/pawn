package sh.insane.pawn.callback.builtin;

import lombok.extern.log4j.Log4j2;
import sh.insane.pawn.ExecutionContext;
import sh.insane.pawn.callback.NativeCallback;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class Format implements NativeCallback {

    @Override
    public int call(ExecutionContext executionContext, List<Integer> callArguments) {
        int targetAddress = callArguments.get(0);
        int size = callArguments.get(1);
        String text = executionContext.readString(callArguments.get(2));

        String result = String.format(text, callArguments.stream().skip(3).collect(Collectors.toList()).toArray());

        if(result.length() > size - 1) {
            result = result.substring(0, size - 1);
        }
        for(int i = 0; i < size; i++) {

            int toPut = 0;

            if(i < result.length()) {
                toPut = result.charAt(i);
            }

            executionContext.writeInt(targetAddress + i * 4, toPut);
        }

        executionContext.writeInt(targetAddress + 4 * size, 0);

        log.info("Val: " + result);
        log.info("Val2: " + executionContext.readString(targetAddress));

        log.info("Format called {} {} {}", targetAddress, size, text);
        return 0;
    }
}
