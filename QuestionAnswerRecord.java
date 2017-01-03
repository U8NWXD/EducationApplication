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
 * Abstract object to record a single answer to a Question
 */
public abstract class QuestionAnswerRecord {
    private LocalDateTime timestamp;
    private Duration duration;
    private boolean correct;

    /**
     * Constructor that creates a new object from scratch
     * @param inDuration Time Student took answering question
     * @param inCorrect Whether the answer was correct
     */
    public QuestionAnswerRecord(Duration inDuration, boolean inCorrect) {
        timestamp = LocalDateTime.now();
        duration = inDuration;
        correct = inCorrect;
    }

    /**
     * Default constructor that initializes timestamp and duration to absurd values to be changed later (default is
     * for an incorrect answer)
     */
    public QuestionAnswerRecord() {
        timestamp = LocalDateTime.MIN;
        duration = Duration.ZERO;
        correct = false;
    }

    /**
     * Convert object into a String representation of itself
     * @return String representation of the QuestionAnswerRecord
     */
    public abstract String toString();

    /**
     * Get the timestamp of the answer
     * @return Timestamp of when the answer was submitted
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Set the timestamp of the answer
     * @param inTimestamp When the answer was submitted
     */
    public void setTimestamp(LocalDateTime inTimestamp) {
        timestamp = inTimestamp;
    }

    /**
     * Get the duration of the Student's answering of the question
     * @return How long it took the Student to answer the question
     */
    public Duration getDuration() {
        return duration;
    }

    /**
     * Set the duration of the answer
     * @param inDuration How long it took the Student to answer the question
     */
    public void setDuration(Duration inDuration) {
        duration = inDuration;
    }

    /**
     * Get whether the Student answered correctly
     * @return True if the Student was right, false if they were wrong
     */
    public boolean getCorrect() {
        return correct;
    }

    /**
     * Set whether the Student answered correctly
     * @param inCorrect Whether the student answered correctly
     */
    public void setCorrect(boolean inCorrect) {
        correct = inCorrect;
    }
}
