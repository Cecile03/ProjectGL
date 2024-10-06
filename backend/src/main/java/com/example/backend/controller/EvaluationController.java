package com.example.backend.controller;

import com.example.backend.model.Evaluation;
import com.example.backend.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Le contrôleur Evaluation.
 * Gère les requêtes HTTP liées aux opérations d'évaluation.
 */
@RestController
@RequestMapping("/evaluation")
public class EvaluationController {

    private final EvaluationService evaluationService;

    /**
     * Constructeur pour l'injection de dépendances.
     *
     * @param evaluationService Le service d'évaluation à injecter.
     */
    @Autowired
    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    /**
     * Gère les requêtes PUT pour mettre à jour une sous-note.
     *
     * @param evaluationId L'identifiant de l'évaluation à mettre à jour.
     * @param value La nouvelle valeur de la sous-note.
     * @return Une réponse contenant l'évaluation mise à jour.
     */
    @PutMapping("/update/{evaluationId}/{value}")
    public ResponseEntity<Evaluation> updateSubGrade(@PathVariable("evaluationId") int evaluationId, @PathVariable Double value) {
        try{
            Evaluation evaluation = evaluationService.getEvaluationById(evaluationId);
            return ResponseEntity.ok(evaluationService.updateEvaluation(evaluation, value));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
