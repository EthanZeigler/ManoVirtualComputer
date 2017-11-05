package com.robcampbell.mano;
/*
   Rob Campbell
	Fall 2000
	CS 497 @ University of Northern Colorado
	Senior Project
	.
	ManoHardware.java
	This is the heart of the mano applet; the emulation of all the
	hardware functionality in the mano specifications.
	.
	Compiled using Sun's Java SDK 1.3.
*/

public class ManoHardware extends Thread {
    // nine registers
    private ManoRegister AR;
    private ManoRegister PC;
    private ManoRegister DR;
    private ManoRegister AC;
    private ManoRegister IR;
    private ManoRegister TR;
    private ManoRegister OUTR;
    private ManoRegister INPR;
    private ManoRegister SC;
    // seven flip-flops
    private ManoFlipFlop I;
    private ManoFlipFlop S;
    private ManoFlipFlop E;
    private ManoFlipFlop R;
    private ManoFlipFlop IEN;
    private ManoFlipFlop FGI;
    private ManoFlipFlop FGO;
    // a memory unit with 4096 words of 16 bits each
    private ManoMemory mainMemory;

    private int decoder;      // a 3 x 8 operation decoder (rarely used)
    private int runSpeed;      // factor to slow down execution
    private String instruction;   // current micro-operation description
    private String comment;      // corresponding code from assembly-language

    private ManoDisplay display;      // what to update
    private ManoMemoryMap memoryMap;   // where the memory map is

    private Mano mainApp;      // used to inform mano of halt (HLT)

    // constructor
    public ManoHardware(Mano app) {
        mainApp = app;

        AR = new ManoRegister(12);
        PC = new ManoRegister(12);
        DR = new ManoRegister(16);
        AC = new ManoRegister(16);
        IR = new ManoRegister(16);
        TR = new ManoRegister(16);
        OUTR = new ManoRegister(8);
        INPR = new ManoRegister(8);
        SC = new ManoRegister(4);
        I = new ManoFlipFlop();
        S = new ManoFlipFlop();
        E = new ManoFlipFlop();
        R = new ManoFlipFlop();
        IEN = new ManoFlipFlop();
        FGI = new ManoFlipFlop();
        FGO = new ManoFlipFlop();
        mainMemory = new ManoMemory();

        // begin thread now
        start();
        // runSpeed setting should correspond with default radio button
        // from mano.java
        runSpeed = 2;
        instruction = new String();
        comment = new String("Welcome to MANO.");   // warm fuzzy
    }

    // display requires hardware, hardware requires display
    // this allows non-null initialization
    public void associateManoDisplay(ManoDisplay md) {
        display = md;
    }

    // memory map requires hardware, hardware requires memory map
    // this allows non-null initialization
    public void associateManoMemoryMap(ManoMemoryMap mm) {
        memoryMap = mm;
    }

    // overload the run() function of Thread
    public void run() {
        while (true) {
            try {
                while (S.get() == true) {
                    manoStep();
                    display.update();
                    sleep((2 - runSpeed) * 400);
                }
                sleep(200);
            } catch (InterruptedException iex) {
            }
        }
    }

    // enable input
    public void inputEnable() {
        FGI.set(true);
    }

    // enable output
    public void outputEnable() {
        FGO.set(true);
    }

    // power up
    public void manoStart() {
        S.set(true);
    }

    // power down
    public void manoStop() {
        S.set(false);
        display.update();
    }

    // one clock pulse's operation
    public void manoStep() {
        step();
        display.update();
    }

    // set the run speed
    public void setRunSpeed(int s) {
        runSpeed = s;
    }

    // reset all the hardware components
    public void reset() {
        AR.reset();
        PC.reset();
        DR.reset();
        AC.reset();
        IR.reset();
        TR.reset();
        OUTR.reset();
        INPR.reset();
        SC.reset();
        I.reset();
        S.reset();
        E.reset();
        R.reset();
        IEN.reset();
        FGI.reset();
        FGO.reset();
        mainMemory.reset();
        instruction = new String();
        comment = new String("Welcome to MANO.");
    }

    // inspectors
    public char getAR() {
        return AR.get();
    }

