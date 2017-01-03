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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Object for storing the history of a Student's answers to a StaticQuestion
 */
public class StaticQuestionHistory extends QuestionHistory {
    private ArrayList<StaticQuestionAnswerRecord> responses;

    /**
     * Constructor to create a new object from scratch
     * @param inQuestion Question whose history will be recorded
     */
    public StaticQuestionHistory(StaticQuestion inQuestion) {
        super(inQuestion);
        responses = new ArrayList<>();
    }

    /**
     * Constructor to create a new object from the file system
     * @param inQuestion Question whose history will be recorded
     * @param filepath Path to the file that holds a representation of the history
     * @throws Exception Thrown if the file does not exist or the path is invalid
     */
    public StaticQuestionHistory(StaticQuestion inQuestion, String filepath) throws Exception {
        super(inQuestion);
        String fileText = FileUtilities.fromFileCleaned(filepath);
        String[] fileLines = Utilities.stringSplit(fileText, FileUtilities.EOL);
        responses = new ArrayList<>();
        for (int i = 1; i < fileLines.length; i ++) {
            responses.add(new StaticQuestionAnswerRecord(fileLines[i]));
        }
    }

    /**
     * Add a Student's answer to the history
     * @param response Student's answer
     * @param duration Time the Student spent on the Question
     * @param correct Whether the Student was right
     * @param path Path to the file that holds a representation of the history
     * @throws Exception Thrown if the file does not exist or the path is invalid
     */
    public void addResponse(String response, Duration duration, boolean correct, String path) throws Exception {
        StaticQuestionAnswerRecord sqr = new StaticQuestionAnswerRecord(response, duration, correct);
        responses.add(sqr);
        String newLine = sqr.toString() + FileUtilities.EOL;
        FileUtilities.toFile(FileUtilities.fromFileCleaned(path) + newLine, path);
    }

    /**
     * Get the Student's overall progress expressed as a double in the interval [0,1]
     * @return The average of the percentMastery on each individual StaticQuestion
     */
    public double getPercentMastered() {
        if (responses.size() == 0)
            return 0;
        // TODO: Include duration in calculation (Maybe need to include expected time?)
        double percentCorrect = getPercentCorrect(7);       // Only last 7 days' responses used
        int wait = getWaitingTime();
        if (wait == 0)
            return 0;   // Never right before
        double percentMastery = percentCorrect * (1 - (getDaysSinceLastCorrect() / (2 * wait)));
        if (getDaysSinceLastCorrect() == Integer.MAX_VALUE)
            return 0;
        else if (percentMastery < 0.0)
            return 0;
        else
            return percentMastery;
    }

    /**
     * Get the number of days since the question was last answered correctly
     * @return Number of days since the question was last correctly answered, or MAX_VALUE if it was always missed
     */
    public int getDaysSinceLastCorrect() {
        for (int i = responses.size() - 1; i <= 0; i ++) {
            StaticQuestionAnswerRecord ar = responses.get(i);
            if (ar.getCorrect()) {
                return (int) Duration.between(ar.getTimestamp(), LocalDateTime.now()).toDays();
            }
        }
        return Integer.MAX_VALUE;
    }

    /**
     * Get the interval to wait until asking the question again
     * Calculated as Int((Longest span between consecutive corrects) * 1.2 + 1)
     * @return Days to wait until asking the question again
     */
    public int getWaitingTime() {
        int maxSpan = 0;
        StaticQuestionAnswerRecord last = null;
        for (StaticQuestionAnswerRecord ar : responses) {
            if (last == null && ar.getCorrect())
                last = ar;
        }
        if (last == null) {
            return 0;
        }
        for (StaticQuestionAnswerRecord ar : responses) {
            if (ar.getCorrect()) {
                int span = Math.abs((int) Duration.between(ar.getTimestamp(), last.getTimestamp()).toDays());
                if (span > maxSpan) {
                    maxSpan = span;
                }
                last = ar;
            }
        }
        return (int) (maxSpan * 1.2) + 1;
    }

    /**
     * Get the percentage of all responses less than or equal to maxDaysAgo days ago that were right
     * @param maxDaysAgo Number of days ago to include in search
     * @return Percentage of all responses less than or equal to maxDaysAgo days ago that were right as a double,
     * 0 if no responses, in the interval [0,1]
     */
    public double getPercentCorrect(int maxDaysAgo) {
        double right = 0;
        double wrong = 0;

        for (StaticQuestionAnswerRecord ar : responses) {
            int daysAgo = (int) Duration.between(ar.getTimestamp(), LocalDateTime.now()).toDays();
            if (ar.getCorrect() && daysAgo <= maxDaysAgo)
                right ++;
            else if (!ar.getCorrect() && daysAgo <= maxDaysAgo)
                wrong ++;
        }

        return right / (right + wrong);
    }

    /**
     * Determine if another object is equal to this one
     * @param o Object to compare and determine equality to
     * @return True if the objects are equal, false otherwise
     */
    public boolean equals(Object o) {
        if (o instanceof QuestionHistory) {
            QuestionHistory qh = (QuestionHistory) o;
            if (qh.getQuestion().equals(getQuestion()))
                return true;
            else
                return false;
        } else
            return false;
    }

    /**
     * Get a String representation of the object
     * @return String representation of the object
     */
    public String toString() {
        String toReturn = "StaticQuestionHistory for [" + getQuestion().toString() + "]" + FileUtilities.EOL;
        for (StaticQuestionAnswerRecord ans : responses)
            toReturn += ans.toString() + FileUtilities.EOL;
        return toReturn;
    }
}
