package com.example.backend.service;

import com.example.backend.dao.*;
import com.example.backend.model.Role;
import com.example.backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.mockito.Mockito.*;

class CleanupServiceTest {

    @Mock
    private TeamOrderDao teamOrderDao;
    @Mock
    private TeamGradeDao teamGradeDao;
    @Mock
    private TeamGradeFromStudentDao teamGradeFromStudentDao;
    @Mock
    private BonusMalusDao bonusMalusDao;
    @Mock
    private UserFlagDao userFlagDao;
    @Mock
    private FlagDao flagDao;
    @Mock
    private UserTeamDao userTeamDao;
    @Mock
    private TeamDao teamDao;
    @Mock
    private CriteriaDao criteriaDao;
    @Mock
    private UserDao userDao;
    @Mock
    private ProjectGradeDao projectGradeDao;
    @Mock
    private NotificationDao notificationDao;
    @Mock
    private RoleDao roleDao;
    @Mock
    private EvaluationDao evaluationDao;
    @Mock
    private SprintDao sprintDao;
    @Mock
    private CommentDao commentDao;
    @Mock
    private FeedbackDao feedbackDao;
    @Mock
    private BMValidationDao bmValidationDao;
    @Mock
    private DetailDao detailDao;
    @Mock
    private CategoryDao categoryDao;
    @Mock
    private GradeScaleDao gradeScaleDao;

    @InjectMocks
    private CleanupService cleanupService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deleteAllTeam_shouldCallDeleteAllOnAllDaos() {
        cleanupService.deleteAllTeam();

        verify(feedbackDao, times(1)).deleteAll();
        verify(commentDao, times(1)).deleteAll();
        verify(evaluationDao, times(1)).deleteAll();
        verify(projectGradeDao, times(1)).deleteAll();
        verify(teamOrderDao, times(1)).deleteAll();
        verify(teamGradeDao, times(1)).deleteAll();
        verify(detailDao, times(1)).deleteAll();
        verify(categoryDao, times(1)).deleteAll();
        verify(gradeScaleDao, times(1)).deleteAll();
        verify(teamGradeFromStudentDao, times(1)).deleteAll();
        verify(bmValidationDao, times(1)).deleteAll();
        verify(bonusMalusDao, times(1)).deleteAll();
        verify(userFlagDao, times(1)).deleteAll();
        verify(flagDao, times(1)).deleteAll();
        verify(userTeamDao, times(1)).deleteAll();
        verify(teamDao, times(1)).deleteAll();
        verify(criteriaDao, times(1)).deleteAll();
        verify(notificationDao, times(1)).deleteAll();
        verify(sprintDao, times(1)).deleteAll();
    }

    @Test
    void deleteAllStudents_shouldCallDeleteAllOnAllDaosAndDeleteAllUsers() {
        Role role = new Role();
        role.setName(Role.RoleName.OS);
        User user = new User();
        user.setId(1);

        when(roleDao.findByName(Role.RoleName.OS)).thenReturn(java.util.Optional.of(role));
        when(userDao.findByRoles(role)).thenReturn(java.util.Optional.of(Collections.singletonList(user)));

        cleanupService.deleteAllStudents();

        verify(feedbackDao, times(1)).deleteAll();
        verify(commentDao, times(1)).deleteAll();
        verify(evaluationDao, times(1)).deleteAll();
        verify(projectGradeDao, times(1)).deleteAll();
        verify(teamOrderDao, times(1)).deleteAll();
        verify(teamGradeDao, times(1)).deleteAll();
        verify(detailDao, times(1)).deleteAll();
        verify(categoryDao, times(1)).deleteAll();
        verify(gradeScaleDao, times(1)).deleteAll();
        verify(teamGradeFromStudentDao, times(1)).deleteAll();
        verify(bmValidationDao, times(1)).deleteAll();
        verify(bonusMalusDao, times(1)).deleteAll();
        verify(userFlagDao, times(1)).deleteAll();
        verify(flagDao, times(1)).deleteAll();
        verify(userTeamDao, times(1)).deleteAll();
        verify(teamDao, times(1)).deleteAll();
        verify(criteriaDao, times(1)).deleteAll();
        verify(notificationDao, times(1)).deleteAll();
        verify(userDao, times(1)).deleteAll(Collections.singletonList(user));
        verify(sprintDao, times(1)).deleteAll();

    }
}
