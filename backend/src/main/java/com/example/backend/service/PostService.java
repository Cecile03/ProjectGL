package com.example.backend.service;

import com.example.backend.dto.PostDTO;
import com.example.backend.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service de gestion des posts.
 */
@Service
public abstract class PostService {

    protected TeamService teamService;
    protected SprintService sprintService;
    protected UserService userService;

    /**
     * Constructeur de la classe PostService.
     *
     * @param teamService Le service de gestion des équipes.
     * @param sprintService Le service de gestion des sprints.
     * @param userService Le service de gestion des utilisateurs.
     */
    @Autowired
    protected PostService(TeamService teamService, SprintService sprintService, UserService userService) {
        this.teamService = teamService;
        this.sprintService = sprintService;
        this.userService = userService;
    }

    /**
     * Initialise un post à partir d'un DTO.
     *
     * @param post Le post à initialiser.
     * @param postDTO Le DTO du post.
     */
    protected void initializePostFromDTO(Post post, PostDTO postDTO) {
        post.setId(postDTO.getId());
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setDate(postDTO.getDate());
        post.setEmitter(userService.getUserById(postDTO.getEmitter().getId()));
        post.setTeam(teamService.getTeamById(postDTO.getTeam().getId()));
        post.setSprint(sprintService.getSprintById(postDTO.getSprint().getId()));
    }

    public abstract Post fromDTO(PostDTO postDTO);

    /**
     * Convertit un post en DTO.
     *
     * @param post Le post à convertir.
     * @return Le DTO du post créé.
     */
    public PostDTO toDTO(Post post) {
        PostDTO postDTO = new PostDTO();
        postDTO.setId(post.getId());
        postDTO.setTitle(post.getTitle());
        postDTO.setContent(post.getContent());
        postDTO.setDate(post.getDate());
        postDTO.setEmitter(userService.toDTO(post.getEmitter()));
        postDTO.setTeam(teamService.toDTO(post.getTeam()));
        postDTO.setSprint(sprintService.toDTO(post.getSprint()));
        return postDTO;
    }

    public abstract void savePost(Post post);

    public abstract void updatePost(Post post);

    public abstract List<PostDTO> getPostByTeamIdAndSprintId(int teamId, int sprintId);

    public abstract void deletePost(Post post);
}
