package com.robcampbell.mano;/*
   Rob Campbell
	Fall 2000
	CS 497 @ University of Northern Colorado
	Senior Project
	.
	ManoASMWindow.java
	This file provides a dialog which receives, parses, and processes
	mano assembly code, filling the symbol table and memory map in a
	two-pass assembly process.
	.
	Compiled using Sun's Java SDK 1.3.
*/

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;
import java.util.Vector;

public class ManoASMWindow extends Dialog implements ActionListener {
    // GUI - related
    private Label lbInstructions1 = new Label
            ("Paste your code into this window.  (Ctrl-V)");
    private Label lbInstructions2 = new Label
            ("Press the GO! button to load and assemble.");
    private Panel pMainPanel = new Panel(new BorderLayout());
    private Panel pLowerPanel = new Panel(new GridLayout(1, 2));
    private Panel pLowerRightPanel = new Panel(new GridLayout(1, 2));
    private Button bGo = new Button("GO!");
    private Button bCancel = new Button("Cancel");
    private TextArea taData = new TextArea();   // receives the data
    private Font MONOFONT = new Font("Monospaced", Font.PLAIN, 12);

    // data
    private Frame fDummyFrame = new Frame();   // allows pop-up dialog
    private String sData;                  // holds window contents
    private com.robcampbell.mano.ManoSymbolTable symbolTable;      // receives symbols
    private ManoMemoryMap memoryMap;         // receives assembled code
    private ManoFileParser fileParser;
    private boolean result;                  // Go -> T, Cancel -> F

    // constructor
    public ManoASMWindow(Frame owner, ManoSymbolTable st, ManoMemoryMap mm, String oldData) {
        super(owner, "mano - Assembly Language Input", true);
        setSize(600, 400);
        pMainPanel.add(lbInstructions1, BorderLayout.NORTH);
        pLowerPanel.add(lbInstructions2);
        pLowerRightPanel.add(bGo);
        pLowerRightPanel.add(bCancel);
        pLowerPanel.add(pLowerRightPanel);
        pMainPanel.add(pLowerPanel, BorderLayout.SOUTH);
        taData.setEditable(true);
        taData.setFocusable(true);
        taData.requestFocus();
        pMainPanel.add(taData, BorderLayout.CENTER);
        taData.setFont(MONOFONT);
        taData.setText(oldData);

        add(pMainPanel);
        bGo.addActionListener(this);
        bCancel.addActionListener(this);
        symbolTable = st;
        memoryMap = mm;
    }

    // inspector
    public boolean getResult() {
        return result;
    }

    public String getCode() {
        return taData.getText();
    }

