package sh.insane.pawn;

import lombok.extern.log4j.Log4j2;
import sh.insane.pawn.code.AmxHeader;
import sh.insane.pawn.code.NativeTableEntry;
import sh.insane.pawn.code.OpCode;
import sh.insane.pawn.code.PublicTableEntry;
import sh.insane.pawn.interop.AmxContext;
import sh.insane.pawn.interop.NativeCallback;

import java.util.*;

@Log4j2
public class ScriptRuntime {
    private final AmxHeader amxHeader;
    private final AmxContext amxContext;
    private final byte[] scriptBytes;

    private int pri;
    private int alt;
    private int cip;
    private int stp;
    private int stk;
    private int frm;
    private int hea;

    public ScriptRuntime(AmxHeader amxHeader, AmxContext amxContext, byte[] scriptBytes) {
        this.amxHeader = amxHeader;
        this.amxContext = amxContext;
        this.scriptBytes = scriptBytes;
    }

    private AmxHeader getAmxHeader() {
        return amxHeader;
    }

    private void resetRegisters() {
        hea = getAmxHeader().getHea();
        stp = getAmxHeader().getStp();
        cip = getAmxHeader().getCip();

        pri = 0;
        alt = 0;

        stk = stp;
        stk -= 8;

        frm = stk;
    }

    private OpCode readInstruction(int fromCip) {
        int instruction = ByteUtils.readByte(scriptBytes, amxHeader.getCod() + fromCip) & 0XFF;

        OpCode opCode = OpCode.getFromInstruction(instruction);

        if(opCode == null) {
            log.fatal("Could not get opcode from cip {} for instruction {}", cip, instruction);
        }

        return opCode;
    }

    private void advanceToNextInstruction(OpCode current) {
        int bytesToMove = current.operandSize;
        bytesToMove += 4;
        cip += bytesToMove;
    }

    public int getOperand(int instructionCip) {
        return ByteUtils.readInt(scriptBytes,amxHeader.getCod() + instructionCip + 4);
    }

    public int executePublic(String publicName) {
        for(PublicTableEntry publicTableEntry : getPublics()) {
            if(publicTableEntry.getName().equals(publicName)) {
                execute(publicTableEntry.getAddress());
                return pri;
            }
        }

        //TODO throw function not found
        return 0;
    }

