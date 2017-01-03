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

/**
 * Class used to interact with base functions
 */
public class QuizzingEngine {
    private String pathToRoot;
    private String pathToClass;
    private Student student;
    private Question[] questions;
    private Mailer mailer;

    /**
     * Constructor to create a new QuizzingEngine
     * @param inPathToRoot Path to the root of the file structure storing program files
     * @param inStudentName Name of the Student who is using the system
     * @param className Name of the Student's class
     * @throws Exception Thrown if file structure is invalid
     */
    public QuizzingEngine(String inPathToRoot, String inStudentName, String className) throws Exception {
        pathToRoot = inPathToRoot;
        pathToClass = pathToRoot + "/" + className;

        // Create questions
        ArrayList<Question> questionsList = new ArrayList<>();
        String questionsIndex = FileUtilities.fromFileCleaned(pathToClass + "/Questions/index.txt");
        String[] categories = Utilities.stringSplit(questionsIndex, FileUtilities.EOL);
        for (String category : categories) {
            String categoryText = FileUtilities.fromFileCleaned(pathToClass + "/Questions/" + category);
            String [] categoryLines = Utilities.stringSplit(categoryText, FileUtilities.EOL);
            for (int i = 1; i < categoryLines.length; i ++) {
                if (Question.isStatic(categoryLines[i]))
                    questionsList.add(new StaticQuestion(categoryLines[i]));
                else
                    throw new Exception("File line describing a Question does not match any type of Question");
            }
        }
        questions = new Question[questionsList.size()];
        questionsList.toArray(questions);
        student = new Student(pathToClass + "/" + inStudentName, questions);

        // Initialize Mailing System
        String rootConfigText = FileUtilities.fromFileCleaned(pathToRoot + "/" + "config.txt");
        String username = FileUtilities.findDataFromLabel(rootConfigText, "EmailUsername");
        String password = FileUtilities.findDataFromLabel(rootConfigText, "EmailPassword");
        String classConfigText = FileUtilities.fromFileCleaned(pathToClass + "/" + "config.txt");
        String destination = FileUtilities.findDataFromLabel(classConfigText, "EmailReportDestination");

        mailer = new Mailer(username, password, destination);
    }

    /**
     * Get the student's percent mastery
     * @return Student's percent mastery as an integer percentage (i.e 90 for 90%)
     */
    public int getPercentMastered() {
        return (int) Math.round(student.getPercentMastered()*100);
    }

    /**
     * Get an automatically generated quiz for the student based on one set of required tags
     * @param numToAsk Number of questions to request
     * @param tags Tags, all of which must be satisfied for a question to be included
     * @return Quiz for the Student to take
     */
    public StaticQuestionQuiz getAutoStaticQuiz(int numToAsk, String[] tags) {
        return new StaticQuestionQuiz(numToAsk, student, tags);
    }

    /**
     * Get an automatically generated quiz for the student based on multiple sets of required tags
     * @param numToAsk Number of questions to request
     * @param tagLists Tag lists, each of which will be fulfilled independently, and the combination of which will
     *                 create the question pool the quiz is chosen from
     * @return StaticQuestionQuiz for the Student to take
     */
    public StaticQuestionQuiz getAutoStaticQuiz(int numToAsk, String[][] tagLists) {
        StaticQuestionQuiz sqq = new StaticQuestionQuiz(numToAsk, student, tagLists[0]);
        for (int i = 1; i < tagLists.length; i ++) {
            sqq.moreTags(tagLists[i]);
        }
        return sqq;
    }

    /**
     * Get a StaticQuestionQuiz based on a file detailing a custom Quiz
     * @param pathToQuiz Path to the custom quiz
     * @return StaticQuestionQuiz for the Student to take
     * @throws Exception Thrown if the Quiz file is invalid or missing
     */
    public StaticQuestionQuiz getCustomStaticQuiz(String pathToQuiz) throws Exception {
        return new StaticQuestionQuiz(student, pathToQuiz);
    }

    /**
     * Check whether a Student exists
     * @param inName Name of Student
     * @return true if Student exists, false otherwise
     */
    public boolean checkStudentExistence(String inName, String pathToClass) {
        if (FileUtilities.pathExists(pathToClass + "/" + inName))
            return true;
        else
            return false;
    }

    /**
     * Get the Student using the QuizzingEngine
     * @return Student stored in QuizzingEngine
     */
    public Student getStudent() {
        return student;
    }

