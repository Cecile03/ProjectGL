package com.example.backend.service;

import com.example.backend.dao.EvaluationDao;
import com.example.backend.model.Evaluation;
import com.example.backend.model.EvaluationStatus;
import com.example.backend.model.SubGrade;
import com.example.backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EvaluationServiceTest {

    @InjectMocks
    private EvaluationService evaluationService;

    @Mock
    private EvaluationDao evaluationDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addEvaluatorShouldSaveEvaluationAndUpdateStatus() {
        SubGrade subGrade = new SubGrade();
        User evaluator = new User();

        evaluationService.addEvaluator(subGrade, evaluator);

        verify(evaluationDao, times(1)).save(any(Evaluation.class));
        assert(subGrade.getStatus() == EvaluationStatus.IN_PROGRESS);
    }

    @Test
    void addEvaluatorShouldReturnExistingEvaluationWhenPresent() {
        SubGrade subGrade = new SubGrade();
        User evaluator = new User();
        Evaluation existingEvaluation = new Evaluation();

        when(evaluationDao.findByEvaluatorIdAndSubGradeId(evaluator.getId(), subGrade.getId())).thenReturn(Optional.of(existingEvaluation));

        Evaluation result = evaluationService.addEvaluator(subGrade, evaluator);

        assertEquals(existingEvaluation, result);
        verify(evaluationDao, times(2)).findByEvaluatorIdAndSubGradeId(evaluator.getId(), subGrade.getId());
        verify(evaluationDao, never()).save(any(Evaluation.class));
    }

    @Test
    void updateEvaluationShouldReturnUpdatedEvaluationWhenSuccessful() {
        Double value = 85.5;

        when(evaluationDao.save(any(Evaluation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Evaluation savedEvaluation = evaluationService.addEvaluator(new SubGrade(), new User());
        Evaluation result = evaluationService.updateEvaluation(savedEvaluation, value);

        assertEquals(savedEvaluation, result);
        verify(evaluationDao, times(2)).save(any(Evaluation.class));
    }

    @Test
    void updateEvaluationShouldThrowIllegalArgumentExceptionWhenExceptionThrown() {
        Evaluation evaluation = new Evaluation();
        Double value = 85.5;

        when(evaluationDao.save(any(Evaluation.class))).thenThrow(new RuntimeException());

        assertThrows(IllegalArgumentException.class, () -> evaluationService.updateEvaluation(evaluation, value));
        verify(evaluationDao, times(1)).save(any(Evaluation.class));
    }

    @Test
    void getEvaluationBySubGradeIdAndEvaluatorIdShouldReturnEvaluationWhenSuccessful() {
        Evaluation evaluation = new Evaluation();
        int subGradeId = 1;
        int evaluatorId = 1;

        when(evaluationDao.findByEvaluatorIdAndSubGradeId(evaluatorId, subGradeId)).thenReturn(Optional.of(evaluation));

        Evaluation result = evaluationService.getEvaluationBySubGradeIdAndEvaluatorId(subGradeId, evaluatorId);

        assertEquals(evaluation, result);
        verify(evaluationDao, times(1)).findByEvaluatorIdAndSubGradeId(evaluatorId, subGradeId);
    }

    @Test
    void getEvaluationByIdShouldReturnEvaluationWhenSuccessful() {
        Evaluation evaluation = new Evaluation();
        int evaluationId = 1;

        when(evaluationDao.findById(evaluationId)).thenReturn(Optional.of(evaluation));

        Evaluation result = evaluationService.getEvaluationById(evaluationId);

        assertEquals(evaluation, result);
        verify(evaluationDao, times(1)).findById(evaluationId);
    }
}