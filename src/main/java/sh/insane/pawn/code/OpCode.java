package sh.insane.pawn.code;

public enum OpCode {
    LOAD_PRI(1, 4),
    LOAD_ALT(2, 4),
    LOAD_S_PRI(3, 4),
    LOAD_S_ALT(4, 4),
    CONST_PRI(11, 4),
    CONST_ALT(12, 4),
    ADDR_PRI(13, 4),
    ADDR_ALT(14, 4),
    STOR_S_PRI(17, 4),
    STOR_I(23),
    MOV_PRI(33),
    PUSH_PRI(36),
    PUSH_ALT(37),
    PUSH_C(39, 4),
    POP_ALT(43),
    STACK(44, 4),
    HEAP(45, 4),
    PROC(46),
    RETN(48),
    ADD(78),
    SUB_ALT(80),
    ZERO_PRI(89),
    INC_S(110, 4),
    DEC_S(115, 4),
    FILL(119, 4),
    HALT(120, 4),
    SYSREQ_C(123, 4),
    PUSH_ADDR(133, 4),
    BREAK(137);

    public final int instruction;
    public final int operandSize;

    OpCode(int instruction, int operandSize) {
        this.instruction = instruction;
        this.operandSize = operandSize;
    }

    OpCode(int instruction) {
        this(instruction, 0);
    }

    public static OpCode getFromInstruction(int instruction) {
        for(OpCode opcode : OpCode.values()) {
            if(opcode.instruction == instruction) {
                return opcode;
            }
        }

        return null;
    }
}