    /**
     * Get the list of Questions in the system
     * @return Array of all Questions in the system
     */
    public Question[] getQuestions() {
        return questions;
    }

    /**
     * Get all the tags used in the system
     * @return Array of all tags used in the system
     */
    public String[] getAllTags() {
        ArrayList<String> tags = new ArrayList<>();
        for (Question q : questions) {
            for (String tag : q.getTags()) {
                if (!Utilities.isPresent(tags, tag))
                    tags.add(tag);
            }
        }
        String[] tagsArray = new String[tags.size()];
        tags.toArray(tagsArray);
        return tagsArray;
    }

    /**
     * Get a string showing the user's response history to the questions that have the requested tags
     * @param tags Tags to use in searching for questions to include; all questions will have all tags
     * @return String showing the Student's response history
     */
    public String getHistory(String[] tags) {
        ArrayList<QuestionHistory> historiesToShow = new ArrayList<>();
        for (QuestionHistory qh : student.getQuestionHistories()) {
            if (qh.getQuestion().hasTags(tags))
                historiesToShow.add(qh);
        }
        String toReturn = "History of Responses: " + FileUtilities.EOL;
        for (QuestionHistory qh : historiesToShow) {
            toReturn += qh.toString() + FileUtilities.EOL;
        }
        return toReturn;
    }

    /**
     * Send a report containing the student's full response history to a given question
     * @return true if email sent successfully, false otherwise
     */
    public boolean sendFullHistoryReport(String[] tags) {
        return mailer.sendReport(getHistory(tags), student);
    }

    /**
     * Send a report containing a table of the student's progress on all tags
     * @return true if email sent successfully, false otherwise
     */
    public boolean sendProgressReport() {
        String[] tags = getAllTags();
        // Table Headings: Tag, Percent Mastery
        String[][] table = new String[tags.length + 1][3];
        table[0][0] = "Tag";
        table[0][1] = "Percent Mastery";
        table[0][2] = "Progress Bar";

        for (int i = 0; i < tags.length; i ++) {
            int r = i + 1;
            int percentMastery = (int) (student.getPercentMastered(tags[i]) * 100);
            table[r][0] = tags[i];
            table[r][1] = "" + percentMastery + "%";
            table[r][2] = UserInterfaceUtilities.makeProgressBar(10, percentMastery / 100.0);
        }
        return mailer.sendReport(UserInterfaceUtilities.makeTable(table), student);
    }

    /**
     * Check whether a directory structure and its files are valid
     * @param pathToRoot Path to the root of the directory tree
     * @return true if it is valid, false if it is not
     */
    public static boolean isValid(String pathToRoot) {
        try {
            String classIndexText = FileUtilities.fromFileCleaned(pathToRoot + "/index.txt");
            String[] classes = Utilities.stringSplit(classIndexText, FileUtilities.EOL);
            for (String className : classes) {
                String pathToClassRoot = pathToRoot + "/" + className;
                String studentIndexText = FileUtilities.fromFileCleaned(pathToClassRoot + "/index.txt");
                String[] students = Utilities.stringSplit(studentIndexText, FileUtilities.EOL);
                for (String s : students) {
                    new QuizzingEngine(pathToRoot, s, className);
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Check if a custom StaticQuestionQuiz is valid
     * @param pathToQuiz Path to the custom quiz file
     * @param student Student who will take the quiz
     * @return true if it is valid, false if it is not
     */
    public static boolean isValidStaticQuiz(String pathToQuiz, Student student) {
        try {
            new StaticQuestionQuiz(student, pathToQuiz);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Get an array of all classes in a directory tree
     * @param pathToRoot Path to the root of the directory tree
     * @return Array of all classes in the directory tree
     * @throws Exception Thrown if the index file is missing
     */
    public static String[] getClasses(String pathToRoot) {
        try {
            String indexText = FileUtilities.fromFileCleaned(pathToRoot + "/index.txt");
            return Utilities.stringSplit(indexText, FileUtilities.EOL);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return new String[0];
    }

    /**
     * Get an array of all students in a class
     * @param pathToClass Path to the class directory
     * @return Array of all students in a class
     * @throws Exception Thrown if the index file is missing
     */
    public static String[] getStudents(String pathToClass) {
        try {
            String indexText = FileUtilities.fromFileCleaned(pathToClass + "/index.txt");
            return Utilities.stringSplit(indexText, FileUtilities.EOL);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return new String[0];
    }

}
