package com.example.backend.service;

import com.example.backend.dao.TeamGradeDao;
import com.example.backend.model.Detail;
import com.example.backend.model.TeamGrade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service de gestion des notes d'équipe.
 */
@Service
public class TeamGradeService {

    private final TeamGradeDao teamGradeDao;
    private final TeamService teamService;
    private final SprintService sprintService;
    private final DetailService detailService;
    private final UserService userService;

    /**
     * Constructeur de la classe TeamGradeService.
     *
     * @param teamGradeDao Le DAO des notes d'équipe.
     * @param teamService Le service de gestion des équipes.
     * @param detailService Le service de gestion des détails.
     * @param sprintService Le service de gestion des sprints.
     * @param userService Le service de gestion des utilisateurs.
     */
    @Autowired
    public TeamGradeService(TeamGradeDao teamGradeDao, TeamService teamService, DetailService detailService, SprintService sprintService, UserService userService){
        this.teamGradeDao = teamGradeDao;
        this.teamService = teamService;
        this.detailService = detailService;
        this.sprintService = sprintService;
        this.userService = userService;
    }

    /**
     * Enregistre une note d'équipe.
     *
     * @param teamId L'identifiant de l'équipe.
     * @param sprintId L'identifiant du sprint.
     * @param detailId L'identifiant du détail.
     * @param evaluatorId L'identifiant de l'évaluateur.
     * @param grade La note.
     */
    public void saveTeamGrade(int teamId, int sprintId, int detailId, int evaluatorId, Double grade) {
        TeamGrade teamGrade = new TeamGrade();
        teamGrade.setGrade(grade);
        teamGrade.setTeam(teamService.getTeamById(teamId));
        teamGrade.setSprint(sprintService.getSprintById(sprintId));
        teamGrade.setDetail(detailService.getDetailById(detailId));
        teamGrade.setEvaluator(userService.getUserById(evaluatorId));
        teamGradeDao.save(teamGrade);
    }

    /**
     * Met à jour une note d'équipe.
     *
     * @param teamId L'identifiant de l'équipe.
     * @param sprintId L'identifiant du sprint.
     * @param detailId L'identifiant du détail.
     * @param evaluatorId L'identifiant de l'évaluateur.
     * @param grade La note.
     */
    public void updateTeamGrade(int teamId, int sprintId, int detailId, int evaluatorId, Double grade) {
        try{
            TeamGrade teamGrade = teamGradeDao.findByTeamIdAndSprintIdAndDetailIdAndEvaluatorId(teamId, sprintId, detailId, evaluatorId).orElseThrow();
            teamGrade.setGrade(grade);
            teamGradeDao.save(teamGrade);
        } catch (Exception e) {
            saveTeamGrade(teamId, sprintId, detailId, evaluatorId, grade);
        }
    }

    /**
     * Récupère une note d'équipe.
     *
     * @param teamId L'identifiant de l'équipe.
     * @param sprintId L'identifiant du sprint.
     * @param detailId L'identifiant du détail.
     * @param evaluatorId L'identifiant de l'évaluateur.
     * @return La note d'équipe.
     */
    public TeamGrade getTeamGrade(int teamId, int sprintId, int detailId, int evaluatorId) {
        return teamGradeDao.findByTeamIdAndSprintIdAndDetailIdAndEvaluatorId(teamId, sprintId, detailId, evaluatorId).orElse(null);
    }

    /**
     * Récupère toutes les notes d'équipe.
     *
     * @return La liste des notes d'équipe.
     */
    public Double getAverageTeamGradeByCategoryId(int categoryId, int sprintId, int teamId, int evaluatorId) {
        List<Detail> details = detailService.getDetailsByCategoryId(categoryId);
        double totalGrade = 0.0;
        int count = 0;
        for (Detail detail : details) {
            Optional<TeamGrade> teamGrade = teamGradeDao.findByTeamIdAndSprintIdAndDetailIdAndEvaluatorId(teamId, sprintId, detail.getId(),evaluatorId);
            if (teamGrade.isPresent()) {
                totalGrade += (teamGrade.get().getGrade() / (double) detail.getMark()) * 20;
                count++;
            }
        }
        return count > 0 ? totalGrade / count : null;
    }
    public void deleteAll() {
        teamGradeDao.deleteAll();
    }
}
