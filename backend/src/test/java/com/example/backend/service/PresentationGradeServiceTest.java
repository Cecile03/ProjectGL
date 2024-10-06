package com.example.backend.service;

import com.example.backend.dao.GradeTypesDao;
import com.example.backend.dao.PresentationGradeDao;
import com.example.backend.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PresentationGradeServiceTest {

    @InjectMocks
    private PresentationGradeService presentationGradeService;

    @Mock
    private UserService userService;

    @Mock
    private SprintService sprintService;

    @Mock
    private SubGradeService subGradeService;

    @Mock
    private PresentationGradeDao presentationGradeDao;

    @Mock
    private GradeTypesDao gradeTypesDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createPresentationGradeShouldCreateAndSavePresentationGrade() {
        PresentationGrade presentationGrade = new PresentationGrade();
        when(presentationGradeDao.save(any(PresentationGrade.class))).thenReturn(presentationGrade);

        PresentationGrade result = presentationGradeService.createPresentationGrade(1, 1);

        assertEquals(presentationGrade, result);
        verify(presentationGradeDao, times(1)).save(any(PresentationGrade.class));
    }

    @Test
    void getPresentationGradeByUserIdAndSprintIdShouldReturnExistingPresentationGrade() {
        PresentationGrade presentationGrade = new PresentationGrade();
        when(presentationGradeDao.findByUserIdAndSprintId(anyInt(), anyInt())).thenReturn(Optional.of(presentationGrade));

        PresentationGrade result = presentationGradeService.getPresentationGradeByUserIdAndSprintId(1, 1);

        assertEquals(presentationGrade, result);
    }

    @Test
    void getPresentationGradeByUserIdAndSprintIdShouldCreateNewPresentationGradeWhenNoneExists() {
        when(presentationGradeDao.findByUserIdAndSprintId(anyInt(), anyInt())).thenReturn(Optional.empty());

        presentationGradeService.getPresentationGradeByUserIdAndSprintId(1, 1);

        verify(presentationGradeDao, times(1)).save(any(PresentationGrade.class));
    }

    @Test
    void updatePresentationGrade_shouldUpdateAndSavePresentationGrade() {
        // Given
        int userId = 1;
        int sprintId = 1;
        GradeTypes ssprType = new GradeTypes();
        GradeTypes otprType = new GradeTypes();
        GradeTypes tcprType = new GradeTypes();
        SubGrade sspr = new SubGrade();
        SubGrade otpr = new SubGrade();
        SubGrade tcpr = new SubGrade();
        PresentationGrade presentationGrade = new PresentationGrade();

        when(gradeTypesDao.findById(GradeTypes.GradeTypesEnum.SSPR.getId())).thenReturn(Optional.of(ssprType));
        when(gradeTypesDao.findById(GradeTypes.GradeTypesEnum.OTPR.getId())).thenReturn(Optional.of(otprType));
        when(gradeTypesDao.findById(GradeTypes.GradeTypesEnum.TCPR.getId())).thenReturn(Optional.of(tcprType));
        when(subGradeService.updateSubGrade(userId, sprintId, ssprType)).thenReturn(sspr);
        when(subGradeService.updateSubGrade(userId, sprintId, otprType)).thenReturn(otpr);
        when(subGradeService.updateSubGrade(userId, sprintId, tcprType)).thenReturn(tcpr);
        when(presentationGradeDao.findByUserIdAndSprintId(userId, sprintId)).thenReturn(Optional.of(presentationGrade));
        when(presentationGradeDao.save(any(PresentationGrade.class))).thenReturn(presentationGrade);

        // When
        PresentationGrade result = presentationGradeService.updatePresentationGrade(userId, sprintId);

        // Then
        assertEquals(presentationGrade, result);
        verify(presentationGradeDao, times(1)).save(any(PresentationGrade.class));
    }
}
