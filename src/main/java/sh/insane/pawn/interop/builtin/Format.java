package sh.insane.pawn.interop.builtin;

import lombok.extern.log4j.Log4j2;
import sh.insane.pawn.ExecutionContext;
import sh.insane.pawn.interop.NativeCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
public class Format implements NativeCallback {

    enum Spec {
        ESCAPE("%"),
        INTEGER("d"),
        STRING("s");

        private String identifier;

        Spec(String identifier) {
            this.identifier = identifier;
        }
    }

    private Map<Integer, Spec> inspect(String formatText) {
        Map<Integer, Spec> map = new HashMap<>();

        for(int i = 0; i < formatText.length() - 1; i++) {
            if(formatText.charAt(i) != '%') {
                continue;
            }

            char c = formatText.charAt(i+1);

            for(Spec spec : Spec.values()) {
                if(spec.identifier.charAt(0) == c) {
                    map.put(i, spec);
                    i++;
                }
            }
        }

        return map;
    }

    @Override
    public int call(ExecutionContext executionContext, List<Integer> callArguments) {
        int targetAddress = callArguments.get(0);
        int size = callArguments.get(1);
        String text = executionContext.readString(callArguments.get(2));
        String formatted = text;

        Map<Integer, Spec> specs = inspect(text);

        int count = callArguments.size();

        for(int i = text.length() - 1; i >= 0; i--) {
            if(!specs.containsKey(i)) {
                continue;
            }

            Spec spec = specs.get(i);

            if(spec.equals(Spec.ESCAPE)) {
                continue;
            }

            String prev = formatted.substring(0, i);
            String next = formatted.substring(i + 2);

            String value = "";

            if (spec.equals(Spec.INTEGER)) {
                value = String.valueOf(executionContext.readInt(callArguments.get(count - 1)));
            } else if(spec.equals(Spec.STRING)) {
                value = executionContext.readString(callArguments.get(count - 1));
            }

            formatted = prev + value + next;

            count--;
        }

        if(formatted.length() > size - 1) {
            formatted = formatted.substring(0, size - 1);
        }

        for(int i = 0; i < size; i++) {
            int toPut = 0;

            if(i < formatted.length()) {
                toPut = formatted.charAt(i);
            }

            executionContext.writeInt(targetAddress + i * 4, toPut);
        }

        executionContext.writeInt(targetAddress + 4 * size, 0);
        return 0;
    }
}
