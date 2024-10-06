package com.example.backend.service;

import com.example.backend.dao.GradeTypesDao;
import com.example.backend.dao.ProjectGradeDao;
import com.example.backend.dto.FinalGradeDTO;
import com.example.backend.dto.InitialGradeDTO;
import com.example.backend.dto.IntermediateGradeDTO;
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

class ProjectGradeServiceTest {

    @InjectMocks
    private ProjectGradeService projectGradeService;

    @Mock
    private ProjectGradeDao projectGradeDao;
    @Mock
    private TeamGradeService teamGradeService;
    @Mock
    private TeamService teamService;
    @Mock
    private UserService userService;
    @Mock
    private SprintService sprintService;
    @Mock
    private TeamGradeFromStudentService teamGradeFromStudentService;

    @Mock
    private InitialGradeService initialGradeService;
    @Mock
    private WorkGradeService workGradeService;
    @Mock
    private PresentationGradeService presentationGradeService;
    @Mock
    private SubGradeService subGradeService;
    @Mock
    private BonusMalusService bonusMalusService;

    @Mock
    private GradeTypesDao gradeTypesDao;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveProjectGrade_shouldCallDaoSaveAndUpdateProjectGrade() {
        int userId = 1;
        int sprintId = 1;
        int teamId = 1;

        double grade = 10;
        User user = new User();
        user.setId(userId);
        Sprint sprint = new Sprint();
        Team team = new Team();
        team.setId(teamId);
        ProjectGrade projectGrade = new ProjectGrade();
        projectGrade.setUser(user);
        projectGrade.setSprint(sprint);

        when(userService.getUserById(userId)).thenReturn(user);
        when(sprintService.getSprintById(sprintId)).thenReturn(sprint);
        when(teamGradeFromStudentService.getOTPR(teamId,sprintId)).thenReturn(grade);
        when(teamService.getTeamByUserId(userId)).thenReturn(team);
        when(projectGradeDao.save(any(ProjectGrade.class))).thenReturn(projectGrade);
        when(projectGradeDao.findByUserIdAndSprintId(userId, sprintId)).thenReturn(Optional.of(projectGrade));

        projectGradeService.saveProjectGrade(userId, sprintId);

        verify(userService, times(1)).getUserById(userId);
        verify(sprintService, times(1)).getSprintById(sprintId);
        verify(projectGradeDao, times(1)).save(any(ProjectGrade.class));
    }

    @Test
    void getProjectGradeByUserIdAndSprintId_shouldReturnProjectGrade() {
        int userId = 1;
        int sprintId = 1;
        ProjectGrade projectGrade = new ProjectGrade();

        when(projectGradeDao.findByUserIdAndSprintId(userId, sprintId)).thenReturn(Optional.of(projectGrade));

        ProjectGrade result = projectGradeService.getProjectGradeByUserIdAndSprintId(userId, sprintId);

        verify(projectGradeDao, times(1)).findByUserIdAndSprintId(userId, sprintId);
        assertEquals(projectGrade, result);
    }

    @Test
    void getProjectGradeByUserIdAndSprintId_shouldThrowException() {
        int userId = 1;
        int sprintId = 1;

        when(projectGradeDao.findByUserIdAndSprintId(userId, sprintId)).thenReturn(Optional.empty());

        projectGradeService.getProjectGradeByUserIdAndSprintId(userId, sprintId);

        verify(projectGradeDao, times(1)).findByUserIdAndSprintId(userId, sprintId);
        verify(projectGradeDao, times(1)).save(any(ProjectGrade.class));
    }

    @Test
    void updateProjectGrade_tryBlock() {
        int userId = 1;
        int sprintId = 1;
        int teamId = 1;
        Team team = new Team();
        team.setId(teamId);
        ProjectGrade projectGrade = new ProjectGrade();
        WorkGrade workGrade = new WorkGrade();
        InitialGrade initialGrade = new InitialGrade();
        PresentationGrade presentationGrade = new PresentationGrade();

        when(teamService.getTeamByUserId(userId)).thenReturn(team);
        when(projectGradeDao.findByUserIdAndSprintId(userId, sprintId)).thenReturn(Optional.of(projectGrade));
        when(projectGradeDao.save(projectGrade)).thenReturn(projectGrade);
        when(initialGradeService.updateInitialGrade(userId, sprintId)).thenReturn(initialGrade);
        when(workGradeService.updateWorkGrade(userId, sprintId)).thenReturn(workGrade);
        when(presentationGradeService.updatePresentationGrade(userId, sprintId)).thenReturn(presentationGrade);

        ProjectGrade result = projectGradeService.updateProjectGrade(userId, sprintId);

        verify(projectGradeDao, times(1)).findByUserIdAndSprintId(userId, sprintId);
        verify(projectGradeDao, times(1)).save(any(ProjectGrade.class));
        assertEquals(projectGrade, result);
    }

