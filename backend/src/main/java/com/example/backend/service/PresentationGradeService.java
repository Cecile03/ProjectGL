package com.example.backend.service;

import com.example.backend.dao.GradeTypesDao;
import com.example.backend.dao.PresentationGradeDao;
import com.example.backend.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service de gestion des notes de présentation.
 */
@Service
public class PresentationGradeService {

    private final UserService userService;
    private final SprintService sprintService;
    private final SubGradeService subGradeService;
    private final PresentationGradeDao presentationGradeDao;
    private final GradeTypesDao gradeTypesDao;

    /**
     * Constructeur de la classe PresentationGradeService.
     *
     * @param userService Le service de gestion des utilisateurs.
     * @param sprintService Le service de gestion des sprints.
     * @param subGradeService Le service de gestion des sous-notes.
     * @param presentationGradeDao Le DAO des notes de présentation.
     * @param gradeTypesDao Le DAO des types de notes.
     */
    @Autowired
    public PresentationGradeService(UserService userService, SprintService sprintService, SubGradeService subGradeService, PresentationGradeDao presentationGradeDao, GradeTypesDao gradeTypesDao) {
        this.userService = userService;
        this.sprintService = sprintService;
        this.subGradeService = subGradeService;
        this.presentationGradeDao = presentationGradeDao;
        this.gradeTypesDao = gradeTypesDao;
    }

    /**
     * Met à jour les notes de présentation.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @param sprintId L'identifiant du sprint.
     * @return Les notes de présentation mises à jour.
     */
    public PresentationGrade updatePresentationGrade(int userId, int sprintId) {
        GradeTypes ssprType = gradeTypesDao.findById(GradeTypes.GradeTypesEnum.SSPR.getId()).orElseThrow();
        GradeTypes otprType = gradeTypesDao.findById(GradeTypes.GradeTypesEnum.OTPR.getId()).orElseThrow();
        GradeTypes tcprType = gradeTypesDao.findById(GradeTypes.GradeTypesEnum.TCPR.getId()).orElseThrow();

        SubGrade sspr =subGradeService.updateSubGrade(userId, sprintId, ssprType);
        SubGrade otpr =subGradeService.updateSubGrade(userId, sprintId, otprType);
        SubGrade tcpr =subGradeService.updateSubGrade(userId, sprintId, tcprType);

        PresentationGrade presentationGrade = getPresentationGradeByUserIdAndSprintId(userId, sprintId);
        presentationGrade.setSspr(sspr);
        presentationGrade.setOtpr(otpr);
        presentationGrade.setTcpr(tcpr);
        presentationGrade.setValue((sspr.getValue()*2 + otpr.getValue() + tcpr.getValue())/4);
        return presentationGradeDao.save(presentationGrade);

    }

    /**
     * Crée une note de présentation.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @param sprintId L'identifiant du sprint.
     * @return La note de présentation créée.
     */
    public PresentationGrade createPresentationGrade(int userId, int sprintId) {
        PresentationGrade presentationGrade = new PresentationGrade();
        presentationGrade.setUser(userService.getUserById(userId));
        presentationGrade.setSprint(sprintService.getSprintById(sprintId));
        return presentationGradeDao.save(presentationGrade);
    }

    /**
     * Récupère une note de présentation par l'identifiant de l'utilisateur et du sprint.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @param sprintId L'identifiant du sprint.
     * @return La note de présentation créée.
     */
    public PresentationGrade getPresentationGradeByUserIdAndSprintId(int userId, int sprintId) {
        try{
            return presentationGradeDao.findByUserIdAndSprintId(userId, sprintId).orElseThrow();
        } catch (Exception e) {
            return createPresentationGrade(userId, sprintId);
        }
    }
}
