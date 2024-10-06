package com.example.backend.controller;

import com.example.backend.dto.PostDTO;
import com.example.backend.model.Comment;
import com.example.backend.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Le contrôleur Comment.
 * Gère les requêtes HTTP liées aux opérations de commentaire.
 */
@RestController
@RequestMapping("/comments")
@PreAuthorize("hasAnyAuthority('TC', 'SS', 'OL', 'PL')")
public class CommentController {

    private final CommentService commentService;

    /**
     * Constructeur pour l'injection de dépendances.
     *
     * @param commentService Le service de commentaire à injecter.
     */
    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Gère les requêtes PUT pour sauvegarder un commentaire.
     *
     * @param postDTO Le DTO du post à sauvegarder.
     * @return Une réponse indiquant si l'opération a réussi.
     */
    //Pour tous les TC, SS, OL, PL
    @PutMapping()
    public ResponseEntity<Void> saveComment(@RequestBody PostDTO postDTO) {
        try {
            Comment comment = commentService.fromDTO(postDTO);
            commentService.updatePost(comment);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    /**
     * Gère les requêtes GET pour récupérer les commentaires par équipe et sprint.
     *
     * @param teamId L'identifiant de l'équipe.
     * @param sprintId L'identifiant du sprint.
     * @return Une réponse contenant la liste des commentaires.
     */
    @GetMapping()
    public ResponseEntity<List<PostDTO>> getCommentsByTeamAndSprint(@RequestParam("teamId") int teamId, @RequestParam("sprintId") int sprintId) {
        try {
            return ResponseEntity.ok(commentService.getPostByTeamIdAndSprintId(teamId, sprintId));
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    /**
     * Gère les requêtes DELETE pour supprimer un commentaire.
     *
     * @param postDTO Le DTO du post à supprimer.
     * @return Une réponse indiquant si l'opération a réussi.
     */
    @DeleteMapping()
    public ResponseEntity<Void> deleteComment(@RequestBody PostDTO postDTO) {
        try {
            Comment comment = commentService.fromDTO(postDTO);
            commentService.deletePost(comment);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }
}
