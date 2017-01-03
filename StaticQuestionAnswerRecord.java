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

/**
 * Object to record a single answer for a static question
 */
public class StaticQuestionAnswerRecord extends QuestionAnswerRecord {
    private String response;

    /**
     * Constructor to create a new object from scratch
     * @param inResponse Student's response
     * @param inDuration Time it took the Student to answer
     * @param inCorrect Whether the Student was right
     */
    public StaticQuestionAnswerRecord(String inResponse, Duration inDuration, boolean inCorrect) {
        super(inDuration, inCorrect);
        response = inResponse;
    }

    /**
     * Constructor to create a new object from a line in a file
     * @param fileLine Line in a file that records the Student's response
     */
    public StaticQuestionAnswerRecord(String fileLine) {
        String[] fields = Utilities.stringSplit(fileLine, '\t');
        setTimestamp(LocalDateTime.parse(fields[0]));
        setDuration(Duration.ofSeconds(Long.parseLong(fields[1])));
        if (fields[2].equals("r"))
            setCorrect(true);
        else
            setCorrect(false);
        response = fields[3];
    }

    /**
     * Get the Student's answer to the question
     * @return Student's answer to the question
     */
    public String getResponse() {
        return response;
    }

    /**
     * Get a filesystem-ready String representation of the object
     * @return Filesystem-ready String representation of the object
     */
    public String toString() {
        if (getCorrect())
            return getTimestamp().toString() + '\t' + getDuration().getSeconds() + '\t' + 'r' + '\t' + response;
        else
            return getTimestamp().toString() + '\t' + getDuration().getSeconds() + '\t' + 'w' + '\t' + response;
    }
}
