package com.example.backend.service;

import com.example.backend.dao.CommentDao;
import com.example.backend.dto.PostDTO;
import com.example.backend.model.Comment;
import com.example.backend.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Service de gestion des commentaires.
 */
@Service
public class CommentService extends PostService{

    private final CommentDao commentDao;

    /**
     * Constructeur de la classe CommentService.
     *
     * @param commentDao Le DAO des commentaires.
     * @param teamService Le service de gestion des équipes.
     * @param sprintService Le service de gestion des sprints.
     * @param userService Le service de gestion des utilisateurs.
     */
    @Autowired
    public CommentService(CommentDao commentDao, TeamService teamService, SprintService sprintService, UserService userService){
        super(teamService, sprintService, userService);
        this.commentDao = commentDao;
    }

    /**
     * Crée un commentaire à partir d'un DTO.
     *
     * @param postDTO Le DTO du commentaire.
     * @return Le commentaire créé.
     */
    @Override
    public Comment fromDTO(PostDTO postDTO) {
        Comment comment = new Comment();
        initializePostFromDTO(comment, postDTO);
        return comment;
    }

    /**
     * Sauvegarde un commentaire.
     *
     * @param post Le commentaire à sauvegarder.
     */
    @Override
    public void savePost(Post post) {
        if (post instanceof Comment comment) {
            comment.setDate(new Date(System.currentTimeMillis()));
            commentDao.save((Comment) post);
        }
    }

    /**
     * Met à jour un commentaire.
     *
     * @param post Le commentaire à mettre à jour.
     */
    @Override
    public void updatePost(Post post) {
        if (post instanceof Comment comment) {
            try{
                Comment newComment = commentDao.findById(comment.getId()).orElseThrow();
                newComment.setTitle(post.getTitle());
                newComment.setContent(post.getContent());
                newComment.setDate(new Date(System.currentTimeMillis()));
                commentDao.save(newComment);
            } catch (Exception e) {
                savePost(comment);
            }
        }
    }

    /**
     * Récupère un commentaire par son identifiant.
     *
     * @param teamId L'identifiant de l'équipe.
     * @param sprintId L'identifiant du sprint.
     * @return Le commentaire trouvé.
     */
    @Override
    public List<PostDTO> getPostByTeamIdAndSprintId(int teamId, int sprintId) {
        // Retrieve the list of comments, or throw an exception if not found
        List<Post> comments = commentDao.findByTeamIdAndSprintId(teamId, sprintId)
                .orElseThrow(() -> new NoSuchElementException("No comments found for the given teamId and sprintId"));

        // Convert the list of comments to a list of PostDTOs using the stream pipeline
        return comments.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Supprime un commentaire.
     *
     * @param post Le commentaire à supprimer.
     */
    @Override
    public void deletePost(Post post) {
        if (post instanceof Comment comment) {
            commentDao.delete(comment);
        }
    }
}
