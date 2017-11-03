package com.RobCampbell.Mano;/*
   Rob Campbell
	Fall 2000
	CS 497 @ University of Northern Colorado
	Senior Project
	.
	ManoDisplay.java
	This portion of the main GUI is separated in order to encapsulate
	the dynamic portions of the GUI, and allow for refreshing these
	dynamic portions with function calls.
	.
	Compiled using Sun's Java SDK 1.3.
*/

import javax.swing.*;
import java.awt.*;

public class ManoDisplay extends Panel {
    // labels
    private Label lbRegisters = new Label("Registers:");
    private Label lbAC = new Label("AC:");
    private Label lbIR = new Label("IR:");
    private Label lbAR = new Label("AR:");
    private Label lbINPR = new Label("INPR:");
    private Label lbDR = new Label("DR:");
    private Label lbTR = new Label("TR:");
    private Label lbPC = new Label("PC:");
    private Label lbOUTR = new Label("OUTR:");
    private Label lbSC = new Label("SC:");
    private Label lbFlipFlops = new Label("Flip-Flops:");
    private Label lbI = new Label("I:");
    private Label lbS = new Label("S:");
    private Label lbE = new Label("E:");
    private Label lbR = new Label("R:");
    private Label lbIEN = new Label("IEN:");
    private Label lbFGI = new Label("FGI:");
    private Label lbFGO = new Label("FGO:");
    private Label lbInstruction = new Label("Instruction:");
    private Label lbComments = new Label("Comments:");
    private Label lbSymbolTable = new Label("Address Symbol Table:");
    private Label lbSTHeader = new Label("Address  Label");
    private Label lbMemoryMap = new Label("Memory Map:");
    private Label lbMMHeader = new Label("Address  HEXCode");

    // text fields
    private TextField tfAC = new TextField(4);
    private TextField tfIR = new TextField(4);
    private TextField tfAR = new TextField(3);
    private TextField tfINPR = new TextField(1);
    private TextField tfDR = new TextField(4);
    private TextField tfTR = new TextField(4);
    private TextField tfPC = new TextField(3);
    private TextField tfOUTR = new TextField(1);
    private TextField tfSC = new TextField(1);
    private TextField tfI = new TextField(1);
    private TextField tfS = new TextField(1);
    private TextField tfE = new TextField(1);
    private TextField tfR = new TextField(1);
    private TextField tfIEN = new TextField(1);
    private TextField tfFGI = new TextField(1);
    private TextField tfFGO = new TextField(1);
    private TextField tfInstruction = new TextField(80);
    private TextField tfComments = new TextField(80);

    // list boxes
    private List lSymbolTable = new List();
    private List lMemoryMap = new List();

    // helpful things
    private Dimension NARROWLABEL = new Dimension(30, 18);
    private Dimension MEDIUMLABEL = new Dimension(40, 18);
    private Dimension XXMEDIUMLABEL = new Dimension(150, 18);
    private Dimension WIDELABEL = new Dimension(200, 18);
    private Dimension NARROWFIELD = new Dimension(35, 24);
    private Dimension MEDIUMFIELD = new Dimension(50, 24);
    private Dimension WIDEFIELD = new Dimension(270, 24);
    private Dimension SHORTAREA = new Dimension(150, 50);
    private Dimension TALLAREA = new Dimension(150, 180);
    private Font MONOFONT = new Font("Monospaced", Font.PLAIN, 12);

    // ===== NON-GUI FIELDS

    private ManoHardware hardware;
    private ManoSymbolTable symbolTable;
    private ManoMemoryMap memoryMap;

    // panel initialization
    public ManoDisplay(ManoHardware mh, ManoSymbolTable mst, ManoMemoryMap mmm) {
        hardware = mh;
        symbolTable = mst;
        memoryMap = mmm;
        setLayout(null);
        setSize(600, 430);
        build();
    }

    // refresh the contents of all text-bearing widgets
    public void updateAll() {
        updateAllWindows();
    }

    // refresh the contents of all text-bearing widgets excluding the
    // memory map and symbol table
    public void update() {
        updateDynamicWindows();
    }

    // update the memory map to reflect the current value of a specified
    // address in memory
    public void adjust(char address) {
        int index = memoryMap.getIndexOf(address);
        lMemoryMap.replaceItem(""
                        + ManoUtil.addressIntToHex(memoryMap.getAddressAtIndex(index))
                        + "      " + ManoUtil.charToHex(hardware.getDataAtAddress(memoryMap.getAddressAtIndex(index)), 16),
                index);
    }

    // move the memory map selection bar to the specified address
    public void select(char address) {
        int index = memoryMap.getIndexOf(address);
        lMemoryMap.select(index);
    }

    // retrieve the first character in the INPR text field
    public char getInput() {
        String sInput = tfINPR.getText();
        if (sInput.length() > 0) {
            return (sInput.charAt(0));
        } else {
            return (char) (0);
        }
    }

