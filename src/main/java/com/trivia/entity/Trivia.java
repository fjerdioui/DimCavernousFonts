package com.trivia.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Trivia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long triviaId;
    private String question;
    private String correctAnswer;
    private int answerAttempts = 0;

    // Getters and Setters
    public Long getTriviaId() {
        return triviaId;
    }

    public void setTriviaId(Long triviaId) {
        this.triviaId = triviaId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public int getAnswerAttempts() {
        return answerAttempts;
    }

    public void setAnswerAttempts(int answerAttempts) {
        this.answerAttempts = answerAttempts;
    }
}
