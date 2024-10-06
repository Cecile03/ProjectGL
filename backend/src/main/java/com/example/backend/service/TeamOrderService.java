package com.example.backend.service;

import com.example.backend.dao.TeamOrderDao;
import com.example.backend.model.TeamOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service de gestion des ordres d'équipe.
 */
@Service
public class TeamOrderService {

    private final TeamOrderDao teamOrderDao;
    private final TeamService teamService;
    private final SprintService sprintService;
    private final UserService userService;

    /**
     * Constructeur de la classe TeamOrderService.
     *
     * @param teamOrderDao Le DAO des ordres d'équipe.
     * @param teamService Le service de gestion des équipes.
     * @param sprintService Le service de gestion des sprints.
     * @param userService Le service de gestion des utilisateurs.
     */
    @Autowired
    public TeamOrderService(TeamOrderDao teamOrderDao, TeamService teamService, SprintService sprintService, UserService userService) {
        this.teamOrderDao = teamOrderDao;
        this.teamService = teamService;
        this.sprintService = sprintService;
        this.userService = userService;
    }

    /**
     * Enregistre un ordre d'équipe.
     *
     * @param teamId L'identifiant de l'équipe.
     * @param sprintId L'identifiant du sprint.
     * @param userIds Les identifiants des utilisateurs.
     */
    public void saveTeamOrder(int teamId, int sprintId, List<Integer> userIds) {
        TeamOrder teamOrder = new TeamOrder();
        teamOrder.setTeam(teamService.getTeamById(teamId));
        teamOrder.setSprint(sprintService.getSprintById(sprintId));
        teamOrder.clean();
        for (Integer userId : userIds) {
            teamOrder.addAfterOrder(userService.getUserById(userId));
        }
        teamOrderDao.save(teamOrder);
    }

    /**
     * Met à jour un ordre d'équipe.
     *
     * @param teamId L'identifiant de l'équipe.
     * @param sprintId L'identifiant du sprint.
     * @param userIds Les identifiants des utilisateurs.
     */
    public void updateTeamOrder(int teamId, int sprintId, List<Integer> userIds) {
        try{
            TeamOrder teamOrder = teamOrderDao.findByTeamIdAndSprintId(teamId, sprintId).orElseThrow();
            teamOrder.clean();
            for (Integer userId : userIds) {
                teamOrder.addAfterOrder(userService.getUserById(userId));
            }
            teamOrderDao.save(teamOrder);
        } catch (Exception e) {
            saveTeamOrder(teamId, sprintId, userIds);
        }
    }

    /**
     * Récupère un ordre d'équipe.
     *
     * @param teamId L'identifiant de l'équipe.
     * @param sprintId L'identifiant du sprint.
     * @return L'ordre d'équipe.
     */
    public TeamOrder getTeamOrder(int teamId, int sprintId) {
        return teamOrderDao.findByTeamIdAndSprintId(teamId, sprintId).orElse(null);
    }
}
