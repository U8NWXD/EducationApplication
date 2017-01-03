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
 * Abstract class that covers all Quizzes
 */
public abstract class Quiz {
    private Student student;

    /**
     * Constructor that creates a new Quiz based on the Student taking it
     * @param inStudent Student taking the Quiz
     */
    public Quiz(Student inStudent) {
        student = inStudent;
    }

    /**
     * Get those QuestionHistory objects of the Student's that have the necessary tags
     * @param inTags Array of tags, each of which will be present in each Question asked of the Student
     *             An empty Array signals no restriction by tag
     * @return Array of QuestionHistory objects whose questions have the requested tags
     */
    public QuestionHistory[] getTaggedQuestionHistories(String[] inTags) {
        ArrayList<QuestionHistory> validQuestionHistories = new ArrayList<>();
        for (QuestionHistory qh : student.getQuestionHistories()) {
            if (qh.getQuestion().hasTags(inTags))
                validQuestionHistories.add(qh);
        }

        QuestionHistory[] toReturn = new QuestionHistory[validQuestionHistories.size()];

        return validQuestionHistories.toArray(toReturn);
    }

    /**
     * Get the next Question text to show the Student
     * @return Text of the next Question to show the Student
     */
    public abstract String getNextQuestionText();

    /**
     * Record and check the answer provided by the Student
     * @param answer The Student's answer to the Question
     * @return True if they were right, false otherwise
     * @throws Exception Thrown if the file in which the answer will be recorded does not exist
     */
    public abstract boolean recordAnswer(String answer) throws Exception;

    /**
     * Get the Student taking the Quiz
     * @return Student taking the Quiz
     */
    public Student getStudent() {
        return student;
    }
}
