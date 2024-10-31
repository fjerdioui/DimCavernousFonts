package com.trivia.service;

import com.trivia.entity.Trivia;
import com.trivia.repository.TriviaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class TriviaServiceTest {

    @Mock
    private TriviaRepository triviaRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private TriviaService triviaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        triviaService = new TriviaService();
        triviaService.setTriviaRepository(triviaRepository);
    }

    @Test
    void testStartTrivia_successful() {
        Trivia result = triviaService.startTrivia();

        assertNotNull(result);
        assertNotNull(result.getQuestion());
        assertFalse(result.getQuestion().isEmpty(), "Question should not be empty");
        assertNotNull(result.getCorrectAnswer());
        assertFalse(result.getCorrectAnswer().isEmpty(), "Correct answer should not be empty");
    }

    @Test
    void testReplyToTrivia_correctAnswer() {
        Trivia trivia = new Trivia();
        trivia.setCorrectAnswer("Answer");
        trivia.setAnswerAttempts(0);

        when(triviaRepository.findById(anyLong())).thenReturn(Optional.of(trivia));

        String response = triviaService.replyToTrivia(1L, "Answer");
        assertEquals("right!", response);
    }

    @Test
    void testReplyToTrivia_incorrectAnswer() {
        Trivia trivia = new Trivia();
        trivia.setCorrectAnswer("Answer");
        trivia.setAnswerAttempts(0);

        when(triviaRepository.findById(anyLong())).thenReturn(Optional.of(trivia));

        String response = triviaService.replyToTrivia(1L, "Wrong Answer");
        assertEquals("wrong!", response);
        assertEquals(1, trivia.getAnswerAttempts());
    }

    @Test
    void testReplyToTrivia_maxAttemptsReached() {
        Trivia trivia = new Trivia();
        trivia.setCorrectAnswer("Answer");
        trivia.setAnswerAttempts(3);

        when(triviaRepository.findById(anyLong())).thenReturn(Optional.of(trivia));

        String response = triviaService.replyToTrivia(1L, "Wrong Answer");
        assertEquals("Max attempts reached!", response);
    }

    @Test
    void testReplyToTrivia_noTriviaFound() {
        when(triviaRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            triviaService.replyToTrivia(1L, "Answer");
        });
        assertEquals("Question don't exist!", exception.getMessage());
    }
}
