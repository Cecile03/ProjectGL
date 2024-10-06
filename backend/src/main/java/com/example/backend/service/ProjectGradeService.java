package com.example.backend.service;

import com.example.backend.dao.ProjectGradeDao;
import com.example.backend.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service de gestion des notes de projet.
 */
@Service
public class ProjectGradeService {

    private final ProjectGradeDao projectGradeDao;
    private final InitialGradeService initialGradeService;
    private final WorkGradeService workGradeService;
    private final PresentationGradeService presentationGradeService;
    private final UserService userService;
    private final SprintService sprintService;

    /**
     * Constructeur de la classe ProjectGradeService.
     *
     * @param projectGradeDao Le DAO des notes de projet.
     * @param initialGradeService Le service de gestion des notes initiales.
     * @param workGradeService Le service de gestion des notes de travail.
     * @param presentationGradeService Le service de gestion des notes de présentation.
     * @param userService Le service de gestion des utilisateurs.
     * @param sprintService Le service de gestion des sprints.
     */
    @Autowired
    public ProjectGradeService(ProjectGradeDao projectGradeDao, InitialGradeService initialGradeService, WorkGradeService workGradeService, PresentationGradeService presentationGradeService, UserService userService, SprintService sprintService) {
        this.projectGradeDao = projectGradeDao;
        this.initialGradeService = initialGradeService;
        this.workGradeService = workGradeService;
        this.presentationGradeService = presentationGradeService;
        this.userService = userService;
        this.sprintService = sprintService;
    }

    /**
     * Enregistre les notes de projet.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @param sprintId L'identifiant du sprint.
     * @return Les notes de projet enregistrées.
     */
    public ProjectGrade saveProjectGrade(int userId, int sprintId) {
        ProjectGrade projectGrade = new ProjectGrade();
        projectGrade.setUser(userService.getUserById(userId));
        projectGrade.setSprint(sprintService.getSprintById(sprintId));
        return projectGradeDao.save(projectGrade);
    }

    /**
     * Met à jour les notes de projet.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @param sprintId L'identifiant du sprint.
     * @return Les notes de projet mises à jour.
     */
    public ProjectGrade updateProjectGrade(int userId, int sprintId) {
        ProjectGrade projectGrade = getProjectGradeByUserIdAndSprintId(userId, sprintId);
        calculateAllGrades(userId, sprintId, projectGrade);
        return projectGradeDao.save(projectGrade);
    }

    /**
     * Calcule toutes les notes de projet.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @param sprintId L'identifiant du sprint.
     * @param projectGrade Les notes de projet.
     */
    private void calculateAllGrades(int userId, int sprintId, ProjectGrade projectGrade) {
        InitialGrade initialGrade = initialGradeService.updateInitialGrade(userId, sprintId);
        projectGrade.setInitialGrade(initialGrade);
        WorkGrade workGrade = workGradeService.updateWorkGrade(userId, sprintId);
        if ((workGrade.getValue()+initialGrade.getValue()<0)){
            workGrade.setValue(0);
        } else if ((workGrade.getValue()+initialGrade.getValue()>20)){
            workGrade.setValue(20);
        } else {
            workGrade.setValue(workGrade.getValue()+initialGrade.getValue());
        }
        workGradeService.saveWorkGrade(workGrade);
        projectGrade.setWorkGrade(workGrade);
        PresentationGrade presentationGrade = presentationGradeService.updatePresentationGrade(userId, sprintId);
        projectGrade.setPresentationGrade(presentationGrade);
        projectGrade.setValue(workGrade.getValue()*0.7 + presentationGrade.getValue()*0.3);
    }

    /**
     * Récupère les notes de projet par l'identifiant de l'utilisateur et du sprint.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @param sprintId L'identifiant du sprint.
     * @return Les notes de projet.
     */
    public ProjectGrade getProjectGradeByUserIdAndSprintId(int userId, int sprintId) {
        try{
            return projectGradeDao.findByUserIdAndSprintId(userId, sprintId).orElseThrow();
        } catch (Exception e) {
            return saveProjectGrade(userId, sprintId);
        }
    }

    /**
     * Met à jour toutes les notes de projet.
     */
    public void updateAllProjectGrades(){
        for(User user: userService.getStudents()){
            for(Sprint sprint: sprintService.getAllSprints()){
                updateProjectGrade(user.getId(), sprint.getId());
            }
        }
    }

    /**
     * Valide les notes de projet.
     *
     * @param projectGradeId L'identifiant des notes de projet.
     */
    public void validateProjectGrade(int projectGradeId) {
        ProjectGrade projectGrade = getProjectGradeById(projectGradeId);
        projectGrade.setValidated(true);
        projectGradeDao.save(projectGrade);
    }

    /**
     * Invalide les notes de projet.
     *
     * @param projectGradeId L'identifiant des notes de projet.
     */
    private ProjectGrade getProjectGradeById(int projectGradeId) {
        return projectGradeDao.findById(projectGradeId).orElseThrow();
    }
}
