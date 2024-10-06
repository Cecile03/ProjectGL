package com.example.backend.service;

import com.example.backend.dao.CommentDao;
import com.example.backend.dto.*;
import com.example.backend.model.Comment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentDao commentDao;

    @Mock
    private TeamService teamService;

    @Mock
    private SprintService sprintService;

    @Mock
    private UserService userService;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void fromDTO_createsCommentFromDTO() {
        UserSendDTO user = new UserSendDTO();
        TeamSendDTO team = new TeamSendDTO();
        SprintDTO sprint = new SprintDTO();
        PostDTO postDTO = new PostDTO();
        postDTO.setTitle("Test Title");
        postDTO.setContent("Test Content");
        postDTO.setEmitter(user);
        postDTO.setTeam(team);
        postDTO.setSprint(sprint);

        Comment comment = commentService.fromDTO(postDTO);

        assertEquals(postDTO.getTitle(), comment.getTitle());
        assertEquals(postDTO.getContent(), comment.getContent());
    }

    @Test
    void savePost_savesCommentWhenPostIsComment() {
        Comment comment = new Comment();
        commentService.savePost(comment);

        verify(commentDao, times(1)).save(comment);
    }

    @Test
    void updatePost_updatesCommentWhenPostIsComment() {
        Comment comment = new Comment();
        comment.setId(1);
        comment.setTitle("New Title");
        comment.setContent("New Content");

        Comment existingComment = new Comment();
        existingComment.setId(1);
        existingComment.setTitle("Old Title");
        existingComment.setContent("Old Content");

        when(commentDao.findById(1)).thenReturn(Optional.of(existingComment));

        commentService.updatePost(comment);

        verify(commentDao, times(1)).save(existingComment);
        assertEquals("New Title", existingComment.getTitle());
        assertEquals("New Content", existingComment.getContent());
    }

    @Test
    void updatePost_createsNewCommentWhenCommentNotFound() {
        Comment comment = new Comment();
        comment.setId(1);
        comment.setTitle("New Title");
        comment.setContent("New Content");

        when(commentDao.findById(1)).thenThrow(new NoSuchElementException());

        commentService.updatePost(comment);

        verify(commentDao, times(1)).save(comment);
    }

    @Test
    void getPostByTeamIdAndSprintId_returnsPostDTOsWhenCommentsExist() {
        // Given
        int teamId = 1;
        int sprintId = 1;
        Comment comment1 = new Comment();
        Comment comment2 = new Comment();
        List<Comment> comments = Arrays.asList(comment1, comment2);
        doReturn(Optional.of(comments)).when(commentDao).findByTeamIdAndSprintId(teamId, sprintId);

        // When
        List<PostDTO> postDTOs = commentService.getPostByTeamIdAndSprintId(teamId, sprintId);

        // Then
        assertEquals(2, postDTOs.size());
    }

    @Test
    void shouldThrowExceptionWhenNoCommentByTeamIdAndSprintId() {
        int teamId = 1;
        int sprintId = 1;
        when(commentDao.findByTeamIdAndSprintId(teamId, sprintId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NoSuchElementException.class, () -> commentService.getPostByTeamIdAndSprintId(teamId, sprintId));

        String expectedMessage = "No comments found for the given teamId and sprintId";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void deletePost_deletesCommentWhenPostIsComment() {
        Comment comment = new Comment();
        commentService.deletePost(comment);

        verify(commentDao, times(1)).delete(comment);
    }
}