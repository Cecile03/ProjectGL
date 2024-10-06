package com.example.backend.service;

import com.example.backend.dao.*;
import com.example.backend.model.Role;
import com.example.backend.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service de nettoyage de la base de données.
 */
@Service
@RequiredArgsConstructor
public class CleanupService {

    private final TeamOrderDao teamOrderDao;
    private final TeamGradeDao teamGradeDao;
    private final TeamGradeFromStudentDao teamGradeFromStudentDao;
    private final BonusMalusDao bonusMalusDao;
    private final UserFlagDao userFlagDao;
    private final FlagDao flagDao;
    private final UserTeamDao userTeamDao;
    private final TeamDao teamDao;
    private final CriteriaDao criteriaDao;
    private final UserDao userDao;
    private final ProjectGradeDao projectGradeDao;
    private final NotificationDao notificationDao;
    private final RoleDao roleDao;
    private final EvaluationDao evaluationDao;
    private final CommentDao commentDao;
    private final FeedbackDao feedbackDao;
    private final BMValidationDao bmValidationDao;
    private final DetailDao detailDao;
    private final CategoryDao categoryDao;
    private final GradeScaleDao gradeScaleDao;
    private final SprintDao sprintDao;

    /**
     * Supprime toutes les équipes.
     */
    public void deleteAllTeam() {
        feedbackDao.deleteAll();
        commentDao.deleteAll();
        evaluationDao.deleteAll();
        projectGradeDao.deleteAll();
        teamOrderDao.deleteAll();
        teamGradeDao.deleteAll();
        detailDao.deleteAll();
        categoryDao.deleteAll();
        gradeScaleDao.deleteAll();
        teamGradeFromStudentDao.deleteAll();
        bmValidationDao.deleteAll();
        bonusMalusDao.deleteAll();
        userFlagDao.deleteAll();
        flagDao.deleteAll();
        userTeamDao.deleteAll();
        teamDao.deleteAll();
        criteriaDao.deleteAll();
        notificationDao.deleteAll();
        sprintDao.deleteAll();
    }

    /**
     * Supprime tous les étudiants.
     */
    public void deleteAllStudents() {
        deleteAllTeam();
        List<User> users = userDao.findByRoles(roleDao.findByName(Role.RoleName.OS).orElseThrow()).orElseThrow();
        userDao.deleteAll(users);
    }
}