    // build the panel
    private void build() {
        lbRegisters.setSize(WIDELABEL);
        lbRegisters.setLocation(20, 50);
        add(lbRegisters);

        lbAC.setSize(MEDIUMLABEL);
        lbAC.setLocation(20, 70);
        add(lbAC);

        lbIR.setSize(MEDIUMLABEL);
        lbIR.setLocation(70, 70);
        add(lbIR);

        lbAR.setSize(MEDIUMLABEL);
        lbAR.setLocation(125, 70);
        add(lbAR);

        lbINPR.setSize(MEDIUMLABEL);
        lbINPR.setLocation(180, 70);
        add(lbINPR);

        lbDR.setSize(MEDIUMLABEL);
        lbDR.setLocation(20, 120);
        add(lbDR);

        lbTR.setSize(MEDIUMLABEL);
        lbTR.setLocation(70, 120);
        add(lbTR);

        lbPC.setSize(MEDIUMLABEL);
        lbPC.setLocation(125, 120);
        add(lbPC);

        lbOUTR.setSize(MEDIUMLABEL);
        lbOUTR.setLocation(180, 120);
        add(lbOUTR);

        lbSC.setSize(MEDIUMLABEL);
        lbSC.setLocation(240, 95);
        add(lbSC);

        tfAC.setSize(MEDIUMFIELD);
        tfAC.setLocation(20, 90);
        add(tfAC);
        tfAC.setEditable(false);

        tfIR.setSize(MEDIUMFIELD);
        tfIR.setLocation(75, 90);
        add(tfIR);
        tfIR.setEditable(false);

        tfAR.setSize(MEDIUMFIELD);
        tfAR.setLocation(130, 90);
        add(tfAR);
        tfAR.setEditable(false);

        tfINPR.setSize(MEDIUMFIELD);
        tfINPR.setLocation(185, 90);
        add(tfINPR);

        tfDR.setSize(MEDIUMFIELD);
        tfDR.setLocation(20, 140);
        add(tfDR);
        tfDR.setEditable(false);

        tfTR.setSize(MEDIUMFIELD);
        tfTR.setLocation(75, 140);
        add(tfTR);
        tfTR.setEditable(false);

        tfPC.setSize(MEDIUMFIELD);
        tfPC.setLocation(130, 140);
        add(tfPC);
        tfPC.setEditable(false);

        tfOUTR.setSize(MEDIUMFIELD);
        tfOUTR.setLocation(185, 140);
        add(tfOUTR);
        tfOUTR.setEditable(false);

        tfSC.setSize(MEDIUMFIELD);
        tfSC.setLocation(245, 115);
        add(tfSC);
        tfSC.setEditable(false);

        lbFlipFlops.setSize(WIDELABEL);
        lbFlipFlops.setLocation(20, 180);
        add(lbFlipFlops);

        lbI.setSize(NARROWLABEL);
        lbI.setLocation(20, 200);
        add(lbI);

        lbS.setSize(NARROWLABEL);
        lbS.setLocation(58, 200);
        add(lbS);

        lbE.setSize(NARROWLABEL);
        lbE.setLocation(96, 200);
        add(lbE);

        lbR.setSize(NARROWLABEL);
        lbR.setLocation(134, 200);
        add(lbR);

        lbIEN.setSize(NARROWLABEL);
        lbIEN.setLocation(172, 200);
        add(lbIEN);

        lbFGI.setSize(NARROWLABEL);
        lbFGI.setLocation(210, 200);
        add(lbFGI);

        lbFGO.setSize(NARROWLABEL);
        lbFGO.setLocation(248, 200);
        add(lbFGO);

        tfI.setSize(NARROWFIELD);
        tfI.setLocation(20, 220);
        add(tfI);
        tfI.setEditable(false);

        tfS.setSize(NARROWFIELD);
        tfS.setLocation(58, 220);
        add(tfS);
        tfS.setEditable(false);

        tfE.setSize(NARROWFIELD);
        tfE.setLocation(96, 220);
        add(tfE);
        tfE.setEditable(false);

        tfR.setSize(NARROWFIELD);
        tfR.setLocation(134, 220);
        add(tfR);
        tfR.setEditable(false);

        tfIEN.setSize(NARROWFIELD);
        tfIEN.setLocation(172, 220);
        add(tfIEN);
        tfIEN.setEditable(false);

        tfFGI.setSize(NARROWFIELD);
        tfFGI.setLocation(210, 220);
        add(tfFGI);
        tfFGI.setEditable(false);

        tfFGO.setSize(NARROWFIELD);
        tfFGO.setLocation(248, 220);
        add(tfFGO);
        tfFGO.setEditable(false);

        lbInstruction.setSize(WIDELABEL);
        lbInstruction.setLocation(20, 260);
        add(lbInstruction);

        tfInstruction.setSize(WIDEFIELD);
        tfInstruction.setLocation(20, 280);
        // tfInstruction.setSize(270, 40);
        add(tfInstruction);
        tfInstruction.setEditable(false);

        lbComments.setSize(WIDELABEL);
        lbComments.setLocation(20, 310);
        add(lbComments);

        tfComments.setSize(WIDEFIELD);
        tfComments.setLocation(20, 330);
        add(tfComments);
        tfComments.setEditable(false);

        lbSymbolTable.setSize(XXMEDIUMLABEL);
        lbSymbolTable.setLocation(430, 50);
        add(lbSymbolTable);

        lbSTHeader.setSize(XXMEDIUMLABEL);
        lbSTHeader.setLocation(430, 70);
        lbSTHeader.setFont(MONOFONT);
        add(lbSTHeader);

        lSymbolTable.setSize(TALLAREA);
        lSymbolTable.setLocation(430, 90);
        lSymbolTable.setMultipleMode(false);
        lSymbolTable.setFont(MONOFONT);
        add(lSymbolTable);

        lbMemoryMap.setSize(XXMEDIUMLABEL);
        lbMemoryMap.setLocation(430, 160);
        add(lbMemoryMap);

        lbMMHeader.setSize(XXMEDIUMLABEL);
        lbMMHeader.setLocation(430, 180);
        lbMMHeader.setFont(MONOFONT);
        add(lbMMHeader);

        lMemoryMap.setSize(TALLAREA);
        lMemoryMap.setLocation(430, 200);
        lMemoryMap.setMultipleMode(false);
        lMemoryMap.setFont(MONOFONT);
        add(lMemoryMap);
    }

