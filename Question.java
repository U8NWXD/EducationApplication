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
 * Abstract class that covers all Questions: Dynamic and Static
 */
public abstract class Question {
    private ArrayList<String> tags;

    /**
     * Default constructor that initializes tags to a blank ArrayList
     */
    public Question() {
        tags = new ArrayList<>();
        tags.add("Question");
    }

    /**
     * Abstract method requiring all Questions to be able to return the text of the question
     * @return Text of the question to be displayed to the user
     */
    public abstract String getQuestionText();

    /**
     * Abstract method requiring all Questions to be able to check the answer provided by the Student
     * @param answer Student-provided answer
     * @return True if the answer was right, false if it was not
     */
    public abstract boolean checkAnswer(String answer);

    /**
     * Abstract method requiring all Questions to be able to determine equality with another object
     * @param o Object whose equality with the Question will be checked
     * @return True if the objects are equal, false otherwise
     */
    public abstract boolean equals(Object o);

    /**
     * Get the List of tags
     * @return List of the tags for the question
     */
    public ArrayList<String> getTags() {
        return tags;
    }

    /**
     * Add a tag to the Question
     * @param newTag The tag to add
     */
    public void addTag(String newTag) {
        tags.add(newTag);
    }

    /**
     * Abstract method requiring all Questions to be able to represent themselves as a String
     * @return String representation of the Question
     */
    public abstract String toString();

    /**
     * Check if the Question has the parameter tag associated with it
     * @param target Tag to search for
     * @return True if the tag is associated with the Question, false if it is not
     */
    public boolean hasTag(String target) {
        for (String tag : tags)
            if (tag.equals(target))
                return true;
        return false;
    }

    /**
     * Check if the Question has all the parameter tags
     * @param targets Array of all the tags the Question must have for a true result
     * @return True if the Question has all the tags in the parameter, false otherwise
     */
    public boolean hasTags(String[] targets) {
        for (String tag : targets) {
            if (!hasTag(tag))
                return false;
        }
        return true;
    }

    /**
     * Check if a file line describes a StaticQuestion
     * @param fileLine Line of a file describing the Question
     * @return true if the line describes a StaticQuestion, false otherwise
     */
    public static boolean isStatic(String fileLine) {
        if (fileLine.charAt(0) == 's')
            return true;
        else
            return false;
    }
}
