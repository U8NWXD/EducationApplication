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

import java.util.Scanner;

public class UserInterfaceUtilities {

    /**
     * Print a menu of the provided options and take the user's selection
     * @param prompt Text to show as a prompt to the user
     * @param options List of options to include in the menu
     * @return Integer index of the user's selection in the provided String array
     */
    public static int menu(String prompt, String[] options) {
        Scanner s = new Scanner(System.in);

        System.out.println('\n');
        System.out.println(prompt);

        for (int i = 0; i < options.length; i ++) {
            int num = i + 1;
            System.out.println(num + ".\t" + options[i]);
        }
        while (true) {
            System.out.print("Selection: ");
            String selection = s.nextLine();
            for (int i = 0; i < options.length; i ++) {
                if (options[i].toLowerCase().equals(selection.toLowerCase()) || selection.equals("" + (i + 1)))
                    return i;
            }
            System.out.println("Sorry, I cannot understand that. Please enter the name of an option or its number.");
        }
    }

    /**
     * Take an integer as user input and continue asking until an integer is provided
     * @param prompt Text to be displayed next to the user's input
     * @return The integer entered by the user
     */
    public static int getIntegerInput(String prompt) {
        Scanner s = new Scanner(System.in);

        while (true) {
            System.out.print(prompt);
            String input = s.nextLine();
            try {
                return Integer.parseInt(input);
            } catch (Exception e) {
                System.out.println("Sorry, I couldn't understand that. Please enter an integer.");
            }
        }
    }

    /**
     * Take an integer as user input and require an integer between min and max inclusive
     * @param prompt Text to be displayed next to the user's input
     * @param min Minimum value to be accepted
     * @param max Maximum value to be accepted
     * @return Integer entered by the user
     */
    public static int getIntegerInput(String prompt, int min, int max) {
        Scanner s = new Scanner(System.in);

        while (true) {
            System.out.print(prompt);
            String input = s.nextLine();
            try {
                int num = Integer.parseInt(input);
                if (num >= min && num <= max)
                    return num;
                else
                    System.out.println("Sorry, but that integer is invalid. Please enter an integer between " + min +
                    " and " + max + ", inclusive.");
            } catch (Exception e) {
                System.out.println("Sorry, I couldn't understand that. Please enter an integer.");
            }
        }
    }

    /**
     * Get string input from the user
     * @param prompt Text displayed next to the user's input
     * @return String entered by the user
     */
    public static String getStringInput(String prompt) {
        Scanner s = new Scanner(System.in);
        System.out.print(prompt);
        return s.nextLine();
    }

    /**
     * Get the indices of a user's selections from a provided list
     * @param prompt Prompt to display to the user
     * @param options List of options to show the user
     * @return Indices of the user's selections from the provided list
     */
    public static int[] getMultipleSelections(String prompt, String[] options) {
        Scanner s = new Scanner(System.in);
        System.out.println(prompt);
        for (int i = 1; i <= options.length; i ++) {
            System.out.println(i + ". " + options[i-1]);
        }
        boolean valid = false;
        int[] selectionIndices = new int[0];
        while (!valid) {
            System.out.println("Below, enter your selection. Separate multiple selections by commas.");
            System.out.print("Selection(s): ");
            String selectionsString = s.nextLine();
            selectionsString = selectionsString.replace(" ", "");
            String[] selections = Utilities.stringSplit(selectionsString, ',');
            selectionIndices = new int[selections.length];
            valid = true;
            for (int i = 0; i < selections.length; i ++) {
                boolean found = false;
                String selection = selections[i];
                for (int o = 0; o < options.length; o ++) {
                    String option = options[o].replace(" ", "");
                    if (option.toLowerCase().equals(selection.toLowerCase()) || selection.equals("" + (o + 1))) {
                        found = true;
                        selectionIndices[i] = o;
                        o = options.length;
                    }
                }
                if (!found) {
                    valid = false;
                    System.out.println("Sorry, but I couldn't understand the following selection: " + selections[i]);
                    i = selections.length;
                }
            }
        }
        return selectionIndices;
    }

    /**
     * Display a message to the user
     * @param message Message to display
     */
    public static void displayMessage(String message) {
        System.out.println('\n');
        System.out.println(message);
    }

    /**
     * Display a list of texts to the user
     * @param title Title of the list
     * @param list Array of strings to show
     */
    public static void displayList(String title, String[] list) {
        System.out.println('\n');
        System.out.println(title);
        for (String s : list) {
            System.out.println('\t' + s);
        }
    }

    /**
     * Ask a user to confirm or cancel an action
     * @param prompt Prompt to show the user
     * @return true if the user chose to confirm, false otherwise
     */
    public static boolean confirmOrCancel(String prompt) {
        String[] options = {"Confirm", "Cancel"};
        return menu(prompt, options) == 0;
    }

