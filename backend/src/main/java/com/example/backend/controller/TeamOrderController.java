package com.example.backend.controller;

import com.example.backend.model.TeamOrder;
import com.example.backend.service.TeamOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Le contrôleur TeamOrder.
 * Gère les requêtes HTTP liées aux opérations d'ordre d'équipe.
 */
@RestController
@RequestMapping("/teamOrder")
public class TeamOrderController {

    private final TeamOrderService teamOrderService;

    /**
     * Constructeur pour l'injection de dépendances.
     *
     * @param teamOrderService Le service d'ordre d'équipe à injecter.
     */
    @Autowired
    public TeamOrderController(TeamOrderService teamOrderService) {
        this.teamOrderService = teamOrderService;
    }

    /**
     * Gère les requêtes PUT pour mettre à jour l'ordre d'une équipe.
     *
     * @param teamId L'identifiant de l'équipe.
     * @param sprintId L'identifiant du sprint.
     * @param userIds La liste des identifiants des utilisateurs.
     * @return Une réponse indiquant si l'opération a réussi.
     */
    @PutMapping("/for/{teamId}/during/{sprintId}")
    public ResponseEntity<Void> updateTeamOrder(@PathVariable("teamId") int teamId, @PathVariable("sprintId") int sprintId, @RequestBody List<Integer> userIds) {
        try {
            teamOrderService.updateTeamOrder(teamId, sprintId, userIds);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    /**
     * Gère les requêtes GET pour obtenir l'ordre d'une équipe.
     *
     * @param teamId L'identifiant de l'équipe.
     * @param sprintId L'identifiant du sprint.
     * @return L'ordre de l'équipe.
     */
    @GetMapping("/for/{teamId}/during/{sprintId}")
    public ResponseEntity<TeamOrder> getTeamOrder(@PathVariable("teamId") int teamId, @PathVariable("sprintId") int sprintId) {
        try {
            return ResponseEntity.ok(teamOrderService.getTeamOrder(teamId, sprintId));
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }
}
