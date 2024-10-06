package com.example.backend.controller;

import com.example.backend.dto.SprintDTO;
import com.example.backend.model.Sprint;
import com.example.backend.service.SprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Le contrôleur Sprint.
 * Gère les requêtes HTTP liées aux opérations de sprint.
 */
@RestController
public class SprintController {

    private final SprintService sprintService;

    /**
     * Constructeur pour l'injection de dépendances.
     *
     * @param sprintService Le service de sprint à injecter.
     */
    @Autowired
    public SprintController(SprintService sprintService) {
        this.sprintService = sprintService;
    }

    /**
     * Gère les requêtes POST pour créer un sprint.
     *
     * @param sprintDTO Le DTO du sprint à créer.
     * @return Une réponse indiquant si l'opération a réussi.
     */
    @PostMapping("/sprint")
    @PreAuthorize("hasAnyAuthority('OL', 'PL')")
    public ResponseEntity<Void> createSprint(@RequestBody SprintDTO sprintDTO) {
        try {
            sprintService.saveSprint(sprintService.fromSprintDTO(sprintDTO));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Gère les requêtes PUT pour mettre à jour un sprint.
     *
     * @param sprintDTO Le DTO du sprint à mettre à jour.
     * @return Une réponse indiquant si l'opération a réussi.
     */
    @PutMapping("/sprint")
    @PreAuthorize("hasAnyAuthority('OL', 'PL')")
    public ResponseEntity<Void> update(@RequestBody SprintDTO sprintDTO) {
        try {
            sprintService.update(sprintService.fromSprintDTO(sprintDTO));
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Gère les requêtes GET pour récupérer un sprint.
     *
     * @return Une réponse contenant le sprint récupéré.
     */
    @GetMapping("/sprint")
    public ResponseEntity<List<Sprint>> getSprint() {
        try{
            return ResponseEntity.status(HttpStatus.OK).body(sprintService.getAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
   }

    /**
     * Gère les requêtes DELETE pour supprimer tous les sprints.
     *
     * @return Une réponse indiquant si l'opération a réussi.
     */
    @DeleteMapping("/sprint")
    @PreAuthorize("hasAnyAuthority('OL', 'PL')")
    public ResponseEntity<Void> deleteAllSprints() {
        try{
            sprintService.deleteAll();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Gère les requêtes GET pour récupérer un sprint par ID.
     *
     * @param id L'identifiant du sprint à récupérer.
     * @return Une réponse contenant le sprint spécifié.
     */
    @GetMapping("/sprint/{id}")
    public ResponseEntity<Sprint> getSprintById(@PathVariable int id) {
        try{
            return ResponseEntity.ok(sprintService.getSprintById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
