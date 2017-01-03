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

import java.util.ArrayList;
import java.util.List;

/**
 * Class of miscellaneous utilities
 */
public class Utilities {

    /**
     * Split a String based on a separation string into an array
     * @param toSplit String to split
     * @param breakPoint Separation string
     * @return Array of all terms that were separated by the separation strings
     */
    public static String[] stringSplit(String toSplit, String breakPoint) {
        ArrayList<String> split = new ArrayList<>();
        while (toSplit.contains(breakPoint)) {
            if (toSplit.indexOf(breakPoint) != 0)
                split.add(toSplit.substring(0, toSplit.indexOf(breakPoint)));
            toSplit = toSplit.substring(toSplit.indexOf(breakPoint) + 1, toSplit.length());
        }
        if (toSplit.length() != 0)
            split.add(toSplit);
        String[] splitArray = new String[split.size()];
        for (int i = 0; i < split.size(); i ++) {
            splitArray[i] = split.get(i);
        }
        return splitArray;
    }

    /**
     * Split a String based on a separation character into an array
     * @param toSplit String to split
     * @param breakPoint Separation character
     * @return Array of all terms that were separated by the separation characters
     */
    public static String[] stringSplit(String toSplit, char breakPoint) {
        return stringSplit(toSplit, "" + breakPoint);
    }

    /**
     * Determine whether an object is present in a List
     * @param lst List in which to search for the Object
     * @param target Object to search for
     * @return true if the object is in the list, false otherwise
     */
    public static boolean isPresent(List lst, Object target) {
        for (Object o : lst) {
            if (o.equals(target))
                return true;
        }
        return false;
    }

    /**
     * Determine whether an object is present in an array
     * @param lst Array in which to search for the Object
     * @param target Object to search for
     * @return true if the object is in the array, false otherwise
     */
    public static boolean isPresent(Object[] lst, Object target) {
        for (Object o : lst) {
            if (o.equals(target))
                return true;
        }
        return false;
    }
}
