package com.example.backend.controller;

import com.example.backend.dao.GradeTypesDao;
import com.example.backend.model.Evaluation;
import com.example.backend.model.GradeTypes;
import com.example.backend.model.SubGrade;
import com.example.backend.service.SubGradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Le contrôleur SubGrade.
 * Gère les requêtes HTTP liées aux opérations de sous-notes.
 */
@RestController
@RequestMapping("/subgrade")
public class SubGradeController {

    private final SubGradeService subGradeService;
    private final GradeTypesDao gradeTypesDao;

    /**
     * Constructeur pour l'injection de dépendances.
     *
     * @param subGradeService Le service de sous-notes à injecter.
     * @param gradeTypesDao Le DAO des types de notes à injecter.
     */
    @Autowired
    public SubGradeController(SubGradeService subGradeService, GradeTypesDao gradeTypesDao) {
        this.subGradeService = subGradeService;
        this.gradeTypesDao = gradeTypesDao;
    }

    /**
     * Gère les requêtes PUT pour mettre à jour une sous-note.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @param sprintId L'identifiant du sprint.
     * @param gradeTypeName Le nom du type de note.
     * @return Une réponse indiquant si l'opération a réussi.
     */
    @PutMapping("/update")
    public ResponseEntity<Void> updateSubGrade(@RequestParam("userId") int userId, @RequestParam("sprintId") int sprintId, @RequestParam("gradeType") String gradeTypeName) {
        try{
            subGradeService.updateSubGrade(userId, sprintId, gradeTypesDao.findById(GradeTypes.GradeTypesEnum.valueOf(gradeTypeName).getId()).orElseThrow());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Gère les requêtes GET pour récupérer une sous-note par ID utilisateur, ID sprint et type de note.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @param sprintId L'identifiant du sprint.
     * @param gradeTypeName Le nom du type de note.
     * @return Une réponse contenant la sous-note spécifiée.
     */
    @GetMapping()
    public ResponseEntity<SubGrade> getSubGradeByUserIdAndSprintIdAndGradeType(@RequestParam("userId") int userId, @RequestParam("sprintId") int sprintId, @RequestParam("gradeType") String gradeTypeName) {
        try{
            return ResponseEntity.ok(subGradeService.getSubGradeByUserIdAndSprintIdAndGradeType(userId, sprintId, gradeTypesDao.findById(GradeTypes.GradeTypesEnum.valueOf(gradeTypeName).getId()).orElseThrow()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Gère les requêtes GET pour récupérer une évaluation par sous-note.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @param sprintId L'identifiant du sprint.
     * @param gradeTypeName Le nom du type de note.
     * @param evaluatorId L'identifiant de l'évaluateur.
     * @return Une réponse contenant l'évaluation spécifiée.
     */
    @GetMapping("/evaluation")
    public ResponseEntity<Evaluation> getEvaluationBySubgrade(@RequestParam("userId") int userId, @RequestParam("sprintId") int sprintId, @RequestParam("gradeType") String gradeTypeName, @RequestParam("evaluatorId") int evaluatorId) {
        try{
            SubGrade sg = subGradeService.getSubGradeByUserIdAndSprintIdAndGradeType(userId, sprintId, gradeTypesDao.findById(GradeTypes.GradeTypesEnum.valueOf(gradeTypeName).getId()).orElseThrow());
            return ResponseEntity.ok(subGradeService.getEvaluationBySubgrade(sg.getId(), evaluatorId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
