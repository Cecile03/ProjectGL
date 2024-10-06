package com.example.backend.controller;

import com.example.backend.dto.PostDTO;
import com.example.backend.model.Feedback;
import com.example.backend.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Le contrôleur Feedback.
 * Gère les requêtes HTTP liées aux opérations de feedback.
 */
@RestController
public class FeedbackController {

    private final FeedbackService feedbackService;

    /**
     * Constructeur pour l'injection de dépendances.
     *
     * @param feedbackService Le service de feedback à injecter.
     */
    @Autowired
    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    /**
     * Gère les requêtes PUT pour sauvegarder un feedback.
     *
     * @param postDTO Le DTO du post à sauvegarder.
     * @return Une réponse indiquant si l'opération a réussi.
     */
    //Pour tous les TC, SS, OL, PL
    @PutMapping("/feedbacks")
    @PreAuthorize("hasAnyAuthority('TC', 'SS', 'OL', 'PL')")
    public ResponseEntity<Void> saveFeedback(@RequestBody PostDTO postDTO) {
        try {
            Feedback feedback = feedbackService.fromDTO(postDTO);
            feedbackService.savePost(feedback);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    /**
     * Gère les requêtes GET pour récupérer les feedbacks par équipe et sprint.
     *
     * @param teamId L'identifiant de l'équipe.
     * @param sprintId L'identifiant du sprint.
     * @return Une réponse contenant la liste des feedbacks.
     */
    @GetMapping("/feedbacks")
    public ResponseEntity<List<PostDTO>> getFeedback(@RequestParam("teamId") int teamId, @RequestParam("sprintId") int sprintId) {
        try {
            return ResponseEntity.ok(feedbackService.getPostByTeamIdAndSprintId(teamId, sprintId));
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    /**
     * Gère les requêtes DELETE pour supprimer un feedback.
     *
     * @param postDTO Le DTO du post à supprimer.
     * @return Une réponse indiquant si l'opération a réussi.
     */
    @DeleteMapping("/feedbacks")
    @PreAuthorize("hasAnyAuthority('TC', 'SS', 'OL', 'PL')")
    public ResponseEntity<Void> deleteFeedback(@RequestBody PostDTO postDTO) {
        try {
            Feedback feedback = feedbackService.fromDTO(postDTO);
            feedbackService.deletePost(feedback);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }
}
