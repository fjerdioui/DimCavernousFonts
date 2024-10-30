package com.trivia.service;

import com.trivia.entity.Trivia;
import com.trivia.repository.TriviaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Service
public class TriviaService {

    @Autowired
    private TriviaRepository triviaRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String triviaApiUrl = "https://opentdb.com/api.php?amount=1";

    public Trivia startTrivia() {
        Map<String, Object> triviaData = fetchTriviaFromApi();
        Trivia trivia = new Trivia();
        trivia.setQuestion((String) triviaData.get("question"));
        trivia.setCorrectAnswer((String) triviaData.get("correct_answer"));
        return triviaRepository.save(trivia);
    }

    public String replyToTrivia(Long triviaId, String answer) {
        Trivia trivia = triviaRepository.findById(triviaId)
                .orElseThrow(() -> new RuntimeException("No such question!"));

        if (trivia.getAnswerAttempts() >= 3) {
            return "Max attempts reached!";
        }

        if (trivia.getCorrectAnswer().equalsIgnoreCase(answer)) {
            triviaRepository.delete(trivia);
            return "right!";
        } else {
            trivia.setAnswerAttempts(trivia.getAnswerAttempts() + 1);
            triviaRepository.save(trivia);
            return "wrong!";
        }
    }

    private Map<String, Object> fetchTriviaFromApi() {
        return restTemplate.getForObject(triviaApiUrl, Map.class);
    }
}
