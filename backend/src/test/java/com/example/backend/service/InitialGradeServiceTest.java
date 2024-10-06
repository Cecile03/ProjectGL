package com.example.backend.service;

import com.example.backend.dao.GradeTypesDao;
import com.example.backend.dao.InitialGradeDao;
import com.example.backend.model.GradeTypes;
import com.example.backend.model.InitialGrade;
import com.example.backend.model.SubGrade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class InitialGradeServiceTest {

    @InjectMocks
    private InitialGradeService initialGradeService;

    @Mock
    private UserService userService;

    @Mock
    private SprintService sprintService;

    @Mock
    private SubGradeService subGradeService;

    @Mock
    private InitialGradeDao initialGradeDao;
    @Mock
    private GradeTypesDao gradeTypesDao;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updateInitialGradeShouldUpdateAndSaveInitialGrade() {
        SubGrade subGrade = mock(SubGrade.class);
        when(subGrade.getValue()).thenReturn(10.0);
        when(subGradeService.updateSubGrade(anyInt(), anyInt(), any(GradeTypes.class))).thenReturn(subGrade);

        InitialGrade initialGrade = new InitialGrade();
        when(initialGradeDao.save(any(InitialGrade.class))).thenReturn(initialGrade);
        when(gradeTypesDao.findById(anyInt())).thenReturn(Optional.of(new GradeTypes()));

        InitialGrade result = initialGradeService.updateInitialGrade(1, 1);

        assertEquals(initialGrade, result);
        verify(initialGradeDao, times(2)).save(any(InitialGrade.class));
    }

    @Test
    void createInitialGradeShouldCreateAndSaveInitialGrade() {
        InitialGrade initialGrade = new InitialGrade();
        when(initialGradeDao.save(any(InitialGrade.class))).thenReturn(initialGrade);

        InitialGrade result = initialGradeService.createInitialGrade(1, 1);

        assertEquals(initialGrade, result);
        verify(initialGradeDao, times(1)).save(any(InitialGrade.class));
    }

    @Test
    void getInitialGradeByUserIdAndProjectIdShouldReturnExistingInitialGrade() {
        InitialGrade initialGrade = new InitialGrade();
        when(initialGradeDao.findByUserIdAndSprintId(anyInt(), anyInt())).thenReturn(Optional.of(initialGrade));

        InitialGrade result = initialGradeService.getInitialGradeByUserIdAndProjectId(1, 1);

        assertEquals(initialGrade, result);
    }

    @Test
    void getInitialGradeByUserIdAndProjectIdShouldCreateNewInitialGradeWhenNoneExists() {
        when(initialGradeDao.findByUserIdAndSprintId(anyInt(), anyInt())).thenReturn(Optional.empty());

        initialGradeService.getInitialGradeByUserIdAndProjectId(1, 1);

        verify(initialGradeDao, times(1)).save(any(InitialGrade.class));
    }
}
