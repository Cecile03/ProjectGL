package com.example.backend.service;

import com.example.backend.dao.GradeTypesDao;
import com.example.backend.dao.InitialGradeDao;
import com.example.backend.model.GradeTypes;
import com.example.backend.model.InitialGrade;
import com.example.backend.model.SubGrade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service de gestion des notes initiales.
 */
@Service
public class InitialGradeService {

    private final UserService userService;
    private final SprintService sprintService;
    private final SubGradeService subGradeService;
    private final InitialGradeDao initialGradeDao;
    private final GradeTypesDao gradeTypesDao;

    /**
     * Constructeur de la classe InitialGradeService.
     *
     * @param userService Le service de gestion des utilisateurs.
     * @param sprintService Le service de gestion des sprints.
     * @param subGradeService Le service de gestion des sous-notes.
     * @param initialGradeDao Le DAO des notes initiales.
     * @param gradeTypesDao Le DAO des types de notes.
     */
    @Autowired
    public InitialGradeService(UserService userService, SprintService sprintService, SubGradeService subGradeService, InitialGradeDao initialGradeDao, GradeTypesDao gradeTypesDao) {
        this.userService = userService;
        this.sprintService = sprintService;
        this.subGradeService = subGradeService;
        this.initialGradeDao = initialGradeDao;
        this.gradeTypesDao = gradeTypesDao;
    }

    /**
     * Met à jour les notes initiales.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @param projectId L'identifiant du projet.
     * @return Les notes initiales mises à jour.
     */
    public InitialGrade updateInitialGrade(int userId, int projectId) {
        GradeTypes.GradeTypesEnum gt = GradeTypes.GradeTypesEnum.PRMO;
        GradeTypes prmoType = gradeTypesDao.findById(gt.getId()).orElseThrow();
        GradeTypes spcoType = gradeTypesDao.findById(GradeTypes.GradeTypesEnum.SPCO.getId()).orElseThrow();
        GradeTypes tesoType = gradeTypesDao.findById(GradeTypes.GradeTypesEnum.TESO.getId()).orElseThrow();
        GradeTypes suprType = gradeTypesDao.findById(GradeTypes.GradeTypesEnum.SUPR.getId()).orElseThrow();

        SubGrade prmo = subGradeService.updateSubGrade(userId, projectId, prmoType);
        SubGrade spco = subGradeService.updateSubGrade(userId, projectId, spcoType);
        SubGrade teso = subGradeService.updateSubGrade(userId, projectId, tesoType);
        SubGrade supr = subGradeService.updateSubGrade(userId, projectId, suprType);

        InitialGrade initialGrade = getInitialGradeByUserIdAndProjectId(userId, projectId);
        initialGrade.setPrmo(prmo);
        initialGrade.setSpco(spco);
        initialGrade.setTeso(teso);
        initialGrade.setSupr(supr);
        initialGrade.setValue((prmo.getValue() + spco.getValue() + teso.getValue() + supr.getValue()) / 4);
        return initialGradeDao.save(initialGrade);
    }

    /**
     * Crée des notes initiales.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @param projectId L'identifiant du projet.
     * @return Les notes initiales créées.
     */
    public InitialGrade createInitialGrade(int userId, int projectId) {
        InitialGrade initialGrade = new InitialGrade();
        initialGrade.setUser(userService.getUserById(userId));
        initialGrade.setSprint(sprintService.getSprintById(projectId));
        return initialGradeDao.save(initialGrade);
    }

    /**
     * Récupère les notes initiales par l'identifiant de l'utilisateur et du projet.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @param projectId L'identifiant du projet.
     * @return Les notes initiales.
     */
    public InitialGrade getInitialGradeByUserIdAndProjectId(int userId, int projectId) {
        try {
            return initialGradeDao.findByUserIdAndSprintId(userId, projectId).orElseThrow();
        } catch (Exception e) {
            return createInitialGrade(userId, projectId);
        }
    }
}
