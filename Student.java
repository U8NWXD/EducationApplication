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
 * Object that represents a Student
 */
public class Student {
    private String name;
    private ArrayList<QuestionHistory> questionHistories;
    private String filepath;

    /**
     * Constructor that creates a new Student
     * @param inName
     */
    public Student(String inName, String inFilepath) {
        name = inName;
        questionHistories = new ArrayList<>();
        filepath = inFilepath;
    }

    /**
     * Constructor that creates a Student from their filesystem records
     * @param inPath Path to the root of the Student's directory
     * @param inQuestionList List of all the Questions in the System
     * @throws Exception Thrown if the Student's records are improperly formatted or missing
     */
    public Student(String inPath, Question[] inQuestionList) throws Exception {
        filepath = inPath;
        Question[] questionList = inQuestionList.clone();
        // Get name from id.txt
        String idText = FileUtilities.fromFileCleaned(filepath + "/id.txt");
        name = idText.substring(0, idText.indexOf(FileUtilities.EOL));
        // Make QuestionHistories from existing QuestionHistory files
        String indexText = FileUtilities.fromFileCleaned(filepath + "/index.txt");
        String[] questionHistoryFiles = Utilities.stringSplit(indexText, FileUtilities.EOL);
        questionHistories = new ArrayList<>();
        for (String fileName : questionHistoryFiles) {
            String questionText = fileName.substring(0, fileName.length() - 4);
            Question question = null;
            for (int i = 0; i < questionList.length; i ++) {
                if (questionList[i] != null) {
                    Question currQ = questionList[i];
                    if (currQ.getQuestionText().equals(questionText)) {
                        question = currQ;
                        // Replace the question in the array with null to mark it as found
                        questionList[i] = null;
                    }
                }
            }

            if (question instanceof StaticQuestion) {
                QuestionHistory qh = new StaticQuestionHistory((StaticQuestion) question, filepath + "/" + fileName);
                questionHistories.add(qh);
            } else if (question == null) {
                throw new Exception("No Question found for QuestionHistory file " + filepath + "/" + fileName);
            } else {
                throw new Exception("Unknown question type found in QuestionHistory file " + filepath + "/" + fileName);
            }
        }
        // Create blank QuestionHistory files and objects for those questions without a QuestionHistory file already
        for (Question q : questionList) {
            // If a question is not null (meaning it was not used already), create a blank file and object for it
            if (q != null) {
                if (q instanceof StaticQuestion) {
                    // Create history file
                    FileUtilities.toFile("Timestamp\tDuration\tCorrectness\tResponse" + FileUtilities.EOL, filepath + "/" +
                            q.getQuestionText() + ".txt");
                    // Add filename to end of index file
                    FileUtilities.appendFile(q.getQuestionText() + ".txt" + FileUtilities.EOL, filepath + "/" + "index.txt");
                } else
                    throw new Exception("Question " + q.toString() + " is not of a known type");
            }
        }
    }

    /**
     * Get the student's progress as a decimal in the interval [0,1]
     * @return Average of the Student's progress on all Questions
     */
    public double getPercentMastered() {
        double sum = 0;
        int count = 0;

        for (QuestionHistory qh : questionHistories) {
            sum += qh.getPercentMastered();
            count ++;
        }

        return sum / count;
    }

    /**
     * Get the average percentMastered value of those questions containing the specified tag
     * @param tag Tag to require in all questions whose percentMastered values will be included in the average
     * @return Average percentMastered values of those questions containing the specified tag
     */
    public double getPercentMastered(String tag) {
        double sum = 0;
        int count = 0;

        for (QuestionHistory qh : questionHistories) {
            if (qh.getQuestion().hasTag(tag)) {
                sum += qh.getPercentMastered();
                count ++;
            }
        }

        return sum / count;
    }

    /**
     * Get all the Student's QuestionHistories
     * @return The Student's answer records
     */
    public ArrayList<QuestionHistory> getQuestionHistories() {
        return questionHistories;
    }

    /**
     * Get path to the Student's records directory
     * @return Path to root of Student's directory
     */
    public String getFilepath() {
        return filepath;
    }

    /**
     * Get name of Student
     * @return Name of Student
     */
    public String getName() {
        return name;
    }
}
