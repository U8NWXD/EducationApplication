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

/**
 * Question object that is static: the question text and correct answers do not change
 */
public class StaticQuestion extends Question {
    private String[] correctAnswers;
    private String question;
    private boolean ignoreCapitalization;
    private char[] ignoreChars;

    /**
     * Constructor that creates a new StaticQuestion from scratch
     * @param inCorrect List of the correct answers
     * @param inQuestion Text of the StaticQuestion
     * @param inIgnoreCaps Whether or not to ignore capitalization in the answer
     * @param inIgnoreChars Which characters will be ignored in the answer
     */
    public StaticQuestion(String[] inCorrect, String inQuestion, boolean inIgnoreCaps, char[] inIgnoreChars) {
        correctAnswers = inCorrect;
        question = inQuestion;
        ignoreCapitalization = inIgnoreCaps;
        ignoreChars = inIgnoreChars;
    }

    /**
     * Constructor to create a new StaticQuestion object from a line in the file used to store the Question
     * @param fileLine Line of a file that represents the StaticQuestion
     */
    public StaticQuestion(String fileLine) {
        // Get correct answers from inside braces
        String correctAnswerString = fileLine.substring(fileLine.indexOf('{') + 1, fileLine.indexOf('}'));
        correctAnswers = Utilities.stringSplit(correctAnswerString, '\t');

        // Get fields that come before the braces
        String[] fields = Utilities.stringSplit(fileLine.substring(0, fileLine.indexOf('{')), '\t');
        // 2nd Field is y/n for ignoring capitalization
        if (fields[1].equals('y'))
            ignoreCapitalization = true;
        else
            ignoreCapitalization = false;
        // 3rd Field contains all characters to ignore
            // Cannot ignore tab characters
            // ** indicates null in filesystem
        if (fields[2].equals("**")) {
            ignoreChars = new char[0];
        } else {
            ignoreChars = new char[fields[2].length()];
            for (int i = 0; i < fields[2].length(); i ++) {
                ignoreChars[i] = fields[2].charAt(i);
            }
        }

        // 4th Field contains question text
        question = fields[3];

        // Get the tags that are listed after the braces
        String[] tags = Utilities.stringSplit(fileLine.substring(fileLine.indexOf('}') + 1), '\t');
        for (String tag : tags) {
            addTag(tag);
        }
        addTag("StaticQuestion");
    }

    /**
     * Get the text of the StaticQuestion
     * @return Text of the StaticQuestion
     */
    public String getQuestionText() {
        return question;
    }

    /**
     * Check the answer provided by the Student
     * @param answer Student-provided answer
     * @return True if the answer is true, false otherwise
     */
    public boolean checkAnswer(String answer) {
        if (ignoreCapitalization)
            answer = answer.toLowerCase();
        for (char toIgnore : ignoreChars) {
            answer = answer.replace(toIgnore + "", "");
        }
        for (String correct : correctAnswers) {
            String correctChanged = correct;
            if (ignoreCapitalization)
                correctChanged = correctChanged.toLowerCase();
            for (char toIgnore : ignoreChars) {
                correctChanged = correctChanged.replace(toIgnore + "", "");
            }
            if (correctChanged.equals(answer))
                return true;
        }
        return false;
    }

    /**
     * Check to see if another object is equal to the StaticQuestion
     * @param o Object whose equality with the Question will be checked
     * @return True if they are equal, false otherwise
     */
    public boolean equals(Object o) {
        if (o instanceof StaticQuestion) {
            StaticQuestion q = (StaticQuestion) o;
            if (q.getQuestionText().equals(getQuestionText()))
                return true;
            else
                return false;
        }
        else
            return false;
    }

    /**
     * Convert the StaticQuestion object into a String
     * @return String that represents the instance fields of the object
     */
    public String toString() {
        String correctAnswersString = "";
        for (String ans : correctAnswers)
            correctAnswersString += ans + '\t';

        String tagsString = "";
        for (String tag : getTags())
            tagsString += tag + "\t";

        String charsIgnoreString = "";
        for (char ig : ignoreChars)
            charsIgnoreString += ig;

        String ignoreCaps;
        if (ignoreCapitalization)
            ignoreCaps = "y";
        else
            ignoreCaps = "n";

        return "s\t" + ignoreCaps + '\t' + charsIgnoreString + '\t' + question + '\t' + '{' + correctAnswersString
                + '}' + '\t' + tagsString;
    }

    /**
     * Get the correct answers
     * @return Array of correct answers
     */
    public String[] getCorrectAnswers() {
        return correctAnswers;
    }
}
