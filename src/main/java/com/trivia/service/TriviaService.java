package com.trivia.service;

import com.trivia.entity.Trivia;
import com.trivia.repository.TriviaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class TriviaService {

    @Autowired
    private TriviaRepository triviaRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String triviaApiUrl = "https://opentdb.com/api.php?amount=1";

    public Trivia startTrivia() {
        Map<String, Object> triviaData = fetchTriviaFromApi();
        Map<String, Object> results = (Map<String, Object>) ((List<?>) triviaData.get("results")).get(0);

        // Create new Trivia object with correct question and answer data
        Trivia trivia = new Trivia();
        trivia.setQuestion((String) results.get("question"));
        trivia.setCorrectAnswer((String) results.get("correct_answer"));
        trivia.setAnswerAttempts(0);

        // Save the trivia question to the database
        triviaRepository.save(trivia);
        
        return trivia;
    }

    public Map<String, Object> fetchTriviaFromApi() {
        return restTemplate.getForObject(triviaApiUrl, Map.class);
    }

    public List<String> getPossibleAnswers(String correctAnswer, List<String> incorrectAnswers) {
        // Combine answers and shuffle them
        List<String> possibleAnswers = new ArrayList<>(incorrectAnswers);
        possibleAnswers.add(correctAnswer);
        Collections.shuffle(possibleAnswers);
        return possibleAnswers;
    }

    public String replyToTrivia(Long triviaId, String answer) {
        // Retrieve the trivia question by ID
        Trivia trivia = triviaRepository.findById(triviaId)
                .orElseThrow(() -> new RuntimeException("No such question!"));
    
        // Check if maximum attempts have been reached
        if (trivia.getAnswerAttempts() >= 3) {
            return "Max attempts reached!";
        }
    
        // Check if the provided answer is correct
        if (trivia.getCorrectAnswer().equalsIgnoreCase(answer)) {
            // Correct answer, remove trivia from the database
            triviaRepository.delete(trivia);
            return "right!";
        } else {
            // Incorrect answer, increment attempt count
            trivia.setAnswerAttempts(trivia.getAnswerAttempts() + 1);
            triviaRepository.save(trivia);
            return "wrong!";
        }
    }
    
}
