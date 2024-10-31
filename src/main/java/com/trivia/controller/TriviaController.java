package com.trivia.controller;

import com.trivia.entity.Trivia;
import com.trivia.service.TriviaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/trivia")
public class TriviaController {

    @Autowired
    private TriviaService triviaService;

    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startTrivia() {
        Trivia trivia = triviaService.startTrivia();

        // Fetch information from 3rd party API
        Map<String, Object> triviaData = triviaService.fetchTriviaFromApiWithRetry();
        @SuppressWarnings("unchecked")
        Map<String, Object> results = (Map<String, Object>) ((List<?>) triviaData.get("results")).get(0);
        @SuppressWarnings("unchecked")
        List<String> incorrectAnswers = (List<String>) results.get("incorrect_answers");

        List<String> possibleAnswers = triviaService.getPossibleAnswers(trivia.getCorrectAnswer(), incorrectAnswers);

        // Create response
        Map<String, Object> response = new HashMap<>();
        response.put("triviaId", trivia.getTriviaId());
        response.put("question", trivia.getQuestion());
        response.put("possibleAnswers", possibleAnswers);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/reply/{triviaId}")
    public ResponseEntity<Map<String, Object>> replyToTrivia(@PathVariable Long triviaId,
            @RequestBody Map<String, String> answerMap) {
        String answer = answerMap.get("answer");
        String result = triviaService.replyToTrivia(triviaId, answer);

        Map<String, Object> response = new HashMap<>();
        response.put("result", result);

        HttpStatus status;
        switch (result) {
            case "right!":
                status = HttpStatus.OK;
                break;
            case "wrong!":
                status = HttpStatus.BAD_REQUEST;
                break;
            case "Max attempts reached!":
                status = HttpStatus.FORBIDDEN;
                break;
            default:
                status = HttpStatus.NOT_FOUND;
                break;
        }

        return new ResponseEntity<>(response, status);
    }
}
