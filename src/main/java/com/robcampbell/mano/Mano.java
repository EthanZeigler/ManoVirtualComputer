package com.robcampbell.mano;/*
<applet code="mano.class" CodeBase="" width=300 height=400></applet>
*/

/*
   Rob Campbell
	Fall 2000
	CS 497 @ University of Northern Colorado
	Senior Project
	.
	mano.java
	This is the main file, maintaining the user interface and the flow of
	the mano applet.
	.
	Compiled using Sun's Java SDK 1.3.
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class Mano extends JFrame implements ActionListener, ItemListener {
    // ===== GUI FIELDS =====
    // display
    private ManoDisplay display;

    // buttons
    private Button bLoad = new Button("Load");
    private Button bUnload = new Button("Unload");
    private Button bReset = new Button("Reset");
    private Button bInputEnable = new Button("Input Enable");
    private Button bOutputEnable = new Button("Output Enable");
    private Button bStep = new Button("Step");
    private Button bRun = new Button("Run");
    private Button bStop = new Button("Stop");
    private Button bExport = new Button("Export Memory Map");

    // labels
    private Label lbRunSpeed = new Label("Run Speed:");

    // radio buttons
    private CheckboxGroup cbgSpeed = new CheckboxGroup();
    private Checkbox cbSlow = new Checkbox("Slow", false, cbgSpeed);
    private Checkbox cbMedium = new Checkbox("Medium", false, cbgSpeed);
    private Checkbox cbFast = new Checkbox("Fast", true, cbgSpeed);

    // helpful things
    private Frame fDummyFrame = new Frame();
    private Dimension NARROWBUTTON = new Dimension(64, 24);
    private Dimension WIDEBUTTON = new Dimension(100, 24);
    private Dimension XWIDEBUTTON = new Dimension(150, 24);
    private Dimension XMEDIUMLABEL = new Dimension(100, 18);
    private Dimension CBSIZE = new Dimension(100, 24);

    // ===== NON-GUI FIELDS

    private ManoASMWindow assembler;
    private ManoHardware hardware;
    private ManoSymbolTable symbolTable;
    private ManoMemoryMap memoryMap;

    private String codeMem = "";

    // applet initialization
    public void init() {
        hardware = new ManoHardware(this);
        symbolTable = new ManoSymbolTable();
        memoryMap = new ManoMemoryMap();

        display = new ManoDisplay(hardware, symbolTable, memoryMap);
        hardware.associateManoDisplay(display);
        hardware.associateManoMemoryMap(memoryMap);

        setLayout(null);
        setSize(600, 430);
        build();
        setActionButtonsEnabled(false);
        awaken();
        display.updateAll();
    }

    // build the GUI
    private void build() {
        display.setSize(600, 475);
        display.setLocation(0, 0);

        bLoad.setSize(NARROWBUTTON);
        bLoad.setLocation(10, 10);
        display.add(bLoad);

        bUnload.setSize(WIDEBUTTON);
        bUnload.setLocation(84, 10);
        display.add(bUnload);
        bUnload.setEnabled(false);

        bReset.setSize(NARROWBUTTON);
        bReset.setLocation(192, 10);
        display.add(bReset);
        bReset.setEnabled(false);

        bInputEnable.setSize(WIDEBUTTON);
        bInputEnable.setLocation(300, 80);
        display.add(bInputEnable);
        bInputEnable.setEnabled(false);

        bOutputEnable.setSize(WIDEBUTTON);
        bOutputEnable.setLocation(300, 110);
        display.add(bOutputEnable);
        bOutputEnable.setEnabled(false);

        bStep.setSize(WIDEBUTTON);
        bStep.setLocation(300, 150);
        display.add(bStep);
        bStep.setEnabled(false);

        bRun.setSize(WIDEBUTTON);
        bRun.setLocation(300, 180);
        display.add(bRun);
        bRun.setEnabled(false);

        bStop.setSize(WIDEBUTTON);
        bStop.setLocation(300, 210);
        display.add(bStop);
        bStop.setEnabled(false);

        bExport.setSize(XWIDEBUTTON);
        bExport.setLocation(430, 396);
        display.add(bExport);
        bExport.setEnabled(false);

        lbRunSpeed.setSize(XMEDIUMLABEL);
        lbRunSpeed.setLocation(300, 260);
        display.add(lbRunSpeed);

        cbSlow.setSize(CBSIZE);
        cbSlow.setLocation(300, 280);
        display.add(cbSlow);

        cbMedium.setSize(CBSIZE);
        cbMedium.setLocation(300, 305);
        display.add(cbMedium);

        cbFast.setSize(CBSIZE);
        cbFast.setLocation(300, 330);
        display.add(cbFast);

        add(display);
    }

    // add listeners for event handling
    private void awaken() {
        bLoad.addActionListener(this);
        bUnload.addActionListener(this);
        bReset.addActionListener(this);
        bInputEnable.addActionListener(this);
        bOutputEnable.addActionListener(this);
        bStep.addActionListener(this);
        bRun.addActionListener(this);
        bStop.addActionListener(this);
        bExport.addActionListener(this);

        cbSlow.addItemListener(this);
        cbMedium.addItemListener(this);
        cbFast.addItemListener(this);
    }

    // handle button events
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == bLoad) {
            assembler = new ManoASMWindow(fDummyFrame, symbolTable, memoryMap, codeMem);
            assembler.setVisible(true);
            if (assembler.getResult()) {
                codeMem = assembler.getCode();
                bLoad.setEnabled(false);
                hardware.initialize(memoryMap);
                hardware.associateManoMemoryMap(memoryMap);
                display.updateAll();
                bUnload.setEnabled(true);
                bReset.setEnabled(true);
                bExport.setEnabled(true);
                setActionButtonsEnabled(true);
            }
        } else if (source == bUnload) {
            bUnload.setEnabled(false);
            setActionButtonsEnabled(false);
            symbolTable.reset();
            memoryMap.reset();
            hardware.reset();
            display.updateAll();
            bLoad.setEnabled(true);
            bReset.setEnabled(false);
            bExport.setEnabled(false);
        } else if (source == bReset) {
            hardware.initialize(memoryMap);
            display.updateAll();
        } else if (source == bInputEnable) {
            hardware.inputEnable();
        } else if (source == bOutputEnable) {
            hardware.outputEnable();
        } else if (source == bStep) {
            hardware.manoStep();
        } else if (source == bRun) {
            bRun.setEnabled(false);
            bStop.setEnabled(true);
            bUnload.setEnabled(false);
            bReset.setEnabled(false);
            bStep.setEnabled(false);
            bExport.setEnabled(false);
            hardware.manoStart();
        } else if (source == bStop) {
            hardware.manoStop();
            bStop.setEnabled(false);
            bUnload.setEnabled(true);
            bReset.setEnabled(true);
            bStep.setEnabled(true);
            bRun.setEnabled(true);
            bExport.setEnabled(true);
        } else if (source == bExport) {
            (new ManoExportWindow(fDummyFrame, memoryMap, hardware)).setVisible(true);
        }
    }

    // handle item events
    public void itemStateChanged(ItemEvent ie) {
        Object source = ie.getSource();

        if (source == cbSlow) {
            hardware.setRunSpeed(0);
        } else if (source == cbMedium) {
            hardware.setRunSpeed(1);
        } else if (source == cbFast) {
            hardware.setRunSpeed(2);
        }
    }

    // handle hardware-generated halt
    public void informStop() {
        bStop.setEnabled(false);
        bUnload.setEnabled(true);
        bReset.setEnabled(true);
        bStep.setEnabled(true);
        bRun.setEnabled(true);
        bExport.setEnabled(true);
    }

    // enable/disable 4 of the 5 action buttons as a whole
    private void setActionButtonsEnabled(boolean b) {
        bInputEnable.setEnabled(b);
        bOutputEnable.setEnabled(b);
        bStep.setEnabled(b);
        bRun.setEnabled(b);
    }

    public static void main(String[] args) {
        Mano mano = new Mano();
        mano.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        mano.init();
        mano.setVisible(true);
    }
}
