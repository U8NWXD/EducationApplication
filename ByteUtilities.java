/*
 * Copyright (C) 2016 U8N WXD.
 * This file is part of EducationApplication.
 *
 * EducationApplication is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EducationApplication is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with EducationApplication.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.icloud.cs_temporary.EducationApplication;

import com.sun.org.apache.xml.internal.security.utils.Base64;

/**
 * Class for common byte manipulations
 */
public class ByteUtilities {
    /**
     * Static method to convert byte array to hexadecimal String
     * @param Bytes Byte array to convert into hexadecimals
     * @return Hexadecimal String representing of the provided byte string
     */
    public static String ByteToHex(byte[] Bytes) {
        StringBuilder s = new StringBuilder();
        for (byte b : Bytes) {
            s.append(String.format("%02X", b));
        }
        return s.toString();
    }

    /**
     * Static method to convert from bytes to Base 64 Strings
     * @param Bytes Byte data to encode
     * @return Base64-encoded data
     */
    public static String ByteToBase64(byte[] Bytes) {
        return Base64.encode(Bytes);
    }

    /**
     * Static method to convert from Base 64 Strings to bytes
     * @param base64 Base64-encoded String
     * @return Byte array of decoded data
     * @throws Exception If provided data is not valid base64
     */
    public static byte[] Base64ToByte(String base64) throws Exception{
        return Base64.decode(base64);
    }

    /**
     * Static method for converting from byte array to String
     * @param Bytes Data as a byte array, must actually represent a String for readable data to be returned
     * @return String of data
     */
    public static String ByteToString(byte[] Bytes) {
        StringBuilder s = new StringBuilder();
        for (byte b : Bytes) {
            s.append((char) b);
        }
        return s.toString();
    }

    /**
     * Static method for converting a String to a byte array
     * @param input String to convert
     * @return Byte array representation of provided String
     */
    public static byte[] StringToByte(String input) {
        return input.getBytes();
    }
}