    public char getPC() {
        return PC.get();
    }

    public char getDR() {
        return DR.get();
    }

    public char getAC() {
        return AC.get();
    }

    public char getIR() {
        return IR.get();
    }

    public char getTR() {
        return TR.get();
    }

    public char getOUTR() {
        return OUTR.get();
    }

    public char getINPR() {
        return INPR.get();
    }

    public char getSC() {
        return SC.get();
    }

    public boolean getI() {
        return I.get();
    }

    public boolean getS() {
        return S.get();
    }

    public boolean getE() {
        return E.get();
    }

    public boolean getR() {
        return R.get();
    }

    public boolean getIEN() {
        return IEN.get();
    }

    public boolean getFGI() {
        return FGI.get();
    }

    public boolean getFGO() {
        return FGO.get();
    }

    // return the mano "word" at the specified address
    public char getDataAtAddress(char a) {
        return mainMemory.get(a);
    }

    // return the current instruction (micro-operation description)
    public String getInstruction() {
        return new String(instruction);
    }

    // return the current comment (assembly code)
    public String getComment() {
        return new String(comment);
    }

    // reset hardware, load memory map, and prepare for run
    public void initialize(ManoMemoryMap mm) {
        reset();
        mainMemory.loadMemoryMap(mm);
        instruction = new String();
        comment = new String("mano code assembly successful.");
        PC.set(mm.getAddressAtIndex(0));
    }

