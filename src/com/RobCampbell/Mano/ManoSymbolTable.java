package com.RobCampbell.Mano;/*
   Rob Campbell
	Fall 2000
	CS 497 @ University of Northern Colorado
	Senior Project
	.
	ManoSymbolTable.java
	This file maintains the symbol table of the mano program.
	.
	Compiled using Sun's Java SDK 1.3.
*/

import java.util.Vector;

public class ManoSymbolTable {
    Vector table;            // holds the symbol elements

    // constructor
    public ManoSymbolTable() {
        table = new Vector(0, 5);
    }

    // append a symbol to the list of symbols
    public void addSymbol(char a, String l) {
        table.addElement(new ManoSymbolTableNode(a, l));
    }

    // return the index of a symbol in the symbol table
    // return -1 if unsuccessful
    public int getIndexOf(String l) {
        int index = -1;
        boolean found = false;
        int i = 0;

        while ((i < table.size()) && (found == false)) {
            if ((((ManoSymbolTableNode) (table.elementAt(i))).getLabel()).equalsIgnoreCase((String) (l))) {
                found = true;
                index = i;
            }

            i++;
        }

        return index;
    }

    // return the address of table entry at index i
    public char getAddressAtIndex(int i) {
        return (((ManoSymbolTableNode) (table.elementAt(i))).getAddress());
    }

    // return the label of table entry at index i
    public String getLabelAtIndex(int i) {
        return new String(((ManoSymbolTableNode) (table.elementAt(i))).getLabel());

    }

    // return the size of the symbol table
    public int getSize() {
        return table.size();
    }

    // reset the symbol table
    public void reset() {
        table = new Vector(0, 5);
    }

    // indivdual nodes
    private class ManoSymbolTableNode {
        char address;
        String label;

        // constructor
        public ManoSymbolTableNode(char a, String l) {
            address = a;
            label = new String(l);
        }

        // return the address data
        public char getAddress() {
            return address;
        }

        // return the label data
        public String getLabel() {
            return new String(label);
        }
    }
}

