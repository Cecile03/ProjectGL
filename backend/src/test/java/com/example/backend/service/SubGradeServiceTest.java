package com.example.backend.service;

import com.example.backend.dao.EvaluationDao;
import com.example.backend.dao.SubGradeDao;
import com.example.backend.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class SubGradeServiceTest {

    @InjectMocks
    private SubGradeService subGradeService;

    @Mock
    private TeamGradeService teamGradeService;

    @Mock
    private SubGradeDao subGradeDao;

    @Mock
    private TeamGradeFromStudentService teamGradeFromStudentService;

    @Mock
    private BonusMalusService bonusMalusService;

    @Mock
    private UserService userService;

    @Mock
    private SprintService sprintService;

    @Mock
    private TeamService teamService;

    @Mock
    private EvaluationService evaluationService;

    @Mock
    private EvaluationDao evaluationDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updateSubGradeShouldUpdateAndSaveSubGradeWhenGradeTypeIsOne() {
        User user = mock(User.class);
        Team team = mock(Team.class);
        Sprint sprint = mock(Sprint.class);
        when(userService.getUserById(anyInt())).thenReturn(user);
        when(teamService.getTeamByUserId(anyInt())).thenReturn(team);
        when(sprintService.getSprintById(anyInt())).thenReturn(sprint);
        when(teamGradeService.getAverageTeamGradeByCategoryId(anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(10.0);
        when(bonusMalusService.getBonusMalusValue(anyInt(), anyInt(), anyInt())).thenReturn(10.0);
        when(teamGradeFromStudentService.getOTPR(anyInt(), anyInt())).thenReturn(10.0);
        when(teamGradeFromStudentService.getTeamGradesFromStudentForTeam(anyInt(), anyInt())).thenReturn(List.of(new TeamGradeFromStudent()));
        when(teamService.getAllTeams()).thenReturn(List.of(new Team()));

        SubGrade subGrade = new SubGrade();
        subGrade.setSprint(sprint);
        when(subGradeDao.save(any(SubGrade.class))).thenReturn(subGrade);

        GradeTypes.GradeTypesEnum gradeTypeEnum = GradeTypes.GradeTypesEnum.OTPR;
        GradeTypes gradeType = new GradeTypes(gradeTypeEnum);
        gradeType.setId(1);

        SubGrade result = subGradeService.updateSubGrade(1, 1, gradeType);

        assertEquals(subGrade, result);
        verify(subGradeDao, times(2)).save(any(SubGrade.class));
    }

    @Test
    void updateSubGradeShouldUpdateAndSaveSubGradeWhenGradeTypeIdIsTwo() {
        User user = mock(User.class);
        Team team = mock(Team.class);
        User supervisor = mock(User.class);
        User technicalCoach = mock(User.class);
        Sprint sprint = mock(Sprint.class);

        GradeTypes.GradeTypesEnum gradeTypeEnum = GradeTypes.GradeTypesEnum.SUPR;
        GradeTypes gradeType = new GradeTypes(gradeTypeEnum);
        gradeType.setId(2);

        SubGrade subGrade = new SubGrade();
        when(subGradeDao.save(any(SubGrade.class))).thenReturn(subGrade);
        subGrade.setUser(user);
        subGrade.setGradeType(gradeType);
        subGrade.setSprint(sprint);

        when(userService.getUserById(anyInt())).thenReturn(user);
        when(teamService.getTeamByUserId(anyInt())).thenReturn(team);
        when(sprintService.getSprintById(anyInt())).thenReturn(sprint);
        when(team.getSupervisor()).thenReturn(supervisor);
        when(userService.getTechnicalCoaches()).thenReturn(List.of(technicalCoach));
        when(evaluationService.addEvaluator(any(SubGrade.class), any(User.class))).thenReturn(new Evaluation());
        when(teamGradeService.getAverageTeamGradeByCategoryId(anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(10.0);

        SubGrade result = subGradeService.updateSubGrade(1, 1, gradeType);

        assertEquals(subGrade, result);
        verify(subGradeDao, times(2)).save(any(SubGrade.class));
    }

    @Test
    void updateSubGradeShouldUpdateAndSaveSubGradeWhenGradeTypeIdIsFive() {
        User user = mock(User.class);
        Team team = mock(Team.class);
        Sprint sprint = mock(Sprint.class);
        when(userService.getUserById(anyInt())).thenReturn(user);
        when(teamService.getTeamByUserId(anyInt())).thenReturn(team);
        when(sprintService.getSprintById(anyInt())).thenReturn(sprint);
        when(userService.getAllTeachers()).thenReturn(List.of(new User()));

        GradeTypes.GradeTypesEnum gradeTypeEnum = GradeTypes.GradeTypesEnum.SUPR;
        GradeTypes gradeType = new GradeTypes(gradeTypeEnum);
        gradeType.setId(5);

        SubGrade subGrade = new SubGrade();
        when(subGradeDao.save(any(SubGrade.class))).thenReturn(subGrade);
        subGrade.setUser(user);
        subGrade.setGradeType(gradeType);
        subGrade.setSprint(sprint);

        SubGrade result = subGradeService.updateSubGrade(1, 1, gradeType);

        assertEquals(subGrade, result);
        verify(subGradeDao, times(2)).save(any(SubGrade.class));
    }

    @Test
    void updateSubGradeShouldUpdateAndSaveSubGradeWhenGradeTypeIdIsSix() {
        User user = mock(User.class);
        Team team = mock(Team.class);
        Sprint sprint = mock(Sprint.class);
        when(userService.getUserById(anyInt())).thenReturn(user);
        when(teamService.getTeamByUserId(anyInt())).thenReturn(team);
        when(sprintService.getSprintById(anyInt())).thenReturn(sprint);
        when(teamGradeService.getAverageTeamGradeByCategoryId(anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(10.0);
        when(bonusMalusService.getBonusMalusValue(anyInt(), anyInt(), anyInt())).thenReturn(10.0);
        when(teamGradeFromStudentService.getOTPR(anyInt(), anyInt())).thenReturn(10.0);
        when(teamGradeFromStudentService.getTeamGradesFromStudentForTeam(anyInt(), anyInt())).thenReturn(List.of(new TeamGradeFromStudent()));
        when(teamService.getAllTeams()).thenReturn(List.of(new Team()));

        SubGrade subGrade = new SubGrade();
        when(subGradeDao.save(any(SubGrade.class))).thenReturn(subGrade);

        GradeTypes.GradeTypesEnum gradeTypeEnum = GradeTypes.GradeTypesEnum.TEBM;
        GradeTypes gradeType = new GradeTypes(gradeTypeEnum);
        gradeType.setId(6);

        SubGrade result = subGradeService.updateSubGrade(1, 1, gradeType);

        assertEquals(subGrade, result);
        verify(subGradeDao, times(2)).save(any(SubGrade.class));
    }

    @Test
    void updateSubGradeShouldUpdateAndSaveSubGradeWhenGradeTypeIdIsSeven() {
        User user = mock(User.class);
        Team team = mock(Team.class);
        Sprint sprint = mock(Sprint.class);
        when(userService.getUserById(anyInt())).thenReturn(user);
        when(teamService.getTeamByUserId(anyInt())).thenReturn(team);
        when(sprintService.getSprintById(anyInt())).thenReturn(sprint);
        when(teamGradeService.getAverageTeamGradeByCategoryId(anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(10.0);
        when(bonusMalusService.getBonusMalusValue(anyInt(), anyInt(), anyInt())).thenReturn(10.0);
        when(teamGradeFromStudentService.getOTPR(anyInt(), anyInt())).thenReturn(10.0);
        when(teamGradeFromStudentService.getTeamGradesFromStudentForTeam(anyInt(), anyInt())).thenReturn(List.of(new TeamGradeFromStudent()));
        when(teamService.getAllTeams()).thenReturn(List.of(new Team()));

        SubGrade subGrade = new SubGrade();
        when(subGradeDao.save(any(SubGrade.class))).thenReturn(subGrade);

        GradeTypes.GradeTypesEnum gradeTypeEnum = GradeTypes.GradeTypesEnum.SSBM;
        GradeTypes gradeType = new GradeTypes(gradeTypeEnum);
        gradeType.setId(7);

        SubGrade result = subGradeService.updateSubGrade(1, 1, gradeType);

        assertEquals(subGrade, result);
        verify(subGradeDao, times(2)).save(any(SubGrade.class));
    }

    @Test
    void updateSubGradeShouldUpdateAndSaveSubGradeWhenGradeTypeIdIsEight() {
        User user = mock(User.class);
        Team team = mock(Team.class);
        Sprint sprint = mock(Sprint.class);
        when(userService.getUserById(anyInt())).thenReturn(user);
        when(teamService.getTeamByUserId(anyInt())).thenReturn(team);
        when(sprintService.getSprintById(anyInt())).thenReturn(sprint);
        when(teamGradeService.getAverageTeamGradeByCategoryId(anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(10.0);
        when(bonusMalusService.getBonusMalusValue(anyInt(), anyInt(), anyInt())).thenReturn(10.0);
        when(teamGradeFromStudentService.getOTPR(anyInt(), anyInt())).thenReturn(10.0);
        when(teamGradeFromStudentService.getTeamGradesFromStudentForTeam(anyInt(), anyInt())).thenReturn(List.of(new TeamGradeFromStudent()));
        when(teamService.getAllTeams()).thenReturn(List.of(new Team()));

        SubGrade subGrade = new SubGrade();
        when(subGradeDao.save(any(SubGrade.class))).thenReturn(subGrade);

        GradeTypes.GradeTypesEnum gradeTypeEnum = GradeTypes.GradeTypesEnum.SSPR;
        GradeTypes gradeType = new GradeTypes(gradeTypeEnum);
        gradeType.setId(8);

        SubGrade result = subGradeService.updateSubGrade(1, 1, gradeType);

        assertEquals(subGrade, result);
        verify(subGradeDao, times(2)).save(any(SubGrade.class));
    }

    @Test
    void updateSubGradeShouldUpdateAndSaveSubGradeWhenGradeTypeIdIsNine() {
        User user = mock(User.class);
        Team team = mock(Team.class);
        Sprint sprint = mock(Sprint.class);
        when(userService.getUserById(anyInt())).thenReturn(user);
        when(teamService.getTeamByUserId(anyInt())).thenReturn(team);
        when(sprintService.getSprintById(anyInt())).thenReturn(sprint);
        when(teamGradeService.getAverageTeamGradeByCategoryId(anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(10.0);
        when(bonusMalusService.getBonusMalusValue(anyInt(), anyInt(), anyInt())).thenReturn(10.0);
        when(teamGradeFromStudentService.getOTPR(anyInt(), anyInt())).thenReturn(10.0);
        when(teamGradeFromStudentService.getTeamGradesFromStudentForTeam(anyInt(), anyInt())).thenReturn(List.of(new TeamGradeFromStudent()));
        when(teamService.getAllTeams()).thenReturn(List.of(new Team()));

        SubGrade subGrade = new SubGrade();
        when(subGradeDao.save(any(SubGrade.class))).thenReturn(subGrade);

        GradeTypes.GradeTypesEnum gradeTypeEnum = GradeTypes.GradeTypesEnum.TCPR;
        GradeTypes gradeType = new GradeTypes(gradeTypeEnum);
        gradeType.setId(9);

        SubGrade result = subGradeService.updateSubGrade(1, 1, gradeType);

        assertEquals(subGrade, result);
        verify(subGradeDao, times(2)).save(any(SubGrade.class));
    }

    @Test
    void updateSubGradeShouldThrowIllegalArgumentExceptionWhenGradeTypeIsInvalid() {
        User user = mock(User.class);
        Team team = mock(Team.class);
        Sprint sprint = mock(Sprint.class);
        when(userService.getUserById(anyInt())).thenReturn(user);
        when(teamService.getTeamByUserId(anyInt())).thenReturn(team);
        when(sprintService.getSprintById(anyInt())).thenReturn(sprint);

        GradeTypes gradeType = new GradeTypes();

        assertThrows(IllegalArgumentException.class, () -> subGradeService.updateSubGrade(1, 1, gradeType));
    }

    @Test
    void createSubGradeShouldCreateAndSaveSubGrade() {
        User user = mock(User.class);
        when(userService.getUserById(anyInt())).thenReturn(user);

        SubGrade subGrade = new SubGrade();
        when(subGradeDao.save(any(SubGrade.class))).thenReturn(subGrade);

        GradeTypes.GradeTypesEnum gradeTypeEnum = GradeTypes.GradeTypesEnum.OTPR;
        GradeTypes gradeType = new GradeTypes(gradeTypeEnum);

        SubGrade result = subGradeService.createSubGrade(1, 1, gradeType);

        assertEquals(subGrade, result);
        verify(subGradeDao, times(1)).save(any(SubGrade.class));
    }

    @Test
    void getSubGradeByUserIdAndSprintIdAndGradeTypeShouldReturnExistingSubGrade() {
        SubGrade subGrade = new SubGrade();
        when(subGradeDao.findByUserIdAndSprintIdAndGradeType(anyInt(), anyInt(), any(GradeTypes.class))).thenReturn(Optional.of(subGrade));

        GradeTypes.GradeTypesEnum gradeTypeEnum = GradeTypes.GradeTypesEnum.OTPR;
        GradeTypes gradeType = new GradeTypes(gradeTypeEnum);

        SubGrade result = subGradeService.getSubGradeByUserIdAndSprintIdAndGradeType(1, 1, gradeType);

        assertEquals(subGrade, result);
    }

    @Test
    void getSubGradeByUserIdAndSprintIdAndGradeTypeShouldCreateNewSubGradeWhenNoneExists() {
        when(subGradeDao.findByUserIdAndSprintIdAndGradeType(anyInt(), anyInt(), any(GradeTypes.class))).thenReturn(Optional.empty());

        GradeTypes.GradeTypesEnum gradeTypeEnum = GradeTypes.GradeTypesEnum.OTPR;
        GradeTypes gradeType = new GradeTypes(gradeTypeEnum);

        subGradeService.getSubGradeByUserIdAndSprintIdAndGradeType(1, 1, gradeType);

        verify(subGradeDao, times(1)).save(any(SubGrade.class));
    }

    @Test
    void getEvaluationBySubgradeShouldReturnExistingEvaluation() {
        Evaluation evaluation = mock(Evaluation.class);
        when(evaluationService.getEvaluationBySubGradeIdAndEvaluatorId(anyInt(), anyInt())).thenReturn(evaluation);

        Evaluation result = subGradeService.getEvaluationBySubgrade(1, 1);

        assertEquals(evaluation, result);
    }

    @Test
    void getEvaluationBySubgradeShouldCreateNewEvaluationWhenNoneExists() {
        when(evaluationService.getEvaluationBySubGradeIdAndEvaluatorId(anyInt(), anyInt())).thenThrow(new RuntimeException());
        SubGrade subGrade = mock(SubGrade.class);
        when(subGradeDao.findById(anyInt())).thenReturn(Optional.of(subGrade));
        User user = mock(User.class);
        when(userService.getUserById(anyInt())).thenReturn(user);
        Evaluation evaluation = mock(Evaluation.class);
        when(evaluationService.addEvaluator(any(SubGrade.class), any(User.class))).thenReturn(evaluation);

        Evaluation result = subGradeService.getEvaluationBySubgrade(1, 1);

        assertEquals(evaluation, result);
    }
}