    @Test
    void updateProjectGrade_catchBlock() {
        int userId = 1;
        int sprintId = 1;
        int teamId = 1;
        Team team = new Team();
        team.setId(teamId);

        when(teamService.getTeamByUserId(userId)).thenReturn(team);
        when(projectGradeDao.findByUserIdAndSprintId(userId, sprintId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> projectGradeService.updateProjectGrade(userId, sprintId));

        verify(projectGradeDao, times(1)).findByUserIdAndSprintId(userId, sprintId);
    }

    @Test
    void updateAllProjectGrades_shouldUpdateAllProjectGradesForAllUsersAndSprintsValueGreaterThan20() {
        // Given
        User user1 = new User();
        user1.setId(1);
        User user2 = new User();
        user2.setId(2);
        List<User> users = Arrays.asList(user1, user2);

        Sprint sprint1 = new Sprint();
        sprint1.setId(1);
        Sprint sprint2 = new Sprint();
        sprint2.setId(2);
        List<Sprint> sprints = Arrays.asList(sprint1, sprint2);

        ProjectGrade projectGrade = new ProjectGrade();
        projectGrade.setInitialGrade(new InitialGrade());

        WorkGrade workGrade = new WorkGrade();
        workGrade.setValue(11);
        InitialGrade initialGrade = new InitialGrade();
        initialGrade.setValue(11);

        when(userService.getStudents()).thenReturn(users);
        when(sprintService.getAllSprints()).thenReturn(sprints);
        when(projectGradeDao.findByUserIdAndSprintId(anyInt(), anyInt())).thenReturn(Optional.of(projectGrade));
        when(workGradeService.getWorkGradeByUserIdAndSprintId(anyInt(), anyInt())).thenReturn(workGrade);
        when(workGradeService.updateWorkGrade(anyInt(), anyInt())).thenReturn(workGrade);
        when(initialGradeService.updateInitialGrade(anyInt(), anyInt())).thenReturn(initialGrade);
        when(presentationGradeService.updatePresentationGrade(anyInt(), anyInt())).thenReturn(new PresentationGrade());

        // When
        projectGradeService.updateAllProjectGrades();

        // Then
        verify(userService, times(1)).getStudents();
    }

    @Test
    void updateAllProjectGrades_shouldUpdateAllProjectGradesForAllUsersAndSprintsValueLesserThan0() {
        // Given
        User user1 = new User();
        user1.setId(1);
        User user2 = new User();
        user2.setId(2);
        List<User> users = Arrays.asList(user1, user2);

        Sprint sprint1 = new Sprint();
        sprint1.setId(1);
        Sprint sprint2 = new Sprint();
        sprint2.setId(2);
        List<Sprint> sprints = Arrays.asList(sprint1, sprint2);

        ProjectGrade projectGrade = new ProjectGrade();
        projectGrade.setInitialGrade(new InitialGrade());

        WorkGrade workGrade = new WorkGrade();
        workGrade.setValue(-11);
        InitialGrade initialGrade = new InitialGrade();
        initialGrade.setValue(5);

        when(userService.getStudents()).thenReturn(users);
        when(sprintService.getAllSprints()).thenReturn(sprints);
        when(projectGradeDao.findByUserIdAndSprintId(anyInt(), anyInt())).thenReturn(Optional.of(projectGrade));
        when(workGradeService.getWorkGradeByUserIdAndSprintId(anyInt(), anyInt())).thenReturn(workGrade);
        when(workGradeService.updateWorkGrade(anyInt(), anyInt())).thenReturn(workGrade);
        when(initialGradeService.updateInitialGrade(anyInt(), anyInt())).thenReturn(initialGrade);
        when(presentationGradeService.updatePresentationGrade(anyInt(), anyInt())).thenReturn(new PresentationGrade());

        // When
        projectGradeService.updateAllProjectGrades();

        // Then
        verify(userService, times(1)).getStudents();
    }

}
