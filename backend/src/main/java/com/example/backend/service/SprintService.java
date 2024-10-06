package com.example.backend.service;

import com.example.backend.dao.*;
import com.example.backend.dto.SprintDTO;
import com.example.backend.model.Sprint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service de gestion des sprints.
 */
@Service
public class SprintService {

    private final SprintDao sprintDao;
    private final TeamGradeFromStudentDao teamGradeFromStudentDao;
    private final TeamOrderDao teamOrderDao;
    private final FeedbackDao feedbackDao;
    private final CommentDao commentDao;
    private final TeamGradeDao teamGrade;

    /**
     * Constructeur de la classe SprintService.
     *
     * @param sprintDao Le DAO des sprints.
     * @param teamGradeFromStudentDao Le DAO des notes d'équipe des étudiants.
     * @param teamOrderDao Le DAO des ordres d'équipe.
     * @param feedbackDao Le DAO des retours.
     * @param commentDao Le DAO des commentaires.
     * @param teamGrade Le DAO des notes d'équipe.
     */
    @Autowired
    public SprintService(SprintDao sprintDao, TeamGradeFromStudentDao teamGradeFromStudentDao, TeamOrderDao teamOrderDao, FeedbackDao feedbackDao, CommentDao commentDao, TeamGradeDao teamGrade) {
        this.sprintDao = sprintDao;
        this.teamGradeFromStudentDao = teamGradeFromStudentDao;
        this.teamOrderDao = teamOrderDao;
        this.feedbackDao = feedbackDao;
        this.commentDao = commentDao;
        this.teamGrade = teamGrade;
    }

    /**
     * Met à jour un sprint.
     *
     * @param newSprintData Les nouvelles données du sprint.
     */
    public void update(Sprint newSprintData) {
        try{
            Sprint sprint = sprintDao.findById(newSprintData.getId()).orElseThrow();
            sprint.setStartDate(newSprintData.getStartDate());
            sprint.setEndDate(newSprintData.getEndDate());
            sprint.setEndType(newSprintData.getEndType());
            sprintDao.save(sprint);
        } catch (Exception e) {
            saveSprint(newSprintData);
        }
    }

    /**
     * Récupère tous les sprints.
     *
     * @return La liste des sprints.
     */
    public List<Sprint> getAll() {
        return sprintDao.findAll();
    }

    /**
     * Récupère un sprint par son identifiant.
     *
     * @param id L'identifiant du sprint.
     * @return Le sprint.
     */
    public Sprint getSprintById(int id){
        return sprintDao.findById(id).orElseThrow();
    }

    /**
     * Supprime tous les sprints.
     */
    public void deleteAll() {
        teamGrade.deleteAll();
        feedbackDao.deleteAll();
        commentDao.deleteAll();
        teamGradeFromStudentDao.deleteAll();
        teamOrderDao.deleteAll();
        sprintDao.deleteAll();
    }

    /**
     * Sauvegarde un sprint.
     *
     * @param sprint Le sprint à sauvegarder.
     */
    public void saveSprint(Sprint sprint) {
        sprintDao.save(sprint);
    }

    /**
     * Crée un sprint.
     *
     * @param sprintDTO Les données du sprint.
     */
    public Sprint fromSprintDTO(SprintDTO sprintDTO) {
        Sprint sprint = new Sprint();
        sprint.setId(sprintDTO.getId());
        sprint.setStartDate(sprintDTO.getStartDate());
        sprint.setEndDate(sprintDTO.getEndDate());
        sprint.setEndType(sprintDTO.getEndType());
        return sprint;
    }

    /**
     * Convertit un sprint en DTO.
     *
     * @param sprint Le sprint.
     * @return Le DTO du sprint.
     */
    public SprintDTO toDTO(Sprint sprint) {
        SprintDTO sprintDTO = new SprintDTO();
        sprintDTO.setStartDate(sprint.getStartDate());
        sprintDTO.setEndDate(sprint.getEndDate());
        sprintDTO.setEndType(sprint.getEndType());
        return sprintDTO;
    }

    /**
     * Récupère tous les sprints.
     *
     * @return La liste des sprints.
     */
    public List<Sprint> getAllSprints() {
        return sprintDao.findAll();
    }
}
