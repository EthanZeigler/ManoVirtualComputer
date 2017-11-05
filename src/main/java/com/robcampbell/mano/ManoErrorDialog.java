package com.robcampbell.mano;/*
   Rob Campbell
	Fall 2000
	CS 497 @ University of Northern Colorado
	Senior Project
	.
	ManoErrorDialog.java
	This is a dialog box that is used to indicate an error has occurred.
	It allows the message to be passed in, permitting multiple usage.
	.
	Compiled using Sun's Java SDK 1.3.
*/

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ManoErrorDialog extends Dialog implements ActionListener {
    private Panel pMainPanel = new Panel(new BorderLayout());
    private Label lbPrompt = new Label
            ("An error has occurred:");
    private Label lbErrorMessage = new Label();
    private Button bOK = new Button("OOPS!");
    private String sErrorMessage;

    // constructor
    public ManoErrorDialog(Frame owner, String message) {
        super(owner, "mano - Error", true);
        setSize(400, 200);
        pMainPanel.add(lbPrompt, BorderLayout.NORTH);
        pMainPanel.add(bOK, BorderLayout.SOUTH);
        sErrorMessage = new String(message.replace('\t', ' '));
        lbErrorMessage.setText(sErrorMessage);
        pMainPanel.add(lbErrorMessage, BorderLayout.CENTER);
        add(pMainPanel);
        bOK.addActionListener(this);
    }

    // respond to user acknowledgement
    public void actionPerformed(ActionEvent e) {
        hide();
    }
}

