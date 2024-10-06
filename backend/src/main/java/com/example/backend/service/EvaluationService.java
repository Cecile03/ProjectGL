package com.example.backend.service;

import com.example.backend.dao.EvaluationDao;
import com.example.backend.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service de gestion des évaluations.
 */
@Service
public class EvaluationService {

    private final EvaluationDao evaluationDao;

    /**
     * Constructeur de la classe EvaluationService.
     *
     * @param evaluationDao Le DAO des évaluations.
     */
    @Autowired
    public EvaluationService(EvaluationDao evaluationDao) {
        this.evaluationDao = evaluationDao;
    }

    /**
     * Ajoute un évaluateur à une sous-note.
     *
     * @param subGrade La sous-note.
     * @param evaluator L'évaluateur.
     * @return L'évaluation créée.
     */
    public Evaluation addEvaluator(SubGrade subGrade, User evaluator) {
        if(evaluationDao.findByEvaluatorIdAndSubGradeId(evaluator.getId(), subGrade.getId()).isPresent()) {
            return evaluationDao.findByEvaluatorIdAndSubGradeId(evaluator.getId(), subGrade.getId()).orElseThrow();
        }
        Evaluation evaluation = new Evaluation();
        evaluation.setEvaluator(evaluator);
        evaluation.setSubGrade(subGrade);
        subGrade.getEvaluations().add(evaluation);
        saveEvaluation(evaluation);
        updateStatus(subGrade);
        return evaluation;
    }

    /**
     * Met à jour une évaluation.
     *
     * @param evaluation L'évaluation à mettre à jour.
     * @param value La nouvelle valeur de l'évaluation.
     * @return L'évaluation mise à jour.
     */
    public Evaluation updateEvaluation(Evaluation evaluation, Double value) {
        try {
            evaluation.setValue(value == null ? null : Math.round(value * 100.0) / 100.0);
            evaluationDao.save(evaluation);
            updateStatus(evaluation.getSubGrade());
            return evaluation;
        } catch (Exception e){
            throw new IllegalArgumentException("The evaluation does not exist");
        }
    }

    /**
     * Sauvegarde une évaluation.
     *
     * @param evaluation L'évaluation à sauvegarder.
     */
    public void saveEvaluation(Evaluation evaluation) {
        evaluationDao.save(evaluation);
    }

    /**
     * Met à jour le statut d'une sous-note.
     *
     * @param subGrade La sous-note.
     */
    private void updateStatus(SubGrade subGrade) {
        if (allEvaluationsCompleted(subGrade)) {
            subGrade.setStatus(EvaluationStatus.COMPLETED);
        } else {
            subGrade.setStatus(EvaluationStatus.IN_PROGRESS);
        }
    }

    /**
     * Vérifie si toutes les évaluations d'une sous-note sont terminées.
     *
     * @param subGrade La sous-note.
     * @return Vrai si toutes les évaluations sont terminées, faux sinon.
     */
    private boolean allEvaluationsCompleted(SubGrade subGrade) {
        return subGrade.getEvaluations().stream().allMatch(evaluation -> evaluation.getStatus() == EvaluationStatus.COMPLETED);
    }

    /**
     * Récupère une évaluation par l'identifiant de la sous-note et l'identifiant de l'évaluateur.
     *
     * @param subGradeId L'identifiant de la sous-note.
     * @param evaluatorId L'identifiant de l'évaluateur.
     * @return L'évaluation trouvée.
     */
    public Evaluation getEvaluationBySubGradeIdAndEvaluatorId(int subGradeId, int evaluatorId) {
        return evaluationDao.findByEvaluatorIdAndSubGradeId(evaluatorId, subGradeId).orElseThrow();
    }

    /**
     * Récupère une évaluation par son identifiant.
     *
     * @param evaluationId L'identifiant de l'évaluation.
     * @return L'évaluation trouvée.
     */
    public Evaluation getEvaluationById(int evaluationId) {
        return evaluationDao.findById(evaluationId).orElseThrow();
    }
}