    // handle button events
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == bGo) {
            boolean closeOK;

            memoryMap.reset();
            symbolTable.reset();
            taData.setEditable(false);
            sData = taData.getText();
            sData = new String(sData.trim());
            fileParser = new ManoFileParser(sData, fDummyFrame);
            if (fileParser.isSuccessful()) {
                buildSymbolTable();
                closeOK = buildMemoryMap();
                if (closeOK == true) {
                    taData.setEditable(true);
                    result = (memoryMap.getSize() > 0);
                    setVisible(false);
                } else {
                    taData.setEditable(true);
                }
            } else {
                taData.setEditable(true);
            }
        } else if (source == bCancel) {
            result = false;
            setVisible(false);
        }
    }

    // fill the symbol table from the parsed text
    private void buildSymbolTable() {
        char lineCounter = (char) (0);
        boolean more = true;
        int i = 0;

        while ((i < fileParser.getSize()) && (more == true)) {
            if (fileParser.hasLabel(i)) {   // line contains a label
                symbolTable.addSymbol(lineCounter++, fileParser.getArg(0, i));
            } else {   // line contains something else
                if ((fileParser.getArg(1, i)).equalsIgnoreCase("ORG")) {   // adjust line counter
                    lineCounter = ManoUtil.hexToChar(fileParser.getArg(2, i));
                } else if ((fileParser.getArg(1, i)).equalsIgnoreCase("END")) {   // end of program
                    more = false;
                } else {   // other program code
                    lineCounter++;
                }
            }
            i++;
        }
    }

    // fill the memory map from the parsed text
    private boolean buildMemoryMap() {
        char lineCounter = (char) (0);
        boolean more = true;
        boolean success = true;
        int i = 0;
        int x = 0;
        boolean duplicates = false;
        String dupSymbol = null;

        // check for duplicates in the symbol table
        while ((x < symbolTable.getSize()) && (duplicates == false)) {
            for (int y = 0; y < x; y++) {
                if ((y != x) && ((symbolTable.getLabelAtIndex(y)).equalsIgnoreCase(symbolTable.getLabelAtIndex(x)))) {
                    duplicates = true;
                    dupSymbol = new String(symbolTable.getLabelAtIndex(x));
                }
            }
            x++;
        }

        if (duplicates == true) {
            (new ManoErrorDialog(fDummyFrame,
                    new String("Duplicate symbol error: " + dupSymbol))).show();
            more = false;
            success = false;
        }

        // continue creating memory map
        while ((i < fileParser.getSize()) && (more == true)) {
            if (fileParser.isPseudo(i)) {   // is a pseudoinstruction
                if ((fileParser.getArg(1, i)).equalsIgnoreCase("ORG")) {
                    lineCounter = ManoUtil.hexToChar(fileParser.getArg(2, i));
                } else if ((fileParser.getArg(1, i)).equalsIgnoreCase("END")) {
                    more = false;
                } else if ((fileParser.getArg(1, i)).equalsIgnoreCase("DEC")) {
                    memoryMap.addEntry(lineCounter++,
                            ManoUtil.decToChar(fileParser.getArg(2, i)),
                            fileParser.getLineContents(i));
                } else if ((fileParser.getArg(1, i)).equalsIgnoreCase("HEX")) {
                    memoryMap.addEntry(lineCounter++,
                            ManoUtil.hexToChar(fileParser.getArg(2, i)),
                            fileParser.getLineContents(i));
                }
            } else {   // is not a pseudoinstruction
                if (((fileParser.getArg(1, i)).equalsIgnoreCase("AND"))
                        || ((fileParser.getArg(1, i)).equalsIgnoreCase("ADD"))
                        || ((fileParser.getArg(1, i)).equalsIgnoreCase("LDA"))
                        || ((fileParser.getArg(1, i)).equalsIgnoreCase("STA"))
                        || ((fileParser.getArg(1, i)).equalsIgnoreCase("BUN"))
                        || ((fileParser.getArg(1, i)).equalsIgnoreCase("BSA"))
                        || ((fileParser.getArg(1, i)).equalsIgnoreCase("ISZ"))) {   // handle memory-reference instruction
                    if (symbolTable.getIndexOf(fileParser.getArg(2, i)) != -1) {   // symbol found - add to table
                        if (fileParser.getArgumentCount(i) == 3) {   // indirect
                            memoryMap.addEntry(lineCounter++, ((char)
                                            (ManoUtil.hexToChar(ManoOpcodeLookupTable.get(fileParser.getArg(1, i), true))
                                                    + symbolTable.getAddressAtIndex(symbolTable.getIndexOf(fileParser.getArg(2, i))))),
                                    fileParser.getLineContents(i));
                        } else {   // direct
                            memoryMap.addEntry(lineCounter++, ((char)
                                            (ManoUtil.hexToChar(ManoOpcodeLookupTable.get(fileParser.getArg(1, i), false))
                                                    + symbolTable.getAddressAtIndex(symbolTable.getIndexOf(fileParser.getArg(2, i))))),
                                    fileParser.getLineContents(i));
                        }
                    } else {   // symbol not found - raise an error
                        (new ManoErrorDialog(fDummyFrame,
                                new String("Unknown argument error: "
                                        + fileParser.getLineContents(i)))).setVisible(true);
                        more = false;
                        success = false;
                    }
                } else {   // handle register-reference and i/o instruction
                    memoryMap.addEntry(lineCounter++, (char)
                                    (ManoUtil.hexToChar(ManoOpcodeLookupTable.get(fileParser.getArg(1, i)))),
                            fileParser.getLineContents(i));
                }
            }

            i++;
        }

        return success;
    }
}

