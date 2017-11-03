package com.RobCampbell.Mano;/*
   Rob Campbell
	Fall 2000
	CS 497 @ University of Northern Colorado
	Senior Project
	.
	ManoMemoryMap.java
	This file maintains the memory map of the mano program.
	.
	Compiled using Sun's Java SDK 1.3.
*/

import java.util.Vector;

public class ManoMemoryMap {
    Vector map;                  // holds the symbol elements

    // constructor
    public ManoMemoryMap() {
        map = new Vector(0, 5);
    }

    // append an entry to the list of entries
    public void addEntry(char a, char i, String c) {
        map.addElement(new ManoMemoryMapNode(a, i, c));
    }

    // return the index of a address in the memoryMap
    // return -1 if unsuccessful
    public int getIndexOf(char a) {
        int index = -1;
        boolean found = false;
        int i = 0;

        while ((i < map.size()) && (found == false)) {
            if ((char) (getAddressAtIndex(i)) == a) {
                found = true;
                index = i;
            }

            i++;
        }

        return index;
    }

    // return the address of table entry at index i
    public char getAddressAtIndex(int i) {
        return (((ManoMemoryMapNode) (map.elementAt(i))).getAddress());
    }

    // return the instruction of table entry at index i
    public char getInstructionAtIndex(int i) {
        return (((ManoMemoryMapNode) (map.elementAt(i))).getInstruction());
    }

    // return the comment of table entry at index i
    public String getCommentAtIndex(int i) {
        if ((i >= 0) && (i < map.size())) {
            return new String(((ManoMemoryMapNode) (map.elementAt(i))).getComment());
        } else {
            return new String();
        }
    }

    // return the size of the memory map
    public int getSize() {
        return map.size();
    }

    // reset the symbol table
    public void reset() {
        map = new Vector(0, 5);
    }

    // indivdual nodes
    private class ManoMemoryMapNode {
        char address;
        char instruction;
        String comment;

        // constructor
        public ManoMemoryMapNode(char a, char i, String c) {
            address = a;
            instruction = i;
            comment = new String(c);
            comment = new String(comment.replace('\t', ' '));
        }

        // return the address data
        public char getAddress() {
            return address;
        }

        // return the instruction data
        public char getInstruction() {
            return instruction;
        }

        // return the comment data
        public String getComment() {
            return new String(comment);
        }
    }
}