    public void execute(int address) {
        resetRegisters();

        cip = address;

        while(true && cip + amxHeader.getCod() < amxHeader.getDat()) {
            OpCode opCode = readInstruction(cip);

            if(opCode == null) {
                break;
            }

            switch(opCode) {
                case PROC: {
                    ByteUtils.writeInt(scriptBytes, stk, frm);
                    stk = stk - 4;
                    frm = stk;

                    advanceToNextInstruction(opCode);
                    break;
                }
                case BREAK: {
                    advanceToNextInstruction(opCode);
                    break;
                }
                case ZERO_PRI: {
                    pri = 0;

                    advanceToNextInstruction(opCode);
                    break;
                }
                case PUSH_PRI: {
                    stk = stk - 4;
                    ByteUtils.writeInt(scriptBytes, stk, pri);

                    advanceToNextInstruction(opCode);
                    break;
                } case PUSH_C: {
                    stk = stk - 4;
                    ByteUtils.writeInt(scriptBytes, stk, getOperand(cip));

                    advanceToNextInstruction(opCode);
                    break;
                } case SYSREQ_C: {
                    NativeTableEntry nativeTableEntry = getNative(getOperand(cip));
                    NativeCallback nativeCallback = amxContext.getNatives().get(nativeTableEntry.getName());

                    int argumentCount = ByteUtils.readInt(scriptBytes, stk) / 4;

                    List<Integer> callArguments;

                    if(argumentCount > 0) {
                        callArguments = new ArrayList<>();

                        if(argumentCount > 0) {
                            for(int i = 0; i < argumentCount; i++) {
                                callArguments.add(ByteUtils.readInt(scriptBytes, stk + 4 + i * 4));
                            }
                        }
                    } else {
                        callArguments = Collections.emptyList();
                    }

                    pri = nativeCallback.call(new ExecutionContext((x, value) -> ByteUtils.writeInt(scriptBytes, x, value), (x) -> readString(x), (x) -> ByteUtils.readInt(scriptBytes, x) , amxHeader), callArguments);

                    advanceToNextInstruction(opCode);
                    break;
                } case STACK: {
                    alt = stk;
                    stk = stk + getOperand(cip);

                    advanceToNextInstruction(opCode);
                    break;
                } case CONST_PRI: {
                    pri = getOperand(cip);

                    advanceToNextInstruction(opCode);
                    break;
                } case RETN: {
                    frm = ByteUtils.readInt(scriptBytes, stk);
                    stk = stk + 4;
                    cip = ByteUtils.readInt(scriptBytes, stk);
                    stk = stk + 4;
                    stk = stk + ByteUtils.readInt(scriptBytes, stk) + 4;

                    log.info("RETN called"); //TODO Stack hat 4 zu viel aufgeräumt, ursprung unbekannt
                    return;
                    //advanceToNextInstruction(opCode);
                } case ADDR_ALT: {
                    alt = frm + getOperand(cip);
                    advanceToNextInstruction(opCode);
                    break;
                } case FILL: {
                    for(int i = 0; i < getOperand(cip)/4; i++) {
                        ByteUtils.writeInt(scriptBytes,amxHeader.getDat() + ByteUtils.readInt(scriptBytes, alt) + i, ByteUtils.readInt(scriptBytes, pri));
                    }

                    advanceToNextInstruction(opCode);
                    break;
                } case HEAP: {
                    alt = hea;
                    hea = hea + getOperand(cip);
                    advanceToNextInstruction(opCode);
                    break;
                } case STOR_I: {
                    alt = pri;
                    advanceToNextInstruction(opCode);
                    break;
                } case MOV_PRI: {
                    pri = alt;

                    advanceToNextInstruction(opCode);
                    break;
                } case ADDR_PRI: {
                    pri = frm + getOperand(cip);

                    advanceToNextInstruction(opCode);
                    break;
                }
            }
        }
    }

    private int getPublicCount() {
        return (getAmxHeader().getNatives() - getAmxHeader().getPublics()) / getAmxHeader().getDefSize();
    }

    private int getNativeCount() {
        return (getAmxHeader().getLibraries() - getAmxHeader().getNatives()) / getAmxHeader().getDefSize();
    }

    public Set<PublicTableEntry> getPublics() {
        Set<PublicTableEntry> recordEntries = new HashSet<>();

        int publicCount = getPublicCount();

        for(int i = 0; i < publicCount; i++) {
            int address = ByteUtils.readInt(scriptBytes,AmxHeader.PUBLIC_TABLE_OFFSET + (i * 8));
            int nameAddress = ByteUtils.readInt(scriptBytes,AmxHeader.PUBLIC_TABLE_OFFSET + 4 + (i * 8));

            recordEntries.add(new PublicTableEntry(i, address, ByteUtils.readAnsiString(scriptBytes, nameAddress)));
        }

        return Collections.unmodifiableSet(recordEntries);
    }

    public Set<NativeTableEntry> getNatives() {
        Set<NativeTableEntry> recordEntries = new HashSet<>();

        int nativeCount = getNativeCount();

        for(int i = getPublicCount(); i < nativeCount + getPublicCount(); i++) { //TODO: seems to work, but is definitely not the intended way
            int nameAddress = ByteUtils.readInt(scriptBytes,AmxHeader.NATIVE_TABLE_OFFSET + (i * 8));

            recordEntries.add(new NativeTableEntry(i - getPublicCount(), ByteUtils.readAnsiString(scriptBytes, nameAddress)));
        }

        return Collections.unmodifiableSet(recordEntries);
    }

    public NativeTableEntry getNative(int nativeId) {
        return getNatives().stream().filter(n -> n.getId() == nativeId).findFirst().get();
    }

    private String readString(int offset) {
        String result = "";

        if(offset < amxHeader.getDat()) {
            offset += amxHeader.getDat();
        }

        while(true) {
            if(offset < 0 || offset >= scriptBytes.length) {
                break;
            }

            byte b = ByteUtils.readByte(scriptBytes, offset);

            if(b != 0) {
                offset += 4;

                result = result + (char)b;
            } else {
                break;
            }
        }

        return result;
    }
}
