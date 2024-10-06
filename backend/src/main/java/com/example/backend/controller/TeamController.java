package com.example.backend.controller;

import com.example.backend.dto.TeamDTO;
import com.example.backend.dto.UserInteract;
import com.example.backend.model.Team;
import com.example.backend.model.User;
import com.example.backend.service.TeamService;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Le contrôleur Team.
 * Gère les requêtes HTTP liées aux opérations d'équipe.
 */
@RestController
public class TeamController {

    private final TeamService teamService;
    private final UserService userService;

    /**
     * Constructeur pour l'injection de dépendances.
     *
     * @param teamService Le service d'équipe à injecter.
     * @param userService Le service d'utilisateur à injecter.
     */
    @Autowired
    public TeamController(TeamService teamService, UserService userService) {
        this.teamService = teamService;
        this.userService = userService;
    }

    /**
     * Gère les requêtes GET pour récupérer toutes les équipes.
     *
     * @return Une liste de toutes les équipes.
     */
    @GetMapping("/teams")
    public List<Team> getAllTeams() {
        return teamService.getAllTeams();
    }

    /**
     * Gère les requêtes GET pour récupérer une équipe par ID.
     *
     * @param id L'identifiant de l'équipe à récupérer.
     * @return L'équipe spécifiée.
     */
    @GetMapping("/teams/{id}")
    public Team getTeamById(@PathVariable int id) {
        return teamService.getTeamById(id);
    }

    /**
     * Gère les requêtes GET pour ajouter un membre à une équipe.
     *
     * @param teamId L'identifiant de l'équipe.
     * @param userId L'identifiant de l'utilisateur à ajouter à l'équipe.
     * @return Une réponse contenant l'utilisateur ajouté à l'équipe.
     */
    @GetMapping("/teams/{team_id}/add/{user_id}")
    @PreAuthorize("hasAnyAuthority('OL', 'PL')")
    public ResponseEntity<User> getTeam(@PathVariable("team_id") int teamId, @PathVariable("user_id") int userId){
        try{
            return ResponseEntity.ok(teamService.addTeamMember(teamId, userId));
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    /**
     * Gère les requêtes PUT pour mettre à jour une équipe.
     *
     * @param teamDTO Le DTO de l'équipe à mettre à jour.
     * @return Une réponse indiquant si l'opération a réussi.
     */
    @PutMapping("/teams")
    @PreAuthorize("hasAnyAuthority('OL', 'PL', 'SS')")
    public ResponseEntity<Void> updateTeam(@RequestBody TeamDTO teamDTO) {
        try {
            teamService.updateTeam(teamDTO);
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Gère les requêtes POST pour créer des équipes.
     *
     * @param parameters Les paramètres pour créer les équipes.
     * @return Une réponse contenant les équipes créées.
     */
    @PostMapping("/teams")
    @PreAuthorize("hasAnyAuthority('OL', 'PL')")
    public ResponseEntity<List<Team>> createTeams(@RequestBody TeamDTO parameters){
        try{
            List<UserInteract> studentsDTO = parameters.getStudents();
            List<User> students = new ArrayList<>();
            for(UserInteract student : studentsDTO){
                students.add(userService.userInteractToUser(student).orElseThrow());
            }
            List<UserInteract> teachersDTO = parameters.getTeachers();
            List<User> teachers = new ArrayList<>();
            for(UserInteract teacher : teachersDTO){
                teachers.add(userService.userInteractToUser(teacher).orElseThrow());
            }
            List<Team> teams = teamService.createTeams(students, teachers, parameters.getNames(),parameters.getNumberOfTeams(), parameters.getNumberOfGirlsPerTeam());
            return ResponseEntity.ok(teams);
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    /**
     * Gère les requêtes GET pour récupérer les membres d'une équipe.
     *
     * @param teamId L'identifiant de l'équipe.
     * @return Une réponse contenant les membres de l'équipe spécifiée.
     */
    @GetMapping("/teams/{team_id}/members")
    public ResponseEntity<Set<User>> getTeam(@PathVariable("team_id") int teamId){
        try{
            return ResponseEntity.ok(teamService.getTeamById(teamId).getUsers());
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    /**
     * Gère les requêtes DELETE pour supprimer une équipe.
     *
     * @param id L'identifiant de l'équipe à supprimer.
     * @return Une réponse indiquant si l'opération a réussi.
     */
    @DeleteMapping("/teams/{id}")
    @PreAuthorize("hasAnyAuthority('OL', 'PL')")
    public ResponseEntity<Void> deleteTeam(@PathVariable int id){
        try{
            teamService.deleteTeam(id);
            return ResponseEntity.ok().build();
        }catch(Exception e){
            return ResponseEntity.status(400).build();
        }
    }

    /**
     * Gère les requêtes DELETE pour supprimer toutes les équipes.
     *
     * @return Une réponse indiquant si l'opération a réussi.
     */
    @DeleteMapping("/teams")
    @PreAuthorize("hasAnyAuthority('OL', 'PL')")
    public ResponseEntity<Void> deleteAllTeam(){
        try{
            teamService.deleteAllTeam();
            return ResponseEntity.ok().build();
        }catch(Exception e){
            return ResponseEntity.status(400).build();
        }
    }

    /**
     * Gère les requêtes PUT pour valider une équipe.
     *
     * @param teamId L'identifiant de l'équipe à valider.
     * @param status Le statut a attribué à l'équipe.
     * @return Une réponse indiquant si l'opération a réussi.
     */
    @PutMapping("/teams/{teamId}/{status}")
    @PreAuthorize("hasAnyAuthority('SS','OL','PL')")
    public ResponseEntity<String> validateTeam(@PathVariable("teamId") int teamId, @PathVariable("status") String status) {
        try {
            teamService.toggleStatus(teamId, status);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}