package com.example.backend.service;

import com.example.backend.dao.GradeTypesDao;
import com.example.backend.dao.WorkGradeDao;
import com.example.backend.model.GradeTypes;
import com.example.backend.model.SubGrade;
import com.example.backend.model.WorkGrade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class WorkGradeServiceTest {

    @InjectMocks
    private WorkGradeService workGradeService;

    @Mock
    private UserService userService;

    @Mock
    private SprintService sprintService;

    @Mock
    private SubGradeService subGradeService;

    @Mock
    private WorkGradeDao workGradeDao;

    @Mock
    private GradeTypesDao gradeTypesDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updateWorkGradeShouldUpdateAndSaveWorkGrade() {
        int userId = 1;
        int sprintId = 1;
        GradeTypes ssbmType = new GradeTypes();
        ssbmType.setId(GradeTypes.GradeTypesEnum.SSBM.getId());
        GradeTypes tebmType = new GradeTypes();
        tebmType.setId(GradeTypes.GradeTypesEnum.TEBM.getId());
        SubGrade ssbm = new SubGrade();
        ssbm.setValue(5);
        SubGrade tebm = new SubGrade();
        tebm.setValue(7);
        WorkGrade expectedWorkGrade = new WorkGrade();
        expectedWorkGrade.setSsbm(ssbm);
        expectedWorkGrade.setTebm(tebm);
        expectedWorkGrade.setValue(ssbm.getValue() + tebm.getValue());

        when(gradeTypesDao.findById(GradeTypes.GradeTypesEnum.SSBM.getId())).thenReturn(Optional.of(ssbmType));
        when(gradeTypesDao.findById(GradeTypes.GradeTypesEnum.TEBM.getId())).thenReturn(Optional.of(tebmType));
        when(subGradeService.updateSubGrade(userId, sprintId, ssbmType)).thenReturn(ssbm);
        when(subGradeService.updateSubGrade(userId, sprintId, tebmType)).thenReturn(tebm);
        when(workGradeDao.findByUserIdAndSprintId(userId, sprintId)).thenReturn(Optional.of(expectedWorkGrade));
        when(workGradeDao.save(any(WorkGrade.class))).thenReturn(expectedWorkGrade);

        WorkGrade result = workGradeService.updateWorkGrade(userId, sprintId);

        assertEquals(expectedWorkGrade, result);
        verify(subGradeService, times(1)).updateSubGrade(userId, sprintId, ssbmType);
        verify(subGradeService, times(1)).updateSubGrade(userId, sprintId, tebmType);
        verify(workGradeDao, times(1)).findByUserIdAndSprintId(userId, sprintId);
        verify(workGradeDao, times(1)).save(any(WorkGrade.class));
    }

    @Test
    void createWorkGradeShouldCreateAndSaveWorkGrade() {
        WorkGrade workGrade = new WorkGrade();
        when(workGradeDao.save(any(WorkGrade.class))).thenReturn(workGrade);

        WorkGrade result = workGradeService.createWorkGrade(1, 1);

        assertEquals(workGrade, result);
        verify(workGradeDao, times(1)).save(any(WorkGrade.class));
    }

    @Test
    void getWorkGradeByUserIdAndSprintIdShouldReturnExistingWorkGrade() {
        WorkGrade workGrade = new WorkGrade();
        when(workGradeDao.findByUserIdAndSprintId(anyInt(), anyInt())).thenReturn(Optional.of(workGrade));

        WorkGrade result = workGradeService.getWorkGradeByUserIdAndSprintId(1, 1);

        assertEquals(workGrade, result);
    }

    @Test
    void getWorkGradeByUserIdAndSprintIdShouldCreateNewWorkGradeWhenNoneExists() {
        when(workGradeDao.findByUserIdAndSprintId(anyInt(), anyInt())).thenReturn(Optional.empty());

        workGradeService.getWorkGradeByUserIdAndSprintId(1, 1);

        verify(workGradeDao, times(1)).save(any(WorkGrade.class));
    }

    @Test
    void saveWorkGradeShouldSaveWorkGrade() {
        WorkGrade workGrade = new WorkGrade();
        when(workGradeDao.save(any(WorkGrade.class))).thenReturn(workGrade);

        workGradeService.saveWorkGrade(workGrade);

        verify(workGradeDao, times(1)).save(any(WorkGrade.class));
    }
}