    /**
     * Ask a user a yes or no question
     * @param prompt Prompt to show the user
     * @return true if the user chose yes, false if they chose no
     */
    public static boolean yesOrNo(String prompt) {
        String[] options = {"Yes", "No"};
        return menu(prompt, options) == 0;
    }

    /**
     * Display the provided table
     * @param title Title of the table
     * @param table Table to display
     */
    public static void displayTable(String title, String[][] table) {
        System.out.println('\n');
        System.out.println(title);
        System.out.println(makeTable(table));
    }

    /**
     * Create a table out of plaintext characters
     * @param table Table to render in text
     * @return Provided table rendered in plaintext
     */
    public static String makeTable(String[][] table) {
        int width = 76;
        int[] maxLengths = new int[table[0].length];

        for (int r = 0; r < table.length; r ++) {
            for (int c = 0; c < table[0].length; c ++) {
                int len = table[r][c].length();
                if (len > maxLengths[c])
                    maxLengths[c] = len;
            }
        }

        while (sumInts(maxLengths) > width) {
            int maxIndex = 0;
            for (int i = 0; i < maxLengths.length; i ++) {
                if (maxLengths[i] > maxLengths[maxIndex])
                    maxIndex = i;
            }

            maxLengths[maxIndex] --;
        }

        String template = "";
        for (int i : maxLengths)
            template += "%-" + i + "s|";
        template = template.substring(0, template.length() - 1);
        template += "%n";

        String formatted = FormatLineWithWrap(template, maxLengths, table[0]);
        for (int i = 0; i < sumInts(maxLengths) + maxLengths.length - 1; i ++)
            formatted += "-";
        formatted += '\n';

        for (int i = 1; i < table.length; i ++)
            formatted += FormatLineWithWrap(template, maxLengths, table[i]);

        return formatted;
    }

    /**
     * Create a line of a plaintext table with wrapping cells
     * @param format String with formatting information for String.format
     * @param columnWidths Array of the widths of each column
     * @param items Array of items to put in the line
     * @return Line (or lines if wrapped) to add to the bottom of the plaintext table
     */
    private static String FormatLineWithWrap(String format, int[] columnWidths, String[] items) {
        int maxWrapping = 0;

        // Determine the maximum wrapping needed, and thus the dimensions of the map array
        for (int i = 0; i < items.length; i ++) {
            String item = items[i];
            int wrapping = item.length() / (columnWidths[i] + 1);
            if (wrapping > maxWrapping)
                maxWrapping = wrapping;
        }

        String[][] map = new String[maxWrapping + 1][items.length];

        // Fill the map array with all parts of all items
        for (int c = 0; c < items.length; c ++) {
            String item = items[c];
            int r = 0;
            for (int i = 0; i < item.length(); i += columnWidths[c]) {
                int end = Math.min(i + columnWidths[c], item.length());
                String cell = item.substring(i, end);
                map[r][c] = cell;
                r ++;
            }
        }

        // Fill the rest of the map array with empty strings
        for (int r = 0; r < map.length; r ++) {
            for (int c = 0; c < map[0].length; c ++) {
                if (map[r][c] == null)
                    map[r][c] = "";
            }
        }

        // Transform map into string table
        String formatted = "";

        for (String[] line : map) {
            formatted += String.format(format, (Object[]) line);
        }

        return formatted;
    }

    /**
     * Sum up the lengths of all the strings in an array
     * @param items Array of strings whose lengths will be summed
     * @return Sum of the lengths of the strings in the array
     */
    public static int sumStringLengths(String[] items) {
        int sum = 0;
        for (String s : items) {
            sum += s.length();
        }
        return sum;
    }

    /**
     * Sum up the integers in the array
     * @param nums Array of ints to be summed
     * @return Sum of the ints in the array
     */
    public static int sumInts(int[] nums) {
        int sum = 0;
        for (int i : nums) {
            sum += i;
        }
        return sum;
    }

    /**
     * Create a progress bar from plaintext characters
     * @param insideLength Length of the bar itself (actual length 2 characters more from end brackets)
     * @param progress Progress to display as a double in the range [0,1]
     * @return Progress bar made from characters to display
     */
    public static String makeProgressBar(int insideLength, double progress) {
        if (progress > 1)
            progress = 1;
        int numBars = (int) (insideLength * progress);
        String bar = "[";
        for (int i = 0; i < numBars; i ++)
            bar += "=";
        for (int i = 0; i < insideLength - numBars; i ++)
            bar += " ";
        bar += "]";
        return bar;
    }
}