    // WINDOW UPDATERS
    // update the entire window
    private void updateAllWindows() {
        updateRegisters();
        updateFlipFlops();
        updateSymbolTable();
        updateMemoryMap();
        updateInstruction();
        updateComments();
    }

    // update the windows with dynamic data
    private void updateDynamicWindows() {
        updateRegisters();
        updateFlipFlops();
        updateInstruction();
        updateComments();
    }

    // update the contents of the registers
    private void updateRegisters() {
        tfAC.setText(new String("" + ManoUtil.charToHex(hardware.getAC(), 16)));
        tfIR.setText(new String("" + ManoUtil.charToHex(hardware.getIR(), 16)));
        tfAR.setText(new String("" + ManoUtil.charToHex(hardware.getAR(), 12)));
        tfDR.setText(new String("" + ManoUtil.charToHex(hardware.getDR(), 16)));
        tfTR.setText(new String("" + ManoUtil.charToHex(hardware.getTR(), 16)));
        tfPC.setText(new String("" + ManoUtil.charToHex(hardware.getPC(), 12)));
        tfOUTR.setText(new String("" + hardware.getOUTR()));
        tfSC.setText(new String("" + ManoUtil.charToHex(hardware.getSC(), 4)));
    }

    // update the contents of the flip-flops
    private void updateFlipFlops() {
        tfI.setText(new String("" + ManoUtil.boolToInt(hardware.getI())));
        tfS.setText(new String("" + ManoUtil.boolToInt(hardware.getS())));
        tfE.setText(new String("" + ManoUtil.boolToInt(hardware.getE())));
        tfR.setText(new String("" + ManoUtil.boolToInt(hardware.getR())));
        tfIEN.setText(new String("" + ManoUtil.boolToInt(hardware.getIEN())));
        tfFGI.setText(new String("" + ManoUtil.boolToInt(hardware.getFGI())));
        tfFGO.setText(new String("" + ManoUtil.boolToInt(hardware.getFGO())));
    }

    // update the contents of the instruction box
    private void updateInstruction() {
        tfInstruction.setText(new String(hardware.getInstruction()));
    }

    // update the contents of the comments box
    private void updateComments() {
        tfComments.setText(new String(hardware.getComment()));
    }

    // update the contents of the symbol table window
    private void updateSymbolTable() {
        lSymbolTable.removeAll();
        for (int i = 0; i < symbolTable.getSize(); i++) {
            lSymbolTable.add(""
                    + ManoUtil.addressIntToHex(symbolTable.getAddressAtIndex(i))
                    + "      " + symbolTable.getLabelAtIndex(i));
        }
    }

    // update the contents of the memory map window
    private void updateMemoryMap() {
        lMemoryMap.removeAll();
        for (int i = 0; i < memoryMap.getSize(); i++) {
            lMemoryMap.add(""
                    + ManoUtil.addressIntToHex(memoryMap.getAddressAtIndex(i))
                    + "      " + ManoUtil.signedIntToHex(memoryMap.getInstructionAtIndex(i)));
        }
    }
}

