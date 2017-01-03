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
import java.io.*;

/**
 * Class that contains utilities for working with files
 */
public class FileUtilities {
    public static final String EOL = "\n";

    /**
     * Writes the provided data to the file
     * @param input Data to write
     * @param path Full filepath to target file
     * @throws Exception Required for some reason
     */
    public static void toFile(byte[] input, String path) throws Exception {
        File file=new File(path);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(input);
        fos.flush();
        fos.close();
    }

    /**
     * Writes the provided data to the file
     * @param text Data to write
     * @param path Full filepath to target file
     * @throws Exception Required for some reason
     */
    public static void toFile(String text, String path) throws Exception {
        File f = new File(path);
        FileOutputStream fos = new FileOutputStream(f);
        DataOutputStream dos = new DataOutputStream(fos);
        dos.writeBytes(text);
        dos.flush();
        dos.close();
    }

    /**
     * Reads in data from the specified file
     * @param path Full filepath to file to read data from
     * @return Data from file
     * @throws Exception Required for some reason
     */
    public static String fromFile(String path) throws Exception {
        File f = new File(path);
        FileInputStream fis = new FileInputStream(f);
        DataInputStream dis = new DataInputStream(fis);
        byte[] keyBytes = new byte[(int) f.length()];
        dis.readFully(keyBytes);
        dis.close();

        return ByteUtilities.ByteToString(keyBytes);
    }

    /**
     * Read in data from the specified file and clean it of annotations (comments and blank lines)
     * @param path Path to file
     * @return Cleaned text of the file
     * @throws Exception Thrown in case of a non-existent or inaccessible file
     */
    public static String fromFileCleaned(String path) throws Exception {
        String text = fromFile(path);
        return removeAnnotations(text);
    }

    /**
     * Append text to the end of a file
     * @param text Text to add
     * @param path Path to the file
     * @throws Exception Required in case the file does not exist
     */
    public static void appendFile(String text, String path) throws Exception {
        String existing = fromFileCleaned(path);
        toFile(existing + text, path);
    }

    /**
     * Extracts the useful data from ASCII-armored / Radix-64 data
     * @param encapsulated Raw data with ASCII-armor
     * @return Decoded form of the data
     * @throws Exception If encapsulated data is wrongly formatted
     */
    public static byte[] fromEncapsulation(String encapsulated, String header) throws Exception {
        encapsulated = findEncapsulation(encapsulated, header);
        if (encapsulated == null)
            return null;
        int startIndex = encapsulated.indexOf("-----\n") + 6;
        int endIndex = encapsulated.indexOf("\n-----", startIndex);
        String formatted = encapsulated.substring(startIndex, endIndex);
        String base64 = "";
        int previous = 0;
        for (int i = 0; i < formatted.length(); i ++) {
            if (formatted.charAt(i) == '\n') {
                base64 += formatted.substring(previous, i);
                previous = i + 1;
            }
        }
        base64 += formatted.substring(previous, formatted.length());
        return ByteUtilities.Base64ToByte(base64);
    }

    /**
     * Find and return encapsulated text based on the title in the headers
     * @param data Data to search
     * @param target Target phrase to search for in header, such as PGP KEY BLOCK
     * @return Encapsulated block, including the header and footer; if it doesn't exist, returns null
     */
    public static String findEncapsulation(String data, String target) {
        String startTarget = "-----BEGIN " + target + "-----";
        String endTarget = "-----END " + target + "-----";
        int startIndex = data.indexOf(startTarget);
        int endIndex = data.indexOf(endTarget);

        if (startIndex == -1 || endIndex == -1)
            return null;

        endIndex = endIndex + endTarget.length();

        return data.substring(startIndex, endIndex);
    }

    /**
     * Encapsulates the provided byte data
     * @param data Byte data to encapsulate
     * @param header The main text of the header, excluding BEGIN / END
     * @return Encapsulated text
     */
    public static String makeEncapsulation(byte[] data, String header) {
        String inner = ByteUtilities.ByteToBase64(data);
        String encapsulated = "-----BEGIN " + header + "-----\n";
        encapsulated += inner;
        encapsulated += "\n-----END " + header + "-----\n";
        return encapsulated;
    }

    /**
     * Finds the DATA in the format "LABEL: DATA\n" anywhere in a String
     * @param all String data to search
     * @param label Label to search for
     * @return The data found after the label, or null if it is not found
     */
    public static String findDataFromLabel(String all, String label) {
        String parameter = label + ": ";
        int originalStartIndex = all.indexOf(parameter);
        int startIndex = originalStartIndex + parameter.length();
        int endIndex = all.indexOf(EOL, startIndex);

        if (originalStartIndex < 0 || endIndex < 0)
            return null;
        else
            return all.substring(startIndex, endIndex);
    }

    /**
     * Check whether a file exists
     * @param path Path of file whose existence will be checked
     * @return True if file exists, false if it doesn't
     */
    public static boolean fileExists(String path) {
        File f = new File(path);
        return f.exists() && f.isFile();
    }

    /**
     * Check whether a filepath is valid
     * @param path Filepath to verify
     * @return True if filepath is valid, false if it isn't
     */
    public static boolean pathExists(String path) {
        File f = new File(path);
        return f.exists();
    }

