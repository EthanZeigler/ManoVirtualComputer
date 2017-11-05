package com.robcampbell.mano;/*
   Rob Campbell
	Fall 2000
	CS 497 @ University of Northern Colorado
	Senior Project
	.
	ManoExportWindow
	This is a dialog window that allows the user to read out the contents
	of the memory map.
	.
	Compiled using Sun's Java SDK 1.3.
*/

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ManoExportWindow extends Dialog implements ActionListener {
    // GUI-related
    private Label lbInstructions1 = new Label
            ("Highlight text and use (Ctrl-C) to copy to clipboard.");
    private Label lbInstructions2 = new Label
            ("Press the Got it! button to dismiss.");
    private Panel pMainPanel = new Panel(new BorderLayout());
    private Panel pLowerPanel = new Panel(new GridLayout(1, 2));
    private Button bGo = new Button("Got it!");
    private TextArea taData = new TextArea();
    private Font MONOFONT = new Font("Monospaced", Font.PLAIN, 12);

    // data
    private ManoHardware hardware;
    private ManoMemoryMap memoryMap;

    // constructor
    public ManoExportWindow(Frame owner, ManoMemoryMap mm, ManoHardware mh) {
        super(owner, "mano - Memory Map Export", true);
        setSize(600, 400);
        pMainPanel.add(lbInstructions1, BorderLayout.NORTH);
        pLowerPanel.add(lbInstructions2);
        pLowerPanel.add(bGo);
        pMainPanel.add(pLowerPanel, BorderLayout.SOUTH);
        pMainPanel.add(taData, BorderLayout.CENTER);
        taData.setFont(MONOFONT);

        add(pMainPanel);
        bGo.addActionListener(this);
        memoryMap = mm;
        hardware = mh;
        fillData();
    }

    // handle user acknowledgement of received data
    public void actionPerformed(ActionEvent e) {
        hide();
    }

    // load the text area with the required data
    private void fillData() {
        for (int i = 0; i < memoryMap.getSize(); i++) {
            taData.append(""
                    + ManoUtil.addressIntToHex(memoryMap.getAddressAtIndex(i))
                    + "\t" + ManoUtil.charToHex(hardware.getDataAtAddress(memoryMap.getAddressAtIndex(i)), 16)
                    + "\n");
        }
    }
}
