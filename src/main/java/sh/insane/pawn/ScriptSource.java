package sh.insane.pawn;

public enum ScriptSource {
    BYTES("direct load"),
    FILE("loaded from file");

    private final String description;

    ScriptSource(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
