package sh.insane.pawn;

public enum AmxError {
    AMX_ERR_NONE(0, ""),
    AMX_ERR_EXIT(1, "forced exit"),
    AMX_ERR_ASSERT(2, "assertion failed"),
    AMX_ERR_STACKERR(3, "stack/heap collision"),
    AMX_ERR_BOUNDS(4, "index out of bounds"),
    AMX_ERR_MEMACCESS(5, "invalid memory access"),
    AMX_ERR_INVINSTR(6, "invalid instruction"),
    AMX_ERR_STACKLOW(7, "stack underflow"),
    AMX_ERR_HEAPLOW(8, "heap underflow"),
    AMX_ERR_CALLBACK(9, "no callback, or invalid callback"),
    AMX_ERR_NATIVE(10, "native function failed"),
    AMX_ERR_DIVIDE(11, "divide by zero"),
    AMX_ERR_SLEEP(12, "go into sleepmode - can can be restarted"),
    AMX_ERR_INVSTATE(13, "no implementation for this state, no fall-back"),

    AMX_ERR_MEMORY(16, "out of memory"),
    AMX_ERR_FORMAT(17, "invalid file format"),
    AMX_ERR_VERSION(18, "file is for a newer version of the AMX"),
    AMX_ERR_NOTFOUND(19, "function not found"),
    AMX_ERR_INDEX(20, "invalid index parameter (bad entry point)"),
    AMX_ERR_DEBUG(21, "debugger cannot run"),
    AMX_ERR_INIT(22, "AMX not initialized (or doubly initialized"),
    AMX_ERR_USERDATA(23, "unable to set user data field (table full)"),
    AMX_ERR_INIT_JIT(24, "cannot initialize the JIT"),
    AMX_ERR_PARAMS(25, "parameter error"),
    AMX_ERR_DOMAIN(26, "domain error, expression result does not fit in range"),
    AMX_ERR_GENEREL(27, "general error (unknown or unspecific error)"),
    AMX_ERR_OVERLAY(28, "overlays are unsupported (JIT) or uninitialized"),

    AMX_ERR_FILE(40, "file could not be loaded");

    private final int id;
    private final String reason;

    AmxError(int id, String reason) {
        this.id = id;
        this.reason = reason;
    }

    public int getId() {
        return id;
    }

    public String getReason() {
        return reason;
    }
}