// maintains a collection of single tokenized lines
class ManoFileParser {
    Vector lines;
    String sFileContents;
    String sCurrentLine;
    boolean success;
    StringTokenizer lineTok;
    ManoTokenizedLine currentLine;
    Frame fErrorFrame;

    // constructor
    public ManoFileParser(String dataLine, Frame errorFrame) {
        lines = new Vector(0, 5);
        sFileContents = new String(dataLine);
        success = true;
        fErrorFrame = errorFrame;
        process();
    }

    // returns whether the file could be parsed successfully
    public boolean isSuccessful() {
        return success;
    }

    // inspector
    public int getSize() {
        return (lines.size());
    }

    // return the specified argument of the specified tokenized line
    public String getArg(int argNumber, int index) {
        return (new String(((ManoTokenizedLine) (lines.elementAt(index))).getArg(argNumber)));
    }

    // return the contents of the specified tokenized line
    public String getLineContents(int index) {
        return new String(((ManoTokenizedLine) (lines.elementAt(index))).getLineContents());
    }

    // return whether the specified tokenized line has a label
    public boolean hasLabel(int index) {
        return ((ManoTokenizedLine) (lines.elementAt(index))).hasLabel();
    }

    // return whether the specified line is a line of mano code
    public boolean isCode(int index) {
        return ((ManoTokenizedLine) (lines.elementAt(index))).isCode();
    }

    // return whether the specified line is non-garbage
    public boolean isValid(int index) {
        return ((ManoTokenizedLine) (lines.elementAt(index))).isValid();
    }

    // return whether the specified line is a mano pseudoinstruction
    public boolean isPseudo(int index) {
        return ((ManoTokenizedLine) (lines.elementAt(index))).isPseudo();
    }

    // return the number of arguments in the specified tokenized line
    public int getArgumentCount(int index) {
        return ((ManoTokenizedLine) (lines.elementAt(index))).getArgumentCount();
    }

    // break the text into lines and send it out for processing
    private void process() {
        boolean isOk = true;
        lineTok = new StringTokenizer(sFileContents, "\n");
        while ((lineTok.hasMoreTokens()) && (isOk == true)) {   // process each line
            sCurrentLine = new String(lineTok.nextToken());
            sCurrentLine = new String(sCurrentLine.trim());
            currentLine = new ManoTokenizedLine(sCurrentLine);

            if (currentLine.isValid()) {   // add code to the array of parsed lines
                if (currentLine.isCode()) {
                    lines.addElement(new ManoTokenizedLine(currentLine));
                }
            } else {   // raise an error
                isOk = false;
                (new ManoErrorDialog(fErrorFrame, new String("Syntax error: "
                        + sCurrentLine))).setVisible(true);
            }
        }
        success = isOk;
    }
}

// maintains a line in individual, managed tokens
class ManoTokenizedLine {
    String sLineContents;
    String sLabel;
    String sArg1;
    String sArg2;
    String sArg3;
    int nArguments;
    boolean hasLabel;
    boolean isCode;
    boolean isValid;
    boolean isPseudo;
    StringTokenizer tokenTok;

    // constructor
    public ManoTokenizedLine(String data) {
        sLineContents = new String(data);
        hasLabel = false;
        isCode = true;
        isValid = true;
        isPseudo = false;
        tokenize();
    }

    // copy constructor
    public ManoTokenizedLine(ManoTokenizedLine mtl) {
        sLineContents = new String(mtl.getLineContents());
        sLabel = new String(mtl.getArg(0));
        sArg1 = new String(mtl.getArg(1));
        sArg2 = new String(mtl.getArg(2));
        sArg3 = new String(mtl.getArg(3));
        nArguments = mtl.getArgumentCount();
        hasLabel = mtl.hasLabel();
        isCode = mtl.isCode();
        isValid = mtl.isValid();
        isPseudo = mtl.isPseudo();
    }

