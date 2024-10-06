package com.example.backend.controller;

import com.example.backend.dao.GradeTypesDao;
import com.example.backend.model.Evaluation;
import com.example.backend.model.GradeTypes;
import com.example.backend.model.SubGrade;
import com.example.backend.service.SubGradeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SubGradeControllerTest {

    @InjectMocks
    private SubGradeController subGradeController;

    @Mock
    private SubGradeService subGradeService;

    @Mock
    private GradeTypesDao gradeTypesDao;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void updateSubGradeShouldReturnBadRequestWhenExceptionThrown() {
        doThrow(new RuntimeException()).when(subGradeService).updateSubGrade(anyInt(), anyInt(), any(GradeTypes.class));

        ResponseEntity<Void> result = subGradeController.updateSubGrade(1, 1, "TECHNICAL");

        assertEquals(ResponseEntity.badRequest().build(), result);
    }

    @Test
    void updateSubGrade_shouldReturnOkWhenNoExceptionThrown() {
        // Arrange
        int userId = 1;
        int sprintId = 1;
        String gradeTypeName = "PRMO";
        GradeTypes gradeType = new GradeTypes(GradeTypes.GradeTypesEnum.valueOf(gradeTypeName));
        SubGrade expectedSubGrade = new SubGrade();
        when(gradeTypesDao.findById(GradeTypes.GradeTypesEnum.valueOf(gradeTypeName).getId())).thenReturn(Optional.of(gradeType));
        when(subGradeService.updateSubGrade(userId, sprintId, gradeType)).thenReturn(expectedSubGrade);

        // Act
        ResponseEntity<Void> response = subGradeController.updateSubGrade(userId, sprintId, gradeTypeName);

        // Assert
        assertEquals(ResponseEntity.ok().build(), response);
    }


    @Test
    void getSubGradeByUserIdAndSprintIdAndGradeTypeShouldReturnBadRequestWhenExceptionThrown() {
        when(subGradeService.getSubGradeByUserIdAndSprintIdAndGradeType(anyInt(), anyInt(), any(GradeTypes.class))).thenThrow(new RuntimeException());

        ResponseEntity<SubGrade> result = subGradeController.getSubGradeByUserIdAndSprintIdAndGradeType(1, 1, "TECHNICAL");

        assertEquals(ResponseEntity.badRequest().build(), result);
    }

    @Test
    void getEvaluationBySubgradeShouldReturnOkWhenSuccessful() {
        // Define a valid enum constant string
        String validGradeTypeName = "OTPR";  // Ensure this matches an actual enum constant

        // Prepare mock objects
        Evaluation evaluation = new Evaluation();
        when(subGradeService.getEvaluationBySubgrade(anyInt(), anyInt())).thenReturn(evaluation);
        when(subGradeService.getSubGradeByUserIdAndSprintIdAndGradeType(anyInt(), anyInt(), any(GradeTypes.class)))
                .thenReturn(new SubGrade());
        when(gradeTypesDao.findById(anyInt())).thenReturn(Optional.of(new GradeTypes(GradeTypes.GradeTypesEnum.valueOf(validGradeTypeName))));

        // Call the method with a valid grade type name
        ResponseEntity<Evaluation> result = subGradeController.getEvaluationBySubgrade(1, 1, validGradeTypeName, 1);

        // Verify the results
        assertEquals(ResponseEntity.ok(evaluation), result);
        verify(subGradeService, times(1)).getEvaluationBySubgrade(anyInt(), anyInt());
    }


    @Test
    void getEvaluationBySubgradeShouldReturnBadRequestWhenExceptionThrown() {
        when(subGradeService.getEvaluationBySubgrade(anyInt(), anyInt())).thenThrow(new RuntimeException());

        ResponseEntity<Evaluation> result = subGradeController.getEvaluationBySubgrade(1, 1, "TECHNICAL", 1);

        assertEquals(ResponseEntity.badRequest().build(), result);
    }

}