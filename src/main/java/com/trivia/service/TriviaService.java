package com.trivia.service;

import com.trivia.entity.Trivia;
import com.trivia.repository.TriviaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class TriviaService {

    @Autowired
    private TriviaRepository triviaRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String triviaApiUrl = "https://opentdb.com/api.php?amount=1";
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 2000;

    public Trivia startTrivia() {
        Map<String, Object> triviaData = fetchTriviaFromApiWithRetry();
        @SuppressWarnings("unchecked")
        Map<String, Object> results = (Map<String, Object>) ((List<?>) triviaData.get("results")).get(0);

        // Create new Trivia object
        Trivia trivia = new Trivia();
        trivia.setQuestion((String) results.get("question"));
        trivia.setCorrectAnswer((String) results.get("correct_answer"));
        trivia.setAnswerAttempts(0);

        // Save to the database
        triviaRepository.save(trivia);

        return trivia;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> fetchTriviaFromApiWithRetry() {
        // after getting from third party result : "429 Too Many Requests:
        // \"{\"response_code\":5,\"result\":[]}\""
        // we try after waiting 2sec to avoid the issue
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            try {
                return restTemplate.getForObject(triviaApiUrl, Map.class);
            } catch (HttpClientErrorException.TooManyRequests e) {
                if (attempt == MAX_RETRIES - 1) {
                    throw new RuntimeException("Trivia API responding with 429");
                }
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Error", interruptedException);
                }
            }
        }
        throw new RuntimeException("Still getting error after " + MAX_RETRIES + " retries");
    }

    public List<String> getPossibleAnswers(String correctAnswer, List<String> incorrectAnswers) {
        List<String> possibleAnswers = new ArrayList<>(incorrectAnswers);
        possibleAnswers.add(correctAnswer);
        Collections.shuffle(possibleAnswers);
        return possibleAnswers;
    }

    public String replyToTrivia(Long triviaId, String answer) {
        Trivia trivia = triviaRepository.findById(triviaId)
                .orElseThrow(() -> new RuntimeException("Question don't exist!"));

        if (trivia.getAnswerAttempts() >= 3) {
            return "Max attempts reached!";
        }

        if (trivia.getCorrectAnswer().equalsIgnoreCase(answer)) {
            // correct answer
            triviaRepository.delete(trivia);
            return "right!";
        } else {
            // incorrect answer
            trivia.setAnswerAttempts(trivia.getAnswerAttempts() + 1);
            triviaRepository.save(trivia);
            return "wrong!";
        }
    }
}