    // execution version of one clock pulse
    private void step() {
        if (R.get() == false) {   // instruction cycle
            switch (SC.get()) {   // get timing
                case (char) (0):
                    display.select(PC.get());
                    comment = new String(memoryMap.getCommentAtIndex(memoryMap.getIndexOf(PC.get())));
                    instruction = new String("AR<-PC");
                    AR.set(PC.get());
                    SC.inc();
                    break;
                case (char) (1):
                    instruction = new String("IR<-M[AR], PC<-PC+1");
                    IR.set(mainMemory.get(AR.get()));
                    PC.set((char) (PC.get() + (char) (1)));
                    SC.inc();
                    break;
                case (char) (2):
                    instruction = new String("AR<-IR(0-11), I<-IR(15),\nD(0:7)<-Decode IR(12-14)");
                    AR.set(IR.get());
                    I.set(IR.getBit(15));
                    decode1214();
                    SC.inc();
                    break;
                case (char) (3):
                    if ((IEN.get() == true) && ((FGI.get() == true) || (FGO.get() == true))) {
                        R.set(true);
                    }
                    if (decoder != 7) {   // memory reference
                        if (I.get() == false) {   // direct
                            instruction = new String("(do nothing)");
                            SC.inc();
                        } else {   // indirect
                            instruction = new String("AR<-M[AR]");
                            AR.set(mainMemory.get(AR.get()));
                            SC.inc();
                        }
                    } else {   // register or I/O
                        if (I.get() == false) {   // register
                            if (IR.getBit(11) == true) {   // CLA
                                instruction = new String("AC<-0, SC<-0");
                                AC.set((char) (0));
                                SC.reset();
                            } else if (IR.getBit(10) == true) {   // CLE
                                instruction = new String("E<-0, SC<-0");
                                E.set(false);
                                SC.reset();
                            } else if (IR.getBit(9) == true) {   // CMA
                                instruction = new String("AC<-AC', SC<-0");
                                AC.complement();
                                SC.reset();
                            } else if (IR.getBit(8) == true) {   // CME
                                instruction = new String("E<-E', SC<-0");
                                E.toggle();
                                SC.reset();
                            } else if (IR.getBit(7) == true) {   // CIR
                                instruction = new String("AC<-shr AC, AC(15)<-E,\nE<-AC(0), SC<-0");
                                if (E.get() == false) {
                                    E.set(AC.getBit(0));
                                    AC.shiftRight16(false);
                                    SC.reset();
                                } else {
                                    E.set(AC.getBit(0));
                                    AC.shiftRight16(true);
                                    SC.reset();
                                }
                            } else if (IR.getBit(6) == true) {   // CIL
                                instruction = new String("AC<-shl AC, AC(0)<-E,\nE<-AC(15), SC<-0");
                                if (E.get() == false) {
                                    E.set(AC.getBit(15));
                                    AC.shiftLeft16(false);
                                    SC.reset();
                                } else {
                                    E.set(AC.getBit(15));
                                    AC.shiftLeft16(true);
                                    SC.reset();
                                }
                            } else if (IR.getBit(5) == true) {   // INC
                                instruction = new String("AC<-AC+1, SC<-0");
                                AC.inc();
                                SC.reset();
                            } else if (IR.getBit(4) == true) {   // SPA
                                instruction = new String("if (AC(15)=0) then (PC<-PC+1),\nSC<-0");
                                if (AC.getBit(15) == false) {
                                    PC.inc();
                                }
                                SC.reset();
                            } else if (IR.getBit(3) == true) {   // SNA
                                instruction = new String("if (AC(15)=1) then (PC<-PC+1),\nSC<-0");
                                if (AC.getBit(15) == true) {
                                    PC.inc();
                                }
                                SC.reset();
                            } else if (IR.getBit(2) == true) {   // SZA
                                instruction = new String("if (AC=0) then (PC<-PC+1),\nSC<-0");
                                if (AC.get() == (char) (0)) {
                                    PC.inc();
                                }
                                SC.reset();
                            } else if (IR.getBit(1) == true) {   // SZE
                                instruction = new String("if (E=0) then (PC<-PC+1),\nSC<-0");
                                if (E.get() == false) {
                                    PC.inc();
                                }
                                SC.reset();
                            } else if (IR.getBit(0) == true) {   // HLT
                                instruction = new String("S<-0");
                                S.set(false);
                                // inform the applet that execution is halted
                                mainApp.informStop();
                            }
                        } else {   // I/O
                            if (IR.getBit(11) == true) {   // INP
                                instruction = new String("AC(0-7)<-INPR, FGI<-0,\nSC<-0");
                                INPR.set(display.getInput());
                                AC.set(INPR.get());
                                FGI.reset();
                                SC.reset();
                            } else if (IR.getBit(10) == true) {   // OUT
                                instruction = new String("OUTR<-AC(0-7), FGO<-0, SC<-0");
                                OUTR.set(AC.get());
                                FGO.reset();
                                SC.reset();
                            } else if (IR.getBit(9) == true) {   // SKI
                                instruction = new String("if (FGI=1) then (PC<-PC+1),\nSC<-0");
                                if (FGI.get() == true) {
                                    PC.inc();
                                }
                                SC.reset();
                            } else if (IR.getBit(8) == true) {   // SKO
                                instruction = new String("if (FGO=1) then (PC<-PC+1),\nSC<-0");
                                if (FGO.get() == true) {
                                    PC.inc();
                                }
                                SC.reset();
                            } else if (IR.getBit(7) == true) {   // ION
                                instruction = new String("IEN<-1, SC<-0");
                                IEN.set(true);
                                SC.reset();
                            } else if (IR.getBit(6) == true) {   // IOF
                                instruction = new String("IEN<-0, SC<-0");
                                IEN.set(false);
                                SC.reset();
                            }
                        }
                    }
                    break;
                case (char) (4):
                    if ((IEN.get() == true) && ((FGI.get() == true) || (FGO.get() == true))) {
                        R.set(true);
                    }
                    // execute memory reference instruction
                    if (decoder == 0) {   // AND
                        instruction = new String("DR<-M[AR]");
                        DR.set(mainMemory.get(AR.get()));
                        SC.inc();
                    } else if (decoder == 1) {   // ADD
                        instruction = new String("DR<-M[AR]");
                        DR.set(mainMemory.get(AR.get()));
                        SC.inc();
                    } else if (decoder == 2) {   // LDA
                        instruction = new String("DR<-M[AR]");
                        DR.set(mainMemory.get(AR.get()));
                        SC.inc();
                    } else if (decoder == 3) {   // STA
                        instruction = new String("M[AR]<-AC, SC<-0");
                        mainMemory.set(AR.get(), AC.get());
                        display.adjust(AR.get());
                        SC.reset();
                    } else if (decoder == 4) {   // BUN
                        instruction = new String("PC<-AR, SC<-0");
                        PC.set(AR.get());
                        SC.reset();
                    } else if (decoder == 5) {   // BSA
                        instruction = new String("M[AR]<-PC, AR<-AR+1");
                        mainMemory.set(AR.get(), PC.get());
                        display.adjust(AR.get());
                        AR.inc();
                        SC.inc();
                    } else if (decoder == 6) {   // ISZ
                        instruction = new String("DR<-M[AR]");
                        DR.set(mainMemory.get(AR.get()));
                        SC.inc();
                    }
                    break;
                case (char) (5):
                    if ((IEN.get() == true) && ((FGI.get() == true) || (FGO.get() == true))) {
                        R.set(true);
                    }
                    if (decoder == 0) {   // AND
                        instruction = new String("AC<-AC^DR, SC<-0");
                        AC.and(DR.get());
                        SC.reset();
                    } else if (decoder == 1) {   // ADD
                        instruction = new String("AC<-AC+DR, SC<-0");
                        E.set(AC.add(DR.get()));
                        SC.reset();
                    } else if (decoder == 2) {   // LDA
                        instruction = new String("AC<-DR, SC<-0");
                        AC.set(DR.get());
                        SC.reset();
                    } else if (decoder == 5) {   // BSA
                        instruction = new String("PC<-AR, SC<-0");
                        PC.set(AR.get());
                        SC.reset();
                    } else if (decoder == 6) {   // ISZ
                        instruction = new String("DR<-DR+1");
                        DR.inc();
                        SC.inc();
                    }
                    break;
                case (char) (6):
                    if ((IEN.get() == true) && ((FGI.get() == true) || (FGO.get() == true))) {
                        R.set(true);
                    }
                    // ISZ
                    instruction = new String("M[AR]<-DR, if (DR=0) then\n(PC<-PC+1), SC<-0");
                    mainMemory.set(AR.get(), DR.get());
                    display.adjust(AR.get());
                    if (DR.get() == (char) (0)) {
                        PC.inc();
                    }
                    SC.reset();
                    break;
            }
        } else {   // interrupt cycle
            switch (SC.get()) {   // get timing
                case (char) (0):
                    display.select(PC.get());
                    comment = new String(memoryMap.getCommentAtIndex(memoryMap.getIndexOf(PC.get())));
                    instruction = new String("AR<-0, TR<-PC");
                    AR.set((char) (0));
                    TR.set(PC.get());
                    SC.inc();
                    break;
                case (char) (1):
                    instruction = new String("M[AR]<-TR, PC<-0");
                    mainMemory.set(AR.get(), TR.get());
                    display.adjust(AR.get());
                    PC.set((char) (0));
                    SC.inc();
                    break;
                case (char) (2):
                    instruction = new String("PC<-PC+1, IEN<-0,\nR<-0, SC<-0");
                    PC.inc();
                    IEN.reset();
                    R.reset();
                    SC.reset();
                    break;
            }
        }
    }

