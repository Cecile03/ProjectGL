package com.example.backend.controller;

import com.example.backend.model.TeamGradeFromStudent;
import com.example.backend.service.TeamGradeFromStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Le contrôleur TeamGradeFromStudent.
 * Gère les requêtes HTTP liées aux opérations de notation d'équipe par les étudiants.
 */
@RestController
@RequestMapping("/teamGradeFromStudent")
public class TeamGradeFromStudentController {

    private final TeamGradeFromStudentService teamGradeFromStudentService;

    /**
     * Constructeur pour l'injection de dépendances.
     *
     * @param teamGradeFromStudentService Le service de notation d'équipe par les étudiants à injecter.
     */
    @Autowired
    public TeamGradeFromStudentController(TeamGradeFromStudentService teamGradeFromStudentService) {
        this.teamGradeFromStudentService = teamGradeFromStudentService;
    }

    /**
     * Gère les requêtes PUT pour sauvegarder une note d'équipe par un étudiant.
     *
     * @param teamNotingId L'identifiant de l'équipe notée.
     * @param teamToNoteId L'identifiant de l'équipe à noter.
     * @param stringId L'identifiant du sprint.
     * @param grade La note à sauvegarder.
     * @return Une réponse indiquant si l'opération a réussi.
     */
    @PutMapping("/from/{teamNotingId}/to/{teamToNoteId}/for/{sprintId}/grade/{grade}")
    public ResponseEntity<Void> saveTeamGradeFromStudent(@PathVariable("teamNotingId") int teamNotingId, @PathVariable("teamToNoteId") int teamToNoteId, @PathVariable("sprintId") int stringId, @PathVariable("grade") int grade) {
        try {
            teamGradeFromStudentService.updateTeamGradeFromStudent(teamNotingId, teamToNoteId, stringId, grade);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    /**
     * Gère les requêtes GET pour récupérer une note d'équipe par un étudiant.
     *
     * @param teamNotingId L'identifiant de l'équipe notée.
     * @param teamToNoteId L'identifiant de l'équipe à noter.
     * @param stringId L'identifiant du sprint.
     * @return Une réponse contenant la note d'équipe spécifiée.
     */
    @GetMapping("/from/{teamNotingId}/to/{teamToNoteId}/for/{sprintId}")
    public ResponseEntity<TeamGradeFromStudent> getTeamGradeFromStudent(@PathVariable("teamNotingId") int teamNotingId, @PathVariable("teamToNoteId") int teamToNoteId, @PathVariable("sprintId") int stringId) {
        try {
            return ResponseEntity.ok(teamGradeFromStudentService.getTeamGradeFromStudent(teamNotingId, teamToNoteId, stringId));
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }
}
