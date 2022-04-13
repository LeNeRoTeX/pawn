package sh.insane.pawn;

/*

    Primary Register (PRI): general purpose register (frequently used as an accumulator register)
    Alternate Register (ALT): general purpose register (frequently used as an address register)
    Code Segment Register (COD): absolute address to the start of the code segment in memory
    Data Segment Register (DAT): absolute address to the start of the data segment in memory
    Current Instruction Pointer (CIP): address (relative to the COD register) of the next instruction to be executed
    Stack Top Register (STP): address (relative to the DAT register) to the top of the stack
    Stack Index Register (STK): address (relative to the DAT register) to the current location on the stack
    Frame Pointer Register (FRM): address (relative to the DAT register) of the start of the current function's frame in the stack (explained later)
    Heap Pointer (HEA): address (relative to the DAT register) to the top of the heap
 */

import lombok.Getter;

@Getter
public class AmxRuntime {
    private final Header header;
    private final AmxFile amxFile;

    private int pri;
    private int alt;
    private int cod;
    private int dat;
    private int cip;
    private int stp;
    private int stk;
    private int frm;
    private int hea;

    public AmxRuntime(Header header, AmxFile amxFile) {
        this.header = header;
        this.amxFile = amxFile;
    }

    //Execute main
    public void exec() {
        if(!getHeader().hasMainFunction()) {
            System.out.println("Could not execute main, not found in header");
            return;
        }

        hea = getHeader().getHea();
        stp = getHeader().getStp();
        cip = getHeader().getCip();

        eval(cip);
    }

    private int readDatAddr(int offset) {
        int address = getAmxFile().readInt(offset);
        return amxFile.readInt(getHeader().getDat() + address);
    }

    private void eval(int offset) {

        while(true) {
            if(cip != offset) {
                cip = offset;
            }

            byte opcode = amxFile.readByte(offset);

             /*
             halt
proc
break
push.c
sysreq.c
stack
break
zero.pri
retn
const.pri
addr.alt
fill
heap
stor.i
push.alt
push.adr
              */

            switch(opcode) {
                case 1: { //LOAD.pri
                    offset += 4;
                    pri = readDatAddr(offset);
                    offset += 4;
                    break;
                }
                case 2: { //LOAD.alt
                    offset += 4;
                    alt = readDatAddr(offset);
                    offset += 4;
                    break;
                }
                case 46: { //PROC STK = STK - cell size, [STK] = FRM, FRM = STK
                    stk = stk - 4;
                }
            }
        }
    }

    public void exec(int publicId) {

    }
}