    // decode bits 12-14 to a single number D(0:7)
    private void decode1214() {
        int result = 0;

        if (IR.getBit(12) == true) {
            result += 1;
        }

        if (IR.getBit(13) == true) {
            result += 2;
        }

        if (IR.getBit(14) == true) {
            result += 4;
        }

        decoder = result;
    }
}

// a register emulation that stores its bits most-significant-first
// [ 15 | 14 | 13 | ... | 02 | 01 | 00 ]
class ManoRegister {
    int nBits;               // number of bits in register (16, 12, 8...)
    char cData;               // 16-bit unsigned... oh yeah baby

    // constructor
    public ManoRegister(int bits) {
        nBits = bits;
        cData = (char) (0);
    }

    // get the contents of the register
    public char get() {
        return cData;
    }

    // get the status of a single "bit" of the register
    public boolean getBit(int bitNumber) {
        int nBitMask = 1;
        char bitMask;

        nBitMask = nBitMask << bitNumber;
        bitMask = (char) (nBitMask);

        return ((cData & bitMask) == bitMask);
    }

    // shift right with carry in for 16-bit registers
    public void shiftRight16(boolean bitIn) {
        cData = (char) ((int) cData / 2);
        if (bitIn == true) {
            cData = (char) ((int) cData + 32768);
        }
    }

