package sh.insane.pawn;

public class AmxRuntimeException extends RuntimeException {
    public AmxRuntimeException(String message) {
        super(message);
    }

    public AmxRuntimeException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
