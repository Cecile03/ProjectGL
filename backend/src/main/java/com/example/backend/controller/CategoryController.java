package com.example.backend.controller;

import com.example.backend.model.Category;
import com.example.backend.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Le contrôleur Category.
 * Gère les requêtes HTTP liées aux opérations de catégorie.
 */
@RestController
@RequestMapping()
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Constructeur pour l'injection de dépendances.
     *
     * @param categoryService Le service de catégorie à injecter.
     */
    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Gère les requêtes GET pour récupérer une catégorie spécifique.
     *
     * @param id L'identifiant de la catégorie à récupérer.
     * @return La catégorie spécifiée.
     */
    @GetMapping("/categories/{id}")
    public Category getCategoryById(@PathVariable int id) {
        return categoryService.getCategoryById(id);
    }

    /**
     * Gère les requêtes POST pour créer une nouvelle catégorie.
     *
     * @param category La catégorie à créer.
     * @return Une réponse indiquant si l'opération a réussi.
     */
    @PostMapping("/categories")
    @PreAuthorize("hasAnyAuthority('OL', 'PL')")
    public ResponseEntity<Void> createCategory(@RequestBody Category category) {
        try {
            categoryService.createCategory(category);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    /**
     * Gère les requêtes PUT pour mettre à jour une catégorie spécifique.
     *
     * @param id L'identifiant de la catégorie à mettre à jour.
     * @param newCategoryData Les nouvelles données de la catégorie.
     * @return Une réponse indiquant si l'opération a réussi.
     */
    @PutMapping("/categories/{id}")
    @PreAuthorize("hasAnyAuthority('OL', 'PL')")
    public ResponseEntity<Void> update(@PathVariable int id, @RequestBody Category newCategoryData) {
        try {
            categoryService.update(id, newCategoryData);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Gère les requêtes DELETE pour supprimer une catégorie spécifique.
     *
     * @param id L'identifiant de la catégorie à supprimer.
     * @return Une réponse indiquant si l'opération a réussi.
     */
    @DeleteMapping("/categories/{id}")
    @PreAuthorize("hasAnyAuthority('OL', 'PL')")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        try {
            categoryService.delete(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gère les requêtes GET pour récupérer toutes les catégories.
     *
     * @return Une réponse contenant la liste de toutes les catégories.
     */
    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        try {
            List<Category> categories = categoryService.getAll();
            return new ResponseEntity<>(categories, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gère les requêtes GET pour récupérer les catégories par identifiant d'échelle de notes.
     *
     * @param gradeScaleId L'identifiant de l'échelle de notes.
     * @return Une réponse contenant la liste des catégories.
     */
    @GetMapping("/categories/{gradeScaleId}/gradeScale")
    public ResponseEntity<List<Category>> getCategoriesByGradeScaleId(@PathVariable int gradeScaleId) {
        try {
            List<Category> categories = categoryService.getCategoriesByGradeScaleId(gradeScaleId);
            return new ResponseEntity<>(categories, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gère les requêtes DELETE pour supprimer toutes les catégories.
     *
     * @return Une réponse indiquant si l'opération a réussi.
     */
    @DeleteMapping("/categories")
    @PreAuthorize("hasAnyAuthority('OL', 'PL')")
    public ResponseEntity<Void> deleteAll() {
        try {
            categoryService.deleteAll();
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