    // shift left with carry in for 16-bit registers
    public void shiftLeft16(boolean bitIn) {
        cData = (char) ((int) cData * 2);
        if (bitIn == true) {
            cData = (char) ((int) cData + 1);
        }
    }

    // logical AND operation
    public void and(char c) {
        cData = (char) (cData & c);
    }

    // arithmentic ADD operation returns CARRY
    public boolean add(char c) {
        int argument = (int) (cData) + (int) (c);
        boolean overflow = false;

        switch (nBits) {
            case 16:
                if (argument > 65535) {
                    overflow = true;
                    argument -= 65536;
                }
                break;
            case 12:
                if (argument > 4095) {
                    overflow = true;
                    argument -= 4096;
                }
                break;
            case 8:
                if (argument > 255) {
                    overflow = true;
                    argument -= 256;
                }
                break;
            case 4:
                if (argument > 15) {
                    overflow = true;
                    argument -= 16;
                }
                break;
        }

        cData = (char) (argument);
        return overflow;
    }

    // increment the value of a register by one
    public void inc() {
        int argument = (int) cData + 1;
        boolean overflow = false;

        switch (nBits) {
            case 16:
                overflow = (argument == 65536);
                break;
            case 12:
                overflow = (argument == 4096);
                break;
            case 8:
                overflow = (argument == 256);
                break;
            case 4:
                overflow = (argument == 16);
                break;
        }

        if (overflow == true) {
            argument = 0;
        }

        cData = (char) (argument);
    }

    // set the data inside - ensure the data is of the correct size
    public void set(char data) {
        int nBitMask16 = 65535;   // ...1111111111111111
        int nBitMask12 = 4095;   // ...0000111111111111
        int nBitMask8 = 255;   // ...0000000011111111
        int nBitMask4 = 15;      // ...0000000000001111
        int nBitMask;
        char bitMask;
        int nData;

        reset();
        switch (nBits) {
            case 16:
                nBitMask = nBitMask16;
                break;
            case 12:
                nBitMask = nBitMask12;
                break;
            case 8:
                nBitMask = nBitMask8;
                break;
            case 4:
                nBitMask = nBitMask4;
                break;
            default:   // just to shut up the compiler
                nBitMask = 0;
        }

        bitMask = (char) (nBitMask);
        nData = ((int) (data)) & bitMask;
        cData = (char) (nData);
    }

    // reset the data inside
    public void reset() {
        cData = (char) (0);
    }

    // flip all the bits
    public void complement() {
        cData = (char) (65535 - (int) (cData));
    }

}

// a flip-flop emulation
class ManoFlipFlop {
    boolean bData;

    // constructor
    public ManoFlipFlop() {
        bData = false;
    }

    // get the contents of the flip-flop
    public boolean get() {
        return bData;
    }

    // set the data inside
    public void set(boolean data) {
        bData = data;
    }

    // toggle the data
    public void toggle() {
        bData = !bData;
    }

    // reset the data inside
    public void reset() {
        bData = false;
    }
}

// 8K memory device emulation
class ManoMemory {
    private char memory[];      // char = 16-bit unsigned

    // constructor
    public ManoMemory() {
        memory = new char[4096];
    }

    // fill memory with the contents of the memory map
    public void loadMemoryMap(ManoMemoryMap mm) {
        for (int i = 0; i < mm.getSize(); i++) {
            set(mm.getAddressAtIndex(i), mm.getInstructionAtIndex(i));
        }
    }

    // set the specified address to the specified data
    public void set(char address, char data) {
        memory[(int) (address)] = data;
    }

    // return the data at the specified address
    public char get(char address) {
        return memory[(int) (address)];
    }

    // return the data at the specified address as a hex string
    public String getWord(int index) {
        return new String(ManoUtil.charToHex(memory[index], 16));
    }

    // clear the contents of memory and let the garbageman clean up
    public void reset() {
        memory = new char[4096];
    }
}

