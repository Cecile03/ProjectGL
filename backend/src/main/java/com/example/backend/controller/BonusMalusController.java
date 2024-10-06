package com.example.backend.controller;

import com.example.backend.dto.BonusMalusDTO;
import com.example.backend.model.BonusMalus;
import com.example.backend.service.BonusMalusService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Le contrôleur BonusMalus.
 * Gère les requêtes HTTP liées aux opérations de bonus et de malus.
 */
@RestController
@RequestMapping("/bonusMalus")
public class BonusMalusController {

    private final BonusMalusService bonusMalusService;

    /**
     * Constructeur pour l'injection de dépendances.
     *
     * @param bonusMalusService Le service de bonus et de malus à injecter.
     */
    @Autowired
    public BonusMalusController(BonusMalusService bonusMalusService) {
        this.bonusMalusService = bonusMalusService;
    }

    /**
     * Gère les requêtes GET pour récupérer tous les bonus et malus de chaque équipe.
     *
     * @param sprintId L'identifiant du sprint.
     * @return Une réponse contenant la liste de tous les bonus et malus.
     */
    @GetMapping
    public ResponseEntity<List<List<BonusMalusDTO>>> getAllBonusMalus(@RequestParam int sprintId) {
        try{
            return ResponseEntity.ok(bonusMalusService.getAllBonusMalus(sprintId));
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }


    /**
     * Gère les requêtes GET pour récupérer les bonus/malus illimités d'une équipe spécifique.
     *
     * @param teamId L'identifiant de l'équipe.
     * @param sprintId L'identifiant du sprint.
     * @return Une réponse contenant la liste des bonus/malus illimités.
     */
    @GetMapping("/{teamId}/unlimited")
    @PreAuthorize("hasAnyAuthority('SS')")
    public ResponseEntity<List<BonusMalusDTO>> getTeamUBM(@PathVariable int teamId, @RequestParam int sprintId) {
        try{
            return ResponseEntity.ok(bonusMalusService.getUBmByTeamAndSprint(teamId, sprintId));
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    /**
     * Gère les requêtes GET pour récupérer les bonus/malus limités d'une équipe spécifique.
     *
     * @param teamId L'identifiant de l'équipe.
     * @param sprintId L'identifiant du sprint.
     * @return Une réponse contenant la liste des bonus/malus limités.
     */
    @GetMapping("/{teamId}/limited")
    public ResponseEntity<List<BonusMalusDTO>> getTeamLBM(@PathVariable int teamId, @RequestParam int sprintId) {
        try{
            return ResponseEntity.ok(bonusMalusService.getLBmByTeamAndSprint(teamId, sprintId));
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }


    /**
     * Gère les requêtes POST pour ajouter des bonus/malus à une équipe spécifique.
     *
     * @param teamId L'identifiant de l'équipe.
     * @param sprintId L'identifiant du sprint.
     * @param bonusMalusList La liste des bonus/malus à ajouter.
     * @return Une réponse contenant la liste des bonus/malus ajoutés.
     */
    @PostMapping("/{teamId}")
    public ResponseEntity<List<BonusMalusDTO>> addBonusMalus(@PathVariable int teamId, @RequestParam int sprintId, @RequestBody List<BonusMalusDTO> bonusMalusList) {
        try{
            return ResponseEntity.ok(bonusMalusService.addBonusMalusByTeam(teamId, sprintId, bonusMalusList));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    /**
     * Gère les requêtes POST pour valider les bonus/malus d'une équipe spécifique.
     *
     * @param teamId L'identifiant de l'équipe.
     * @param sprintId L'identifiant du sprint.
     * @return Une réponse indiquant si l'opération a réussi.
     */
    @PostMapping("/{teamId}/validate")
    public ResponseEntity<Void> validateTeamBM(@PathVariable int teamId, @RequestParam int sprintId) {
        try{
            bonusMalusService.validateTeamBM(teamId, sprintId);
            return ResponseEntity.status(200).build();
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    /**
     * Gère les requêtes GET pour récupérer les membres qui ont validé les bonus/malus.
     *
     * @param teamId L'identifiant de l'équipe.
     * @param sprintId L'identifiant du sprint.
     * @return Une réponse contenant la liste des membres qui ont validé les bonus/malus.
     */
    @GetMapping("/{teamId}/validate")
    public ResponseEntity<List<Integer>> getMembersWhoValidateBM(@PathVariable int teamId, @RequestParam int sprintId) {
        try{
           return ResponseEntity.ok(bonusMalusService.getMembersWhoValidateBM(teamId, sprintId));
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    /**
     * Gère les requêtes GET pour récupérer les bonus/malus d'un utilisateur pour un sprint spécifique par un étudiant.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @param sprintId L'identifiant du sprint.
     * @return Une réponse contenant les bonus/malus de l'utilisateur.
     */
    @GetMapping("/student")
    public ResponseEntity<BonusMalus> getUserBMForSprintByStudent(@RequestParam int userId, @RequestParam int sprintId) {
        try{
           return ResponseEntity.ok(bonusMalusService.getBonusMalusStudentByTeamIdAndSprintIdValidated(userId, sprintId));
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    /**
     * Gère les requêtes GET pour récupérer les bonus/malus d'un utilisateur pour un sprint spécifique par un enseignant.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @param sprintId L'identifiant du sprint.
     * @return Une réponse contenant les bonus/malus de l'utilisateur.
     */
    @GetMapping("/teacher")
    public ResponseEntity<BonusMalus> getUserBMForSprintBySS(@RequestParam int userId, @RequestParam int sprintId) {
        try{
            return ResponseEntity.ok(bonusMalusService.getBonusMalusSSByTeamIdAndSprintIdValidated(userId, sprintId));
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

}
