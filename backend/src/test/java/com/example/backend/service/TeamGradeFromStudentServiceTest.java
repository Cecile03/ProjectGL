package com.example.backend.service;

import com.example.backend.dao.TeamGradeFromStudentDao;
import com.example.backend.model.TeamGradeFromStudent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TeamGradeFromStudentServiceTest {

    @InjectMocks
    private TeamGradeFromStudentService teamGradeFromStudentService;

    @Mock
    private TeamGradeFromStudentDao teamGradeFromStudentDao;

    @Mock
    private TeamService teamService;

    @Mock
    private SprintService sprintService;


    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testGetTeamGradeFromStudent() {
        when(teamGradeFromStudentDao.findByTeamNotingIdAndTeamToNoteIdAndSprintId(anyInt(), anyInt(), anyInt())).thenReturn(Optional.of(new TeamGradeFromStudent()));
        teamGradeFromStudentService.getTeamGradeFromStudent(1, 1, 1);
        verify(teamGradeFromStudentDao, times(1)).findByTeamNotingIdAndTeamToNoteIdAndSprintId(anyInt(), anyInt(), anyInt());
    }

    @Test
    void testGetTeamGradeFromStudentException() {
        when(teamGradeFromStudentDao.findByTeamNotingIdAndTeamToNoteIdAndSprintId(anyInt(), anyInt(), anyInt())).thenThrow(new RuntimeException());
        assertThrows(RuntimeException.class, () -> teamGradeFromStudentService.getTeamGradeFromStudent(1, 1, 1));
    }

    @Test
    void testSaveTeamGradeFromStudent() {
        int teamNotingId = 1;
        int teamToNoteId = 2;
        int sprintId = 3;
        int grade = 85;

        teamGradeFromStudentService.saveTeamGradeFromStudent(teamNotingId, teamToNoteId, sprintId, grade);

        verify(teamGradeFromStudentDao, times(1)).save(any(TeamGradeFromStudent.class));
    }

    @Test
    void testUpdateTeamGradeFromStudent() {
        int teamNotingId = 1;
        int teamToNoteId = 2;
        int sprintId = 3;
        int grade = 85;

        when(teamGradeFromStudentDao.findByTeamNotingIdAndTeamToNoteIdAndSprintId(anyInt(), anyInt(), anyInt()))
                .thenReturn(Optional.of(new TeamGradeFromStudent()));

        teamGradeFromStudentService.updateTeamGradeFromStudent(teamNotingId, teamToNoteId, sprintId, grade);

        verify(teamGradeFromStudentDao, times(1)).save(any(TeamGradeFromStudent.class));
    }

    @Test
    void testUpdateTeamGradeFromStudentException() {
        int teamNotingId = 1;
        int teamToNoteId = 2;
        int sprintId = 3;
        int grade = 85;

        when(teamGradeFromStudentDao.findByTeamNotingIdAndTeamToNoteIdAndSprintId(anyInt(), anyInt(), anyInt()))
                .thenThrow(new RuntimeException());

        teamGradeFromStudentService.updateTeamGradeFromStudent(teamNotingId, teamToNoteId, sprintId, grade);

        verify(teamGradeFromStudentDao, times(1)).save(any(TeamGradeFromStudent.class));
    }

    @Test
    void getOTPR_shouldCallDaoFindByTeamToNoteIdAndSprintId_andReturnSumOfGrades() {
        int teamToNoteId = 1;
        int sprintId = 1;

        TeamGradeFromStudent teamGrade1 = new TeamGradeFromStudent();
        teamGrade1.setGrade(85);
        TeamGradeFromStudent teamGrade2 = new TeamGradeFromStudent();
        teamGrade2.setGrade(90);

        List<TeamGradeFromStudent> teamGrades = Arrays.asList(teamGrade1, teamGrade2);

        when(teamGradeFromStudentDao.findByTeamToNoteIdAndSprintId(teamToNoteId, sprintId)).thenReturn(Optional.of(teamGrades));

        double result = teamGradeFromStudentService.getOTPR(teamToNoteId, sprintId);

        verify(teamGradeFromStudentDao, times(1)).findByTeamToNoteIdAndSprintId(teamToNoteId, sprintId);
        assertEquals((double) 175 /2, result);
    }

    @Test
    void getOTPR_whenTeamGradesIsEmpty() {
        int teamToNoteId = 1;
        int sprintId = 1;

        when(teamGradeFromStudentDao.findByTeamToNoteIdAndSprintId(teamToNoteId, sprintId)).thenReturn(Optional.of(Collections.emptyList()));

        double result = teamGradeFromStudentService.getOTPR(teamToNoteId, sprintId);

        verify(teamGradeFromStudentDao, times(1)).findByTeamToNoteIdAndSprintId(teamToNoteId, sprintId);
        assertEquals(0, result);
    }

    @Test
    void getOTPR_whenExceptionIsThrown() {
        int teamToNoteId = 1;
        int sprintId = 1;

        when(teamGradeFromStudentDao.findByTeamToNoteIdAndSprintId(teamToNoteId, sprintId)).thenThrow(new RuntimeException());

        double result = teamGradeFromStudentService.getOTPR(teamToNoteId, sprintId);

        verify(teamGradeFromStudentDao, times(1)).findByTeamToNoteIdAndSprintId(teamToNoteId, sprintId);
        assertEquals(0, result);
    }
}