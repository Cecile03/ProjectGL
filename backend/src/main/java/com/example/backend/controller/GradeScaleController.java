package com.example.backend.controller;

import com.example.backend.dto.GradeScaleDTO;
import com.example.backend.model.GradeScale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.backend.service.GradeScaleService;

import java.util.List;

/**
 * Le contrôleur GradeScale.
 * Gère les requêtes HTTP liées aux opérations d'échelle de notes.
 */
@RestController
public class GradeScaleController {

    private final GradeScaleService gradeScaleService;

    /**
     * Constructeur pour l'injection de dépendances.
     *
     * @param gradeScaleService Le service d'échelle de notes à injecter.
     */
    @Autowired
    public GradeScaleController(GradeScaleService gradeScaleService) {
        this.gradeScaleService = gradeScaleService;
    }

    /**
     * Gère les requêtes POST pour créer des échelles de notes complètes.
     *
     * @param gradeScalesDTO La liste des DTO d'échelle de notes à créer.
     * @return Une réponse indiquant si l'opération a réussi.
     */
    @PostMapping("/gradeScale")
    @PreAuthorize("hasAnyAuthority('OL','PL')")
    public ResponseEntity<Void> createFullGradeScales(@RequestBody List<GradeScaleDTO> gradeScalesDTO) {
        try {
            gradeScaleService.createFullGradeScales(gradeScalesDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    /**
     * Gère les requêtes PUT pour mettre à jour une échelle de notes spécifique.
     *
     * @param id L'identifiant de l'échelle de notes à mettre à jour.
     * @param newGradeScaleData Les nouvelles données de l'échelle de notes.
     * @return Une réponse indiquant si l'opération a réussi.
     */
    @PutMapping("/gradeScale/{id}")
    @PreAuthorize("hasAnyAuthority('OL','PL')")
    public ResponseEntity<Void> update(@PathVariable int id, @RequestBody GradeScale newGradeScaleData) {
        try {
            gradeScaleService.update(id, newGradeScaleData);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Gère les requêtes DELETE pour supprimer une échelle de notes spécifique.
     *
     * @param id L'identifiant de l'échelle de notes à supprimer.
     * @return Une réponse indiquant si l'opération a réussi.
     */
    @DeleteMapping("/gradeScale/{id}")
    @PreAuthorize("hasAnyAuthority('OL','PL')")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        try {
            gradeScaleService.delete(id);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Gère les requêtes GET pour récupérer une échelle de notes spécifique.
     *
     * @param id L'identifiant de l'échelle de notes à récupérer.
     * @return Une réponse contenant l'échelle de notes spécifiée.
     */
    @GetMapping("/gradeScale/{id}")
    public ResponseEntity<GradeScale> getGradeScaleById(@PathVariable int id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(gradeScaleService.getGradeScaleById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Gère les requêtes GET pour récupérer toutes les échelles de notes.
     *
     * @return Une réponse contenant la liste de toutes les échelles de notes.
     */
    @GetMapping("/gradeScale")
    public ResponseEntity<List<GradeScale>> getGradeScale() {
        return ResponseEntity.status(HttpStatus.OK).body(gradeScaleService.getAll());
    }

    /**
     * Gère les requêtes DELETE pour supprimer toutes les échelles de notes.
     *
     * @return Une réponse indiquant si l'opération a réussi.
     */
    @DeleteMapping("/gradeScale")
    @PreAuthorize("hasAnyAuthority('OL', 'PL')")
    public ResponseEntity<Void> deleteAll() {
        try {
            gradeScaleService.deleteAll();
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
