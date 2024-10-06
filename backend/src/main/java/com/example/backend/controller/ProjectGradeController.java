package com.example.backend.controller;

import com.example.backend.model.ProjectGrade;
import com.example.backend.service.ProjectGradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Le contrôleur ProjectGrade.
 * Gère les requêtes HTTP liées aux opérations de notation de projet.
 */
@RestController
@RequestMapping("/projectGrade")
public class ProjectGradeController {

    private final ProjectGradeService projectGradeService;

    /**
     * Constructeur pour l'injection de dépendances.
     *
     * @param projectGradeService Le service de notation de projet à injecter.
     */
    @Autowired
    public ProjectGradeController(ProjectGradeService projectGradeService) {
        this.projectGradeService = projectGradeService;
    }

    /**
     * Gère les requêtes POST pour sauvegarder une note de projet.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @param sprintId L'identifiant du sprint.
     * @return Une réponse contenant la note de projet sauvegardée.
     */
    @PostMapping("/{userId}/{sprintId}")
    @PreAuthorize("hasAnyAuthority('SS','OL','PL','OS','TC')")
    public ResponseEntity<ProjectGrade> saveProjectGrade(@PathVariable int userId, @PathVariable int sprintId) {
        try{
            return ResponseEntity.ok(projectGradeService.saveProjectGrade(userId, sprintId));
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Gère les requêtes PUT pour mettre à jour une note de projet.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @param sprintId L'identifiant du sprint.
     * @return Une réponse contenant la note de projet mise à jour.
     */
    @PutMapping("/{userId}/{sprintId}")
    @PreAuthorize("hasAnyAuthority('SS','OL','PL','OS','TC')")
    public ResponseEntity<ProjectGrade> updateProjectGrade(@PathVariable int userId, @PathVariable int sprintId) {
        try{
            return ResponseEntity.ok(projectGradeService.updateProjectGrade(userId, sprintId));
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Gère les requêtes GET pour récupérer une note de projet par ID utilisateur et ID sprint.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @param sprintId L'identifiant du sprint.
     * @return Une réponse contenant la note de projet spécifiée.
     */
    @GetMapping("/{userId}/{sprintId}")
    @PreAuthorize("hasAnyAuthority('SS','OL','PL','OS','TC')")
    public ResponseEntity<ProjectGrade> getProjectGradeByUserIdAndSprintId(@PathVariable int userId, @PathVariable int sprintId) {
        try{
            return ResponseEntity.ok(projectGradeService.getProjectGradeByUserIdAndSprintId(userId, sprintId));
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Gère les requêtes GET pour récupérer toutes les notes de projet.
     *
     * @return Une réponse indiquant si l'opération a réussi.
     */
    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('SS','OL','PL','OS','TC')")
    public ResponseEntity<Void> getAll() {
        try{
            projectGradeService.updateAllProjectGrades();
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Gère les requêtes PUT pour valider une note de projet.
     *
     * @param id L'identifiant de la note de projet à valider.
     * @return Une réponse indiquant si l'opération a réussi.
     */
    @PutMapping("/validate/{id}")
    @PreAuthorize("hasAnyAuthority('SS','OL','PL')")
    public ResponseEntity<Void> validateProjectGrade(@PathVariable int id) {
        try{
            projectGradeService.validateProjectGrade(id);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }
}