    // return whether the line has a label
    public boolean hasLabel() {
        return hasLabel;
    }

    // return whether the line is mano code
    public boolean isCode() {
        return isCode;
    }

    // return whether the line is non-garbage
    public boolean isValid() {
        return isValid;
    }

    // return whether the line is a mano pseudoinstruction
    public boolean isPseudo() {
        return isPseudo;
    }

    // retrieve the specified argument from the line
    public String getArg(int index) {
        String returnString;

        switch (index) {
            case 0:      // label
                returnString = sLabel;
                break;
            case 1:      // arg1
                returnString = sArg1;
                break;
            case 2:      // arg2
                returnString = sArg2;
                break;
            case 3:      // arg3
                returnString = sArg3;
                break;
            default:   // silence compiler
                returnString = null;
        }

        if (returnString != null) {
            return new String(returnString);
        } else {
            return new String();
        }
    }

    // return the number of arguments on the line
    public int getArgumentCount() {
        return nArguments;
    }

    // return the contents of the line
    public String getLineContents() {
        return new String(sLineContents);
    }

    // break the line into tokens and inspect the line for syntax
    private void tokenize() {
        if (!(sLineContents.startsWith("/"))) {   // not a whole-line comment
            // strip off end-of-line comments
            tokenTok = new StringTokenizer(sLineContents, "/");
            sLineContents = new String(tokenTok.nextToken());
            sLineContents = new String(sLineContents.trim());
            if (sLineContents.indexOf(',') != -1) {   // this line contains a label
                hasLabel = true;
                tokenTok = new StringTokenizer(sLineContents, ",");
                sLabel = new String(tokenTok.nextToken());
                sLabel = new String(sLabel.trim());
                sLineContents = new String(tokenTok.nextToken());
                sLineContents = new String(sLineContents.trim());
            }
            // get rest of line
            tokenTok = new StringTokenizer(sLineContents);
            sArg1 = new String(tokenTok.nextToken());
            sArg1 = new String(sArg1.trim());
            if ((sArg1.equalsIgnoreCase("END"))
                    || (sArg1.equalsIgnoreCase("CLA"))
                    || (sArg1.equalsIgnoreCase("CLE"))
                    || (sArg1.equalsIgnoreCase("CMA"))
                    || (sArg1.equalsIgnoreCase("CME"))
                    || (sArg1.equalsIgnoreCase("CIR"))
                    || (sArg1.equalsIgnoreCase("CIL"))
                    || (sArg1.equalsIgnoreCase("INC"))
                    || (sArg1.equalsIgnoreCase("SPA"))
                    || (sArg1.equalsIgnoreCase("SNA"))
                    || (sArg1.equalsIgnoreCase("SZA"))
                    || (sArg1.equalsIgnoreCase("SZE"))
                    || (sArg1.equalsIgnoreCase("HLT"))
                    || (sArg1.equalsIgnoreCase("INP"))
                    || (sArg1.equalsIgnoreCase("OUT"))
                    || (sArg1.equalsIgnoreCase("SKI"))
                    || (sArg1.equalsIgnoreCase("SKO"))
                    || (sArg1.equalsIgnoreCase("ION"))
                    || (sArg1.equalsIgnoreCase("IOF"))) {   // there should be only one argument
                if (tokenTok.hasMoreTokens()) {
                    isValid = false;
                    nArguments = 0;
                } else {
                    nArguments = 1;
                    if (sArg1.equalsIgnoreCase("END")) {
                        isPseudo = true;
                    }
                }
            } else {   // there should be multiple arguments
                if ((sArg1.equalsIgnoreCase("AND"))
                        || (sArg1.equalsIgnoreCase("ADD"))
                        || (sArg1.equalsIgnoreCase("LDA"))
                        || (sArg1.equalsIgnoreCase("STA"))
                        || (sArg1.equalsIgnoreCase("BUN"))
                        || (sArg1.equalsIgnoreCase("BSA"))
                        || (sArg1.equalsIgnoreCase("ISZ"))
                        || (sArg1.equalsIgnoreCase("ORG"))
                        || (sArg1.equalsIgnoreCase("DEC"))
                        || (sArg1.equalsIgnoreCase("HEX"))) {   // there should be at least one more argument
                    sArg2 = new String(tokenTok.nextToken());
                    sArg2 = new String(sArg2.trim());
                    if ((sArg1.equalsIgnoreCase("ORG"))
                            || (sArg1.equalsIgnoreCase("DEC"))
                            || (sArg1.equalsIgnoreCase("HEX"))) {   // there should be only one more argument
                        if (tokenTok.hasMoreTokens()) {
                            isValid = false;
                            nArguments = 0;
                        } else {
                            isPseudo = true;
                            nArguments = 2;
                        }
                    } else {   // indirection is possible
                        if (tokenTok.hasMoreTokens()) {   // there is SOMETHING after the operand - see if it's "I"
                            sArg3 = new String(tokenTok.nextToken());
                            sArg3 = new String(sArg3.trim());
                            if (sArg3.equalsIgnoreCase("I")) {
                                nArguments = 3;
                            } else {
                                isValid = false;
                                nArguments = 0;
                            }
                        } else {   // it is direct
                            nArguments = 2;
                        }
                    }
                } else {   // this is not a valid command
                    nArguments = 0;
                    isValid = false;
                }
            }
        } else {   // is a whole-line comment
            isCode = false;
        }
    }
}

