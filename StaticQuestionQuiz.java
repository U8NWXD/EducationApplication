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
import java.util.Collections;

/**
 * Quiz made up of StaticQuestions for a Student to take
 */
public class StaticQuestionQuiz extends Quiz {
    private ArrayList<StaticQuestion> quiz;             // Just the StaticQuestions to be asked
    private ArrayList<StaticQuestionHistory> pool;      // All QuestionHistories that have the needed tags and are Static
    private int numToAsk;
    private int quizIndex;
    private LocalDateTime start;

    /**
     * Constructor to create a new StaticQuestionQuiz from a file detailing a custom StaticQuestionQuiz
     * @param inStudent Student who will take the quiz
     * @param pathToCustomQuiz Filepath to the file describing a custom quiz
     * @throws Exception Thrown in case of an invalid file path
     */
    public StaticQuestionQuiz(Student inStudent, String pathToCustomQuiz) throws Exception {
        super(inStudent);
        quiz = new ArrayList<>();
        quizIndex = 0;

        String[] questionTexts = Utilities.stringSplit(FileUtilities.fromFileCleaned(pathToCustomQuiz), FileUtilities.EOL);
        for (QuestionHistory qh : getStudent().getQuestionHistories()) {
            if (qh instanceof StaticQuestionHistory) {
                StaticQuestionHistory sqh = (StaticQuestionHistory) qh;
                for (String questionText : questionTexts) {
                    if (sqh.getQuestion().getQuestionText().equals(questionText))
                        quiz.add((StaticQuestion) sqh.getQuestion());
                }
            }
        }
    }

    /**
     * Constructor to create a new StaticQuestionQuiz
     * @param inNumToAsk Number of Questions to ask
     * @param inStudent Student who will be taking the Quiz
     * @param tags Array of tags, each of which will be present in each Question asked of the Student
     *             An empty Array signals no restriction by tag
     */
    public StaticQuestionQuiz(int inNumToAsk, Student inStudent, String[] tags) {
        super(inStudent);
        quiz = new ArrayList<>();
        quizIndex = 0;
        numToAsk = inNumToAsk;
        pool = new ArrayList<>();

        for (QuestionHistory qh : getTaggedQuestionHistories(tags)) {
            if (qh instanceof StaticQuestionHistory)
                pool.add((StaticQuestionHistory) qh);
        }

        makeQuiz();
    }

    /**
     * Search the Student's QuestionHistories based on a new set of tags (all tags must be present for a Question to
     * be included), add the resulting questions (without duplication) to the pool, and re-create the quiz from that
     * pool
     * @param tags
     */
    public void moreTags(String[] tags) {
        for (QuestionHistory qh : getTaggedQuestionHistories(tags)) {
            if (qh instanceof StaticQuestionHistory && pool.indexOf(qh) == -1)
                pool.add((StaticQuestionHistory) qh);
        }
        makeQuiz();
    }

    /**
     * Based on the current pool, select numToAsk questions from the pool and put them in the quiz
     */
    private void makeQuiz() {
        quiz.clear();
        Collections.sort(pool);

        int quizSize;
        if (numToAsk > pool.size())
            quizSize = pool.size();
        else
            quizSize = numToAsk;

        for (int i = 0; i < quizSize; i ++)
            quiz.add((StaticQuestion) pool.get(i).getQuestion());

        Collections.shuffle(quiz);
    }

    /**
     * Get the text of the next StaticQuestion to ask
     * @return Text of the next question to ask
     */
    public String getNextQuestionText() {
        String text = quiz.get(quizIndex).getQuestionText();
        start = LocalDateTime.now();
        return text;
    }

    /**
     * Record and check the answer provided by the Student
     * @param answer The Student's answer to the Question
     * @return True if they were right, false otherwise
     * @throws Exception Thrown if the file in which the answer will be recorded does not exist
     */
    public boolean recordAnswer(String answer) throws Exception {
        LocalDateTime end = LocalDateTime.now();
        Duration duration = Duration.between(start, end);
        boolean correctness = quiz.get(quizIndex).checkAnswer(answer);
        ArrayList<QuestionHistory> histories = getStudent().getQuestionHistories();
        for (QuestionHistory qh : histories) {
            if (qh.getQuestion().equals(quiz.get(quizIndex)))
                // TODO: Handle invalid file names in question texts
                ((StaticQuestionHistory) qh).addResponse(answer, duration, correctness, getStudent().getFilepath()
                        + "/" + quiz.get(quizIndex).getQuestionText() + ".txt");
        }
        quizIndex ++;
        return correctness;
    }

    /**
     * Get the correct answers for the last answered question
     * @return Array of correct answers for the last answered question
     */
    public String[] getLastCorrect() {
        return quiz.get(quizIndex - 1).getCorrectAnswers();
    }

    /**
     * Get the questions in the quiz
     * @return The questions in the quiz, in order
     */
    public ArrayList<StaticQuestion> getQuiz() {
        return quiz;
    }
}
