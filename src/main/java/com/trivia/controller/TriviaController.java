package com.trivia.controller;

import com.trivia.entity.Trivia;
import com.trivia.service.TriviaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/trivia")
public class TriviaController {

    @Autowired
    private TriviaService triviaService;

    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startTrivia() {
        Trivia trivia = triviaService.startTrivia();
        Map<String, Object> response = new HashMap<>();
        response.put("triviaId", trivia.getTriviaId());
        response.put("question", trivia.getQuestion());
        response.put("possibleAnswers", new String[]{trivia.getCorrectAnswer(), "Dummy Answer 1", "Dummy Answer 2"});
        return ResponseEntity.ok(response);
    }

    @PutMapping("/reply/{triviaId}")
    public ResponseEntity<Map<String, Object>> replyToTrivia(@PathVariable Long triviaId, @RequestBody Map<String, String> answerMap) {
        String answer = answerMap.get("answer");
        String result = triviaService.replyToTrivia(triviaId, answer);

        Map<String, Object> response = new HashMap<>();
        response.put("result", result);

        HttpStatus status = "right!".equals(result) ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        if ("Max attempts reached!".equals(result)) {
            status = HttpStatus.FORBIDDEN;
        }

        return new ResponseEntity<>(response, status);
    }
}
