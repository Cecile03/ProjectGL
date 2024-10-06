package com.example.backend.service;

import com.example.backend.dao.GradeTypesDao;
import com.example.backend.dao.WorkGradeDao;
import com.example.backend.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service de gestion des notes de travail.
 */
@Service
public class WorkGradeService {

    private final UserService userService;
    private final SprintService sprintService;
    private final SubGradeService subGradeService;
    private final WorkGradeDao workGradeDao;
    private final GradeTypesDao gradeTypesDao;

    /**
     * Constructeur de la classe WorkGradeService.
     *
     * @param userService Le service de gestion des utilisateurs.
     * @param sprintService Le service de gestion des sprints.
     * @param subGradeService Le service de gestion des sous-notes.
     * @param workGradeDao Le DAO des notes de travail.
     * @param gradeTypesDao Le DAO des types de notes.
     */
    @Autowired
    public WorkGradeService(UserService userService, SprintService sprintService, SubGradeService subGradeService, WorkGradeDao workGradeDao, GradeTypesDao gradeTypesDao) {
        this.userService = userService;
        this.sprintService = sprintService;
        this.subGradeService = subGradeService;
        this.workGradeDao = workGradeDao;
        this.gradeTypesDao = gradeTypesDao;
    }

    /**
     * Met à jour une note de travail.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @param sprintId L'identifiant du sprint.
     * @return La note de travail mise à jour.
     */
    public WorkGrade updateWorkGrade(int userId, int sprintId) {
        GradeTypes ssbmType = gradeTypesDao.findById(GradeTypes.GradeTypesEnum.SSBM.getId()).orElseThrow();
        GradeTypes tebmType = gradeTypesDao.findById(GradeTypes.GradeTypesEnum.TEBM.getId()).orElseThrow();

        SubGrade ssbm = subGradeService.updateSubGrade(userId, sprintId, ssbmType);
        SubGrade tebm = subGradeService.updateSubGrade(userId, sprintId, tebmType);

        WorkGrade workGrade = getWorkGradeByUserIdAndSprintId(userId, sprintId);
        workGrade.setSsbm(ssbm);
        workGrade.setTebm(tebm);
        workGrade.setValue((ssbm.getValue() + tebm.getValue()));
        return workGradeDao.save(workGrade);
    }

    /**
     * Crée une note de travail.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @param sprintId L'identifiant du sprint.
     * @return La note de travail créée.
     */
    public WorkGrade createWorkGrade(int userId, int sprintId) {
        WorkGrade workGrade = new WorkGrade();
        workGrade.setUser(userService.getUserById(userId));
        workGrade.setSprint(sprintService.getSprintById(sprintId));
        return workGradeDao.save(workGrade);
    }

    /**
     * Récupère une note de travail par l'identifiant de l'utilisateur et l'identifiant du sprint.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @param sprintId L'identifiant du sprint.
     * @return La note de travail.
     */
    public WorkGrade getWorkGradeByUserIdAndSprintId(int userId, int sprintId) {
        try {
            return workGradeDao.findByUserIdAndSprintId(userId, sprintId).orElseThrow();
        } catch (Exception e) {
            return createWorkGrade(userId, sprintId);
        }
    }

    /**
     * Enregistre une note de travail.
     *
     * @param workGrade La note de travail.
     */
    public void saveWorkGrade(WorkGrade workGrade) {
        workGradeDao.save(workGrade);
    }
}
