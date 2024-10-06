package com.example.backend.service;

import com.example.backend.dao.TeamGradeDao;
import com.example.backend.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TeamGradeServiceTest {

    @InjectMocks
    private TeamGradeService teamGradeService;

    @Mock
    private TeamGradeDao teamGradeDao;

    @Mock
    private UserService userService;

    @Mock
    private SprintService sprintService;


    @Mock
    private DetailService detailService;

    @Mock
    private TeamService teamService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testGetTeamGrade() {
        when(teamGradeDao.findByTeamIdAndSprintIdAndDetailIdAndEvaluatorId(anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(Optional.of(new TeamGrade()));
        teamGradeService.getTeamGrade(1, 1, 1,1);
        verify(teamGradeDao, times(1)).findByTeamIdAndSprintIdAndDetailIdAndEvaluatorId(anyInt(), anyInt(), anyInt(), anyInt());
    }

    @Test
    void testGetTeamGradeException() {
        when(teamGradeDao.findByTeamIdAndSprintIdAndDetailIdAndEvaluatorId(anyInt(), anyInt(), anyInt(), anyInt())).thenThrow(new RuntimeException());
        assertThrows(RuntimeException.class, () -> teamGradeService.getTeamGrade(1, 1, 1,1));
    }

    @Test
    void testSaveTeamGrade() {
        int teamId = 1;
        int sprintId = 1;
        int detailId = 2;
        int evaluatorId = 1;
        Double grade = 85.0;

        when(userService.getUserById(anyInt())).thenReturn(mock(User.class));
        when(sprintService.getSprintById(anyInt())).thenReturn(mock(Sprint.class));
        when(detailService.getDetailById(anyInt())).thenReturn(mock(Detail.class));
        when(teamService.getTeamById(anyInt())).thenReturn(mock(Team.class));

        teamGradeService.saveTeamGrade(teamId,sprintId, detailId, evaluatorId, grade);

        verify(teamGradeDao, times(1)).save(any(TeamGrade.class));
    }

    @Test
    void testUpdateTeamGrade() {
        int teamId = 1;
        int sprintId = 1;
        int detailId = 2;
        int evaluatorId = 1;
        Double grade = 85.0;

        when(teamGradeDao.findByTeamIdAndSprintIdAndDetailIdAndEvaluatorId(anyInt(), anyInt(), anyInt(), anyInt()))
                .thenReturn(Optional.of(new TeamGrade()));

        teamGradeService.updateTeamGrade(teamId,sprintId, detailId, evaluatorId, grade);

        verify(teamGradeDao, times(1)).save(any(TeamGrade.class));
    }

    @Test
    void updateTeamGradeShouldCallSaveTeamGradeWhenNoTeamGradeFound() {
        int teamId = 1;
        int sprintId = 1;
        int detailId = 2;
        int evaluatorId = 1;
        Double grade = 85.0;

        when(teamGradeDao.findByTeamIdAndSprintIdAndDetailIdAndEvaluatorId(anyInt(), anyInt(), anyInt(), anyInt()))
                .thenThrow(new RuntimeException());

        teamGradeService.updateTeamGrade(teamId, sprintId, detailId, evaluatorId, grade);

        verify(teamGradeDao, times(1)).findByTeamIdAndSprintIdAndDetailIdAndEvaluatorId(anyInt(), anyInt(), anyInt(), anyInt());
    }

    @Test
    void getTeamGradeByCategoryId_shouldCallDetailServiceAndTeamGradeDao_andReturnListOfTeamGrades() {
        int categoryId = 1;
        int sprintId = 1;
        int teamId = 1;
        int evaluatorId = 1;

        Detail detail1 = new Detail();
        detail1.setId(1);
        Detail detail2 = new Detail();
        detail2.setId(2);

        List<Detail> details = Arrays.asList(detail1, detail2);

        TeamGrade teamGrade1 = new TeamGrade();
        teamGrade1.setDetail(detail1);
        teamGrade1.setGrade(10.0);
        TeamGrade teamGrade2 = new TeamGrade();
        teamGrade2.setDetail(detail2);
        teamGrade2.setGrade(10.0);

        when(detailService.getDetailsByCategoryId(categoryId)).thenReturn(details);
        when(teamGradeDao.findByTeamIdAndSprintIdAndDetailIdAndEvaluatorId(teamId, sprintId, detail1.getId(),evaluatorId)).thenReturn(Optional.of(teamGrade1));
        when(teamGradeDao.findByTeamIdAndSprintIdAndDetailIdAndEvaluatorId(teamId, sprintId, detail2.getId(),evaluatorId)).thenReturn(Optional.of(teamGrade2));

        teamGradeService.getAverageTeamGradeByCategoryId(categoryId, sprintId, teamId, evaluatorId);

        verify(detailService, times(1)).getDetailsByCategoryId(categoryId);
        verify(teamGradeDao, times(1)).findByTeamIdAndSprintIdAndDetailIdAndEvaluatorId(teamId, sprintId, detail1.getId(), evaluatorId);
        verify(teamGradeDao, times(1)).findByTeamIdAndSprintIdAndDetailIdAndEvaluatorId(teamId, sprintId, detail2.getId(), evaluatorId);
    }

    @Test
    void deleteAllShouldCallDeleteAllInTeamGradeDao() {
        teamGradeService.deleteAll();
        verify(teamGradeDao, times(1)).deleteAll();
    }

}