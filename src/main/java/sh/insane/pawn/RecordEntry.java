package sh.insane.pawn;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RecordEntry {
    private int variable;
    private int offset;
}
