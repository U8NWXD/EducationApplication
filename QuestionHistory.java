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
 * Abstract class to store the history of a Student's answers to a Question
 */
public abstract class QuestionHistory implements Comparable {
    private Question question;

    /**
     * Constructor to create new object from the Question it corresponds to
     * @param inQuestion Question the answers recorded are for
     */
    public QuestionHistory(Question inQuestion) {
        question = inQuestion;
    }

    /**
     * Get the Student's progress expressed as a decimal between 0 and 1, inclusive
     * @return Student's progress as a double in the interval [0,1]
     */
    public abstract double getPercentMastered();

    /**
     * Determine if another object is equal to this one
     * @param o Object to compare and determine equality to
     * @return True if the objects are equal, false otherwise
     */
    public abstract boolean equals(Object o);

    /**
     * Get the Question the history is for
     * @return Question the history is for
     */
    public Question getQuestion() {
        return question;
    }

    /**
     * Compare this QuestionHistory object to another based on Student progress
     * @param o Object to compare with this one
     * @return -1 if the parameter has a higher percent mastery, 0 if equal, 1 if lower
     */
    public int compareTo(Object o) {
        QuestionHistory qh = (QuestionHistory) o;
        if (getPercentMastered() < qh.getPercentMastered())
            return -1;
        else if (getPercentMastered() > qh.getPercentMastered())
            return 1;
        else
            return 0;
    }

    /**
     * Get a String representation of the object
     * @return String representation of the object
     */
    public abstract String toString();
}
