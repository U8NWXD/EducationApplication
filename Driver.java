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

public class Driver {
    public static void main(String[] args) {
        try {
            Scanner s = new Scanner(System.in);

            Copyright c = new Copyright();

            UserInterfaceUtilities.displayMessage(c.getCopyright() + "\n\n" + c.getShortLicense() + '\n');

            // Search normal locations for root of directory tree or request and validate path from user
            String pathToRoot;
            // TODO: Change path specifications to support other OSes
            // TODO: Consider security and privacy implications of having to store email credentials in plaintext
            String home = System.getProperty("user.home");
            if (FileUtilities.pathExists(home + "/Library/Application Support/EducationApplication/Root"))
                pathToRoot = home + "/Library/Application Support/EducationApplication/Root";
            else {
                boolean valid = false;
                do {
                    System.out.print("Path to Root: ");
                    pathToRoot = s.nextLine();
                    if (!FileUtilities.pathExists(pathToRoot)) {
                        System.out.println("Sorry, but that is not a valid file path.");
                    } else {
                        valid = QuizzingEngine.isValid(pathToRoot);
                        if (valid) {
                            valid = true;
                        } else
                            System.out.println("Sorry, but the specified root does not contain a valid directory tree.");
                    }
                } while (!valid);
            }

            String[] classes = QuizzingEngine.getClasses(pathToRoot);
            String className = classes[UserInterfaceUtilities.menu("Select your Class", classes)];
            String pathToClassRoot = pathToRoot + "/" + className;

            String[] students = QuizzingEngine.getStudents(pathToClassRoot);
            String studentName = students[UserInterfaceUtilities.menu("Select your Name", students)];

            QuizzingEngine qe = new QuizzingEngine(pathToRoot, studentName, className);

            boolean mainLoop = true;

            while (mainLoop) {
                String[] mainMenu = {"Take Quiz", "View Progress", "View History", "Send History", "Send Progress",
                        "About", "View License", "Exit"};
                String selection = mainMenu[UserInterfaceUtilities.menu("Hi, " + qe.getStudent().getName() + ". " +
                        "You have a total percent mastery of " + qe.getPercentMastered() + "%. " +
                        "What would you like to do?", mainMenu)];

                if (selection.equals("Take Quiz")) {
                    String[] tags = qe.getAllTags();
                    int[] selectedTagIndices = UserInterfaceUtilities.getMultipleSelections(
                            "Which tags should be included?", tags);
                    String[] selectedTags = new String[selectedTagIndices.length];
                    for (int i = 0; i < selectedTags.length; i ++) {
                        selectedTags[i] = tags[selectedTagIndices[i]];
                    }
                    int numQuestions = UserInterfaceUtilities.getIntegerInput("Number of Questions: ", 1, qe.getQuestions().length);
                    StaticQuestionQuiz quiz = qe.getAutoStaticQuiz(numQuestions, selectedTags);

                    for (int i = 0; i < numQuestions; i ++) {
                        System.out.println(quiz.getNextQuestionText());
                        String ans = UserInterfaceUtilities.getStringInput("Answer: ");
                        try {
                            if (quiz.recordAnswer(ans)) {
                                UserInterfaceUtilities.displayMessage("Correct");
                            } else {
                                UserInterfaceUtilities.displayList("Incorrect. The correct answer(s): ", quiz.getLastCorrect());
                            }
                        } catch (Exception e) {
                            UserInterfaceUtilities.displayMessage("ERROR: Sorry, but something went wrong and your answer may" +
                                    " not have been saved.");
                            // TODO: Specific way to handle errors (display, cleanup, logging)
                        }
                    }
                } else if (selection.equals("View Progress")) {
                    String[] tags = qe.getAllTags();
                    // Table Headings: Tag, Percent Mastery
                    String[][] table = new String[tags.length + 1][3];
                    table[0][0] = "Tag";
                    table[0][1] = "Percent Mastery";
                    table[0][2] = "Progress Bar";

                    for (int i = 0; i < tags.length; i ++) {
                        int r = i + 1;
                        int percentMastery = (int) (qe.getStudent().getPercentMastered(tags[i]) * 100);
                        table[r][0] = tags[i];
                        table[r][1] = "" + percentMastery + "%";
                        table[r][2] = UserInterfaceUtilities.makeProgressBar(10, percentMastery / 100.0);
                    }
                    UserInterfaceUtilities.displayTable("Progress on All Tags", table);

                } else if (selection.equals("Exit")) {
                    mainLoop = false;
                } else if (selection.equals("View History")) {
                    String[] tags = qe.getAllTags();
                    int[] selectedTagIndices = UserInterfaceUtilities.getMultipleSelections(
                            "Which tags should be included?", tags);
                    String[] selectedTags = new String[selectedTagIndices.length];
                    for (int i = 0; i < selectedTags.length; i ++) {
                        selectedTags[i] = tags[selectedTagIndices[i]];
                    }
                    UserInterfaceUtilities.displayMessage(qe.getHistory(selectedTags));
                } else if (selection.equals("Send History")) {
                    String[] tags = qe.getAllTags();
                    int[] selectedTagIndices = UserInterfaceUtilities.getMultipleSelections(
                            "Which tags should be included?", tags);
                    String[] selectedTags = new String[selectedTagIndices.length];
                    for (int i = 0; i < selectedTags.length; i ++) {
                        selectedTags[i] = tags[selectedTagIndices[i]];
                    }
                    if (!qe.sendFullHistoryReport(selectedTags))
                        UserInterfaceUtilities.displayMessage("SEND FAILURE: Sorry, but the report failed to send for " +
                                "an unknown reason.");
                } else if (selection.equals("Send Progress")) {
                    if (!qe.sendProgressReport())
                        UserInterfaceUtilities.displayMessage("SEND FAILURE: Sorry, but the report failed to send for " +
                                "an unknown reason.");
                } else if (selection.equals("About")) {
                    UserInterfaceUtilities.displayMessage(c.getAbout());
                } else if (selection.equals("View License")) {
                    UserInterfaceUtilities.displayMessage(c.getLicense());
                } else
                    throw new Error("No if statement triggered in mainLoop");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("FATAL UNHANDLED ERROR");
            System.exit(1);
        }
    }
}
