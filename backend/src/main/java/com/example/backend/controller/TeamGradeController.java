package com.example.backend.controller;

import com.example.backend.model.TeamGrade;
import com.example.backend.service.TeamGradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Le contrôleur TeamGrade.
 * Gère les requêtes HTTP liées aux opérations de notation d'équipe.
 */
@RestController
@RequestMapping("/teamGrade")
@PreAuthorize("hasAnyAuthority('SS','TC','OL','PL')")
public class TeamGradeController {

    private final TeamGradeService teamGradeService;

    /**
     * Constructeur pour l'injection de dépendances.
     *
     * @param teamGradeService Le service de notation d'équipe à injecter.
     */
    @Autowired
    public TeamGradeController(TeamGradeService teamGradeService) {
        this.teamGradeService = teamGradeService;
    }

    /**
     * Gère les requêtes PUT pour sauvegarder une note d'équipe.
     *
     * @param teamId L'identifiant de l'équipe.
     * @param sprintId L'identifiant du sprint.
     * @param detailId L'identifiant du détail.
     * @param evaluatorId L'identifiant de l'évaluateur.
     * @param grade La note à sauvegarder.
     * @return Une réponse indiquant si l'opération a réussi.
     */
    @PutMapping()
    public ResponseEntity<Void> saveTeamGrade(@RequestParam("teamId") int teamId, @RequestParam("sprintId") int sprintId, @RequestParam("detailId") int detailId, @RequestParam("evaluatorId") int evaluatorId, @RequestParam("grade") Double grade) {
        try {
            teamGradeService.updateTeamGrade(teamId, sprintId, detailId, evaluatorId, grade);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    /**
     * Gère les requêtes GET pour récupérer une note d'équipe.
     *
     * @param teamId L'identifiant de l'équipe.
     * @param detailId L'identifiant du détail.
     * @param sprintId L'identifiant du sprint.
     * @param evaluatorId L'identifiant de l'évaluateur.
     * @return Une réponse contenant la note d'équipe spécifiée.
     */
    @GetMapping()
    public ResponseEntity<TeamGrade> getTeamGrade(@RequestParam("teamId") int teamId, @RequestParam("detailId") int detailId, @RequestParam("sprintId") int sprintId, @RequestParam("evaluatorId") int evaluatorId) {
        try {
            return ResponseEntity.ok(teamGradeService.getTeamGrade(teamId, sprintId, detailId, evaluatorId));
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }
}
