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

import java.io.File;

public class Initializer {
    public static void main(String[] args) throws Exception {
        // Check for Pre-Existing File Structure; If it exists, ask user to delete it first
        String home = System.getProperty("user.home");
        if (FileUtilities.pathExists(home + "/Library/Application Support/EducationApplication/Root")) {
            UserInterfaceUtilities.displayMessage("Please remove the pre-existing file structure in " + home +
                    "/Library/Application Support/EducationApplication");
            System.exit(0);
        }

        // Search for Configuration File
        String pathToConfig = "";
        while (!FileUtilities.fileExists(pathToConfig)) {
            pathToConfig = UserInterfaceUtilities.getStringInput("Path to Configuration File: ");
        }

        // Create file structure
        String root = home + "/Library/Application Support/EducationApplication/Root";
        File f = new File(root);
        f.mkdirs();

        String configText = FileUtilities.fromFileCleaned(pathToConfig);
        String username = FileUtilities.findDataFromLabel(configText, "EmailUsername");
        String password = FileUtilities.findDataFromLabel(configText, "EmailPassword");

        FileUtilities.toFile("EmailUsername: " + username + FileUtilities.EOL +
                "EmailPassword: " + password + FileUtilities.EOL, root + "/config.txt");
        FileUtilities.toFile("", root + "/index.txt");

        String[] classes = Utilities.stringSplit(FileUtilities.findDataFromLabel(configText, "Classes"), '\t');
        for (String c : classes) {
            String classRoot = root + "/" + c;
            (new File(classRoot)).mkdir();
            String destination = FileUtilities.findDataFromLabel(configText, c + " EmailReportDestination");
            FileUtilities.toFile("", classRoot + "/index.txt");
            FileUtilities.toFile("EmailReportDestination: " + destination + FileUtilities.EOL, classRoot + "/config.txt");
            FileUtilities.appendFile(c + FileUtilities.EOL, root + "/index.txt");

            String[] students = Utilities.stringSplit(FileUtilities.findDataFromLabel(configText, c + " Students"), '\t');
            for (String s : students) {
                String studentRoot = classRoot + "/" + s;
                (new File(studentRoot)).mkdir();
                FileUtilities.toFile("", studentRoot + "/index.txt");
                FileUtilities.toFile(s + FileUtilities.EOL, studentRoot + "/id.txt");
                FileUtilities.appendFile(s + FileUtilities.EOL, classRoot + "/index.txt");
            }

            String[] categories = Utilities.stringSplit(FileUtilities.findDataFromLabel(configText, c + " Categories"), '\t');
            (new File(classRoot + "/Questions")).mkdir();
            FileUtilities.toFile("", classRoot + "/Questions/index.txt");
            for (String ca : categories) {
                String path = FileUtilities.findDataFromLabel(configText, c + " " + ca + " File");
                String questionText = FileUtilities.fromFile(path);

                FileUtilities.toFile(questionText, classRoot + "/Questions/" + ca + ".txt");
                FileUtilities.appendFile(ca + ".txt" + FileUtilities.EOL, classRoot + "/Questions/index.txt");
            }
        }
    }
}