    /**
     * Make a string valid for writing to a file by removing special characters
     * @param original String to make valid
     * @return String that is valid for recording in a file
     */
    public static String replaceSpecialFileCharacters(String original) {
        // Replaces these characters: *{}\t\r\n
        original = original.replace("\t", "@TAB");
        original = original.replace("\n", "@NEWLINE");
        original = original.replace("\r", "@RETURN");
        original = original.replace("*", "@ASTERISK");
        original = original.replace("{", "@OPENBRACE");
        original = original.replace("}", "@CLOSEBRACE");
        return original;
    }

    /**
     * Restore original text after it has been formatted for writing to file
     * @param fromFile Formatted text
     * @return Original text
     */
    public static String restoreSpecialFileCharacters(String fromFile) {
        fromFile = fromFile.replace("@TAB", "\t");
        fromFile = fromFile.replace("@NEWLINE", "\n");
        fromFile = fromFile.replace("@RETURN", "\r");
        fromFile = fromFile.replace("@ASTERISK", "*");
        fromFile = fromFile.replace("@OPENBRACE", "{");
        fromFile = fromFile.replace("@CLOSEBRACE", "}");
        return fromFile;
    }

    /**
     * Make a String into a cross-platform valid file name
     * @param original Text to convert to file name
     * @return Valid file name from text
     */
    public static String makePathCompliant(String original) {
        // Replaces these characters: ,.<>/?\|÷'";:[] ^%#$&•{}@*\t-\r\n–
        original = original.replace(",", "@COMMA");
        original = original.replace(".", "@PERIOD");
        original = original.replace(">", "@GREATER");
        original = original.replace("<", "@LESS");
        original = original.replace("/", "@FORWARDSLASH");
        original = original.replace("?", "@QUESTION");
        original = original.replace("\\", "@BACKSLASH");
        original = original.replace("÷", "@DIVISION");
        original = original.replace("'", "@APOSTROPHE");
        original = original.replace("\"", "@QUOTATION");
        original = original.replace(";", "@SEMICOLON");
        original = original.replace(":", "@COLON");
        original = original.replace("[", "@OPENBRACE");
        original = original.replace("]", "@CLOSEBRACE");
        original = original.replace("^", "@CARET");
        original = original.replace("%", "@PERCENT");
        original = original.replace("#", "@POUND");
        original = original.replace("$", "@DOLLAR");
        original = original.replace("&", "@AMPERSAND");
        original = original.replace("•", "@BULLET");
        original = original.replace("{", "@OPENBRACE");
        original = original.replace("}", "@CLOSEBRACE");
        original = original.replace("@", "@AT");
        original = original.replace("\t", "@TAB");
        original = original.replace("\n", "@NEWLINE");
        original = original.replace("\r", "@RETURN");
        original = original.replace("*", "@ASTERISK");
        original = original.replace(" ", "@SPACE");
        original = original.replace("-", "@DASH");
        original = original.replace("–", "@HYPHEN");
        return original;
    }

    /**
     * Convert file name formatted for validity back to original text
     * @param path Formatted file name
     * @return Original text
     */
    public static String undoPathComplianceReplacements(String path) {
        path = path.replace("@COMMA", ",");
        path = path.replace("@PERIOD", ".");
        path = path.replace("@GREATER", ">");
        path = path.replace("@LESS", "<");
        path = path.replace("@FORWARDSLASH", "/");
        path = path.replace("@QUESTION", "?");
        path = path.replace("@BACKSLASH", "\\");
        path = path.replace("@DIVISION", "÷");
        path = path.replace("@APOSTROPHE", "'");
        path = path.replace("@QUOTATION", "\"");
        path = path.replace("@SEMICOLON", ";");
        path = path.replace("@COLON", ":");
        path = path.replace("@OPENBRACE", "[");
        path = path.replace("@CLOSEBRACE", "]");
        path = path.replace("@CARET", "^");
        path = path.replace("@PERCENT", "%");
        path = path.replace("@POUND", "#");
        path = path.replace("@DOLLAR", "$");
        path = path.replace("@AMPERSAND", "&");
        path = path.replace("@BULLET", "•");
        path = path.replace("@OPENBRACE", "{");
        path = path.replace("@CLOSEBRACE", "}");
        path = path.replace("@AT", "@");
        path = path.replace("@TAB", "\t");
        path = path.replace("@NEWLINE", "\n");
        path = path.replace("@RETURN", "\r");
        path = path.replace("@ASTERISK", "*");
        path = path.replace("@SPACE", " ");
        path = path.replace("@DASH", "-");
        path = path.replace("@HYPHEN", "–");
        return path;
    }

    /**
     * Prepare text from a file for program parsing by removing comments and blank lines a user may have inserted
     * @param text Text to be prepared
     * @return Prepared text
     */
    public static String removeAnnotations(String text) {
        text = removeComments(text);
        text = removeBlankLines(text);
        return text;
    }

    /**
     * Remove all text between a # and the end of the line, inclusive
     * @param text Text with comments to remove
     * @return Text without the comments
     */
    public static String removeComments(String text) {
        while (text.contains("#")) {
            int poundIndex = text.indexOf("#");
            text = text.substring(0, poundIndex) + text.substring(text.indexOf(EOL, poundIndex) + 1, text.length());
        }
        return text;
    }

    /**
     * Remove all the blank lines in the text from a file
     * @param text Text from a file to remove blank lines from
     * @return The text without any blank lines
     */
    public static String removeBlankLines(String text) {
        String[] nonBlankLines = Utilities.stringSplit(text, EOL);
        String withoutBlankLines = "";
        for (String s : nonBlankLines)
            withoutBlankLines += s + EOL;
        return withoutBlankLines;
    }
}