// maintains a list of the mano opcodes and facilitates look-up
class ManoOpcodeLookupTable {
    // array of memory-reference instructions
    private static final String sMR[][] =
            {
                    {"AND", "0000", "8000"},
                    {"ADD", "1000", "9000"},
                    {"LDA", "2000", "A000"},
                    {"STA", "3000", "B000"},
                    {"BUN", "4000", "C000"},
                    {"BSA", "5000", "D000"},
                    {"ISZ", "6000", "E000"}
            };

    // array of register-reference and I/O instructions
    private static final String sRRIO[][] =
            {
                    {"CLA", "7800"},
                    {"CLE", "7400"},
                    {"CMA", "7200"},
                    {"CME", "7100"},
                    {"CIR", "7080"},
                    {"CIL", "7040"},
                    {"INC", "7020"},
                    {"SPA", "7010"},
                    {"SNA", "7008"},
                    {"SZA", "7004"},
                    {"SZE", "7002"},
                    {"HLT", "7001"},
                    {"INP", "F800"},
                    {"OUT", "F400"},
                    {"SKI", "F200"},
                    {"SKO", "F100"},
                    {"ION", "F080"},
                    {"IOF", "F040"},
            };

    // return a memory-reference opcode's machine language
    public static String get(String opcode, boolean indirect) {
        int index = getIndexMR(opcode);

        if (indirect == false) {
            return (new String(sMR[index][1]));
        } else {
            return (new String(sMR[index][2]));
        }
    }

    // return a register-reference or I/O opcode's machine language
    public static String get(String opcode) {
        int index = getIndexRRIO(opcode);

        return (new String(sRRIO[index][1]));
    }

    // locate a memory-reference opcode in the array
    private static int getIndexMR(String opcode) {
        int index = -1;
        int i = 0;

        while ((i < 7) && (index == -1)) {
            if ((sMR[i][0]).equalsIgnoreCase(opcode)) {
                index = i;
            }
            i++;
        }

        return index;
    }

    // locate a register-reference or I/O opcode in the array
    private static int getIndexRRIO(String opcode) {
        int index = -1;
        int i = 0;

        while ((i < 18) && (index == -1)) {
            if ((sRRIO[i][0]).equalsIgnoreCase(opcode)) {
                index = i;
            }
            i++;
        }

        return index;
    }
}
