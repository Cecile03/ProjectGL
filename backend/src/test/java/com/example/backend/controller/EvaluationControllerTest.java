package com.example.backend.controller;

import com.example.backend.model.Evaluation;
import com.example.backend.service.EvaluationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class EvaluationControllerTest {

    @InjectMocks
    private EvaluationController evaluationController;

    @Mock
    private EvaluationService evaluationService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updateSubGradeShouldReturnUpdatedEvaluation() {
        Evaluation evaluation = new Evaluation();
        when(evaluationService.getEvaluationById(anyInt())).thenReturn(evaluation);
        when(evaluationService.updateEvaluation(any(Evaluation.class), anyDouble())).thenReturn(evaluation);

        ResponseEntity<Evaluation> result = evaluationController.updateSubGrade(1, 90.0);

        assertEquals(ResponseEntity.ok(evaluation), result);
        verify(evaluationService, times(1)).getEvaluationById(anyInt());
        verify(evaluationService, times(1)).updateEvaluation(any(Evaluation.class), anyDouble());
    }

    @Test
    void updateSubGradeShouldReturnBadRequestWhenExceptionThrown() {
        when(evaluationService.getEvaluationById(anyInt())).thenThrow(new RuntimeException());

        ResponseEntity<Evaluation> result = evaluationController.updateSubGrade(1, 90.0);

        assertEquals(ResponseEntity.badRequest().build(), result);
        verify(evaluationService, times(1)).getEvaluationById(anyInt());
        verify(evaluationService, times(0)).updateEvaluation(any(Evaluation.class), anyDouble());
    }
}