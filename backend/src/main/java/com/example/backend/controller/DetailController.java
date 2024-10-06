package com.example.backend.controller;

import com.example.backend.model.Detail;
import com.example.backend.service.DetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Le contrôleur Detail.
 * Gère les requêtes HTTP liées aux opérations de détail.
 */
@RestController
public class DetailController {

    private final DetailService detailService;

    /**
     * Constructeur pour l'injection de dépendances.
     *
     * @param detailService Le service de détail à injecter.
     */
    @Autowired
    public DetailController(DetailService detailService) {
        this.detailService = detailService;
    }

    /**
     * Gère les requêtes POST pour ajouter un détail.
     *
     * @param detail Le détail à ajouter.
     * @return Une réponse contenant le détail ajouté.
     */
    @PostMapping("/details")
    @PreAuthorize("hasAnyAuthority('OL', 'PL')")
    public ResponseEntity<Detail> add(@RequestBody Detail detail) {
        try {
            detailService.createDetail(detail);
            return new ResponseEntity<>(detail, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Gère les requêtes PUT pour mettre à jour un détail spécifique.
     *
     * @param id L'identifiant du détail à mettre à jour.
     * @param newDetailData Les nouvelles données du détail.
     * @return Une réponse contenant le détail mis à jour.
     */
    @PutMapping("/details/{id}")
    @PreAuthorize("hasAnyAuthority('OL', 'PL')")
    public ResponseEntity<Detail> update(@PathVariable int id, @RequestBody Detail newDetailData) {
        try {
            Detail updatedetail = detailService.update(id, newDetailData);
            return ResponseEntity.ok(updatedetail);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Gère les requêtes DELETE pour supprimer un détail spécifique.
     *
     * @param id L'identifiant du détail à supprimer.
     * @return Une réponse indiquant si l'opération a réussi.
     */
    @DeleteMapping("/details/{id}")
    @PreAuthorize("hasAnyAuthority('OL', 'PL')")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        try {
            detailService.delete(id);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Gère les requêtes GET pour récupérer un détail spécifique.
     *
     * @param id L'identifiant du détail à récupérer.
     * @return Une réponse contenant le détail spécifié.
     */
    @GetMapping("/details/{id}")
    public ResponseEntity<Detail> getDetailById(@PathVariable int id) {
        try {
            Detail detail = detailService.getDetailById(id);
            return new ResponseEntity<>(detail, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Gère les requêtes GET pour récupérer tous les détails.
     *
     * @return Une réponse contenant la liste de tous les détails.
     */
    @GetMapping("/details")
    public ResponseEntity<List<Detail>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(detailService.getAll());
    }

    /**
     * Gère les requêtes GET pour récupérer les détails par identifiant de catégorie.
     *
     * @param categoryId L'identifiant de la catégorie.
     * @return Une réponse contenant la liste des détails.
     */
    @GetMapping("/details/{categoryId}/category")
    public ResponseEntity<List<Detail>> getDetailsByCategoryId(@PathVariable int categoryId) {
        return ResponseEntity.status(HttpStatus.OK).body(detailService.getDetailsByCategoryId(categoryId));
    }

    /**
     * Gère les requêtes DELETE pour supprimer tous les détails.
     *
     * @return Une réponse indiquant si l'opération a réussi.
     */
    @DeleteMapping("/details")
    @PreAuthorize("hasAnyAuthority('OL', 'PL')")
    public ResponseEntity<Void> deleteAll() {
        try {
            detailService.deleteAll();
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
