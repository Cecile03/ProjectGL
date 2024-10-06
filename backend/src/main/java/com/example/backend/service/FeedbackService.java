package com.example.backend.service;

import com.example.backend.dao.FeedbackDao;
import com.example.backend.dto.PostDTO;
import com.example.backend.model.Feedback;
import com.example.backend.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Service de gestion des retours.
 */
@Service
public class FeedbackService extends PostService {

    private final FeedbackDao feedbackDao;

    /**
     * Constructeur de la classe FeedbackService.
     *
     * @param feedbackDao Le DAO des retours.
     * @param teamService Le service de gestion des équipes.
     * @param sprintService Le service de gestion des sprints.
     * @param userService Le service de gestion des utilisateurs.
     */
    @Autowired
    public FeedbackService(FeedbackDao feedbackDao, TeamService teamService, SprintService sprintService, UserService userService){
        super(teamService, sprintService, userService);
        this.feedbackDao = feedbackDao;
    }

    /**
     * Crée un retour à partir d'un DTO.
     *
     * @param postDTO Le DTO du retour.
     * @return Le retour créé.
     */
    @Override
    public Feedback fromDTO(PostDTO postDTO) {
        Feedback feedback = new Feedback();
        initializePostFromDTO(feedback, postDTO);
        return feedback;
    }

    /**
     * Sauvegarde un retour.
     *
     * @param post Le retour à sauvegarder.
     */
    @Override
    public void savePost(Post post) {
        if (post instanceof Feedback feedback) {
            feedback.setDate(new Date(System.currentTimeMillis()));
            feedbackDao.save(feedback);
        }
    }

    /**
     * Convertit un retour en DTO.
     *
     * @param post Le retour à convertir.
     * @return Le DTO du retour.
     */
    @Override
    public void updatePost(Post post) {
        if (post instanceof Feedback feedback) {
            try{
                Feedback newFeedback = feedbackDao.findById(feedback.getId()).orElseThrow();
                newFeedback.setTitle(feedback.getTitle());
                newFeedback.setContent(feedback.getContent());
                newFeedback.setDate(new Date(System.currentTimeMillis()));
                feedbackDao.save(newFeedback);
            } catch (Exception e) {
                savePost(feedback);
            }
        }
    }

    /**
     * Récupère un retour via son équipe et son sprint.
     *
     * @param teamId L'identifiant de l'équipe.
     * @param sprintId L'identifiant du sprint.
     * @return Le retour trouvé.
     */
    @Override
    public List<PostDTO> getPostByTeamIdAndSprintId(int teamId, int sprintId) {
        // Retrieve the list of comments, or throw an exception if not found
        List<Post> feedbacks = feedbackDao.findByTeamIdAndSprintId(teamId, sprintId)
                .orElseThrow(() -> new NoSuchElementException("No feedbacks found for the given teamId and sprintId"));

        // Convert the list of comments to a list of PostDTOs using the stream pipeline
        return feedbacks.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Supprime un retour.
     *
     * @param post Le retour à supprimer.
     */
    @Override
    public void deletePost(Post post) {
        if(post instanceof Feedback feedback){
            feedbackDao.delete(feedback);
        }
    }
}