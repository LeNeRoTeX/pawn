package sh.insane.pawn.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class PublicTableEntry {
    private int id;
    private int address;
    private String name;
}
