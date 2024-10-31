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
    }

    @Test
    void testStartTrivia_successful() {
        // // Mock API response with the expected trivia question and answer
        // Map<String, Object> mockApiResponse = Map.of("results", List.of(Map.of(
        // "question", "Sample question?",
        // "correct_answer", "Answer")));

        // // Set up the mock behavior for RestTemplate to return our mock response
        // when(restTemplate.getForObject(anyString(),
        // eq(Map.class))).thenReturn(mockApiResponse);

        // // Mock repository save to return a trivia instance with our sample question
        // and
        // // answer
        // Trivia trivia = new Trivia();
        // trivia.setQuestion("Sample question?");
        // trivia.setCorrectAnswer("Answer");
        // when(triviaRepository.save(any(Trivia.class))).thenReturn(trivia);

        // Act: Call the startTrivia method
        Trivia result = triviaService.startTrivia();

        // Assert: Verify the response matches our mock expectations
        assertNotNull(result);
        assertNotNull(result.getQuestion());
        assertNotNull(result.getCorrectAnswer());

        // Verify that the RestTemplate call was indeed made once
        // verify(restTemplate, times(1)).getForObject(anyString(), eq(Map.class));
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
        assertEquals("No such question!", exception.getMessage());
    }
}
