package com.RobCampbell.Mano;/*
   Rob Campbell
	Fall 2000
	CS 497 @ University of Northern Colorado
	Senior Project
	.
	ManoUtil.java
	This file provides little helper functions for the mano applet.
	.
	Compiled using Sun's Java SDK 1.3.
*/

public final class ManoUtil {
    // convert a char (mano "word") to a hex string whose length
    // represents the specified number of bits
    public static String charToHex(char c, int bits) {
        int nData = (int) (c);
        int nDigit;
        char cDigit;
        int nBitMask = 15;      //...0000000000001111
        int length = bits / 4;
        String out = new String();

        for (int i = 0; i < length; i++) {
            nDigit = ((nData & nBitMask) >> (4 * i));
            cDigit = decCharToHexChar(nDigit);
            out = new String(cDigit + out);
            nBitMask = nBitMask << 4;
        }

        return new String(out);
    }

    // convert an int to a 12-bit hex string
    public static String addressIntToHex(int address) {
        return new String(charToHex(((char) (address)), 12));
    }

    // convert a signed int to a 16-bit hex string, handling overflow
    public static String signedIntToHex(int signedInt) {
        if (signedInt < 0) {
            signedInt += 65536;
        }

        return new String(charToHex(((char) (signedInt)), 16));
    }

    // convert a hex string to a char (mano "word")
    public static char hexToChar(String data) {
        int temp = Integer.parseInt(data, 16);

        if (temp > 32767) {
            temp = temp - 65536;
        }

        return (char) (temp);
    }

    // convert a decimal string to a char (mano "word")
    public static char decToChar(String data) {
        int temp = Integer.parseInt(data, 10);

        if (temp > 32767) {
            temp = temp - 65536;
        }

        return (char) (temp);
    }

    // convert a hex string to a signed int
    public static int hexToSignedInt(String data) {
        int temp = Integer.parseInt(data, 16);

        if (temp > 32767) {
            return (temp - 65536);
        } else {
            return temp;
        }
    }

    // return 1 for true, 0 for false
    public static int boolToInt(boolean b) {
        if (b == true) {
            return 1;
        } else {
            return 0;
        }
    }

    // convert a decimal (0-15) to a hex digit (0-F)
    private static char decCharToHexChar(int d) {
        if ((d >= 0) && (d <= 9)) {
            return ((char) ('0' + d));
        } else {
            return ((char) ('A' + (d - 10)));
        }
    }
}

