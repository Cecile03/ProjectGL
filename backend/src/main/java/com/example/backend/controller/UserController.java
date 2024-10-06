package com.example.backend.controller;

import com.example.backend.dto.UserInteract;
import com.example.backend.model.Team;
import com.example.backend.model.User;
import com.example.backend.service.AuthService;
import com.example.backend.service.TeamService;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Le contrôleur UserController.
 * Gère les requêtes HTTP liées aux opérations des utilisateurs.
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final AuthService authService;
    private final TeamService teamService;

    /**
     * Constructeur pour l'injection de dépendances.
     *
     * @param userService Le service des utilisateurs à injecter.
     * @param authService Le service d'authentification à injecter.
     * @param teamService Le service des équipes à injecter.
     */
    @Autowired
    public UserController(UserService userService, AuthService authService, TeamService teamService) {
        this.userService = userService;
        this.authService = authService;
        this.teamService = teamService;
    }

    /**
     * Gère les requêtes POST pour créer des étudiants.
     * Cette méthode prend une liste d'étudiants à créer en tant que corps de la requête.
     * Elle renvoie une réponse indiquant si l'opération a réussi.
     *
     * @param students La liste des étudiants à créer.
     * @return Une réponse indiquant si l'opération a réussi.
     */
    @PostMapping("/students")
    @PreAuthorize("hasAnyAuthority('OL', 'PL')")
    public ResponseEntity<Void> createStudents(@RequestBody List<UserInteract> students) {
        try {
            for (UserInteract userInt : students) {
                if(userService.existsByEmail(userInt.getEmail())) {
                    User userToDelete = userService.loadUserByEmail(userInt.getEmail());
                    userService.deleteUser(userToDelete.getId());
                }
                User user = userService.userInteractToUser(userInt).orElseThrow();
                userService.setDefaultRoles(user);
                authService.setDefaultPassword(user);
                userService.saveUser(user);
            }
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Gère les requêtes GET pour obtenir tous les enseignants.
     *
     * @return Une réponse contenant la liste des enseignants.
     */
    @GetMapping("/teachers")
    public ResponseEntity<List<User>> getTeachers(){
        try{
            return ResponseEntity.ok(userService.getTeachers());
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    /**
     * Gère les requêtes GET pour obtenir tous les étudiants.
     *
     * @return Une réponse contenant la liste des étudiants.
     */
    @GetMapping("/students")
    public ResponseEntity<List<User>> getStudents(){
        try{
            return ResponseEntity.ok(userService.getStudents());
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    /**
     * Gère les requêtes GET pour obtenir tous les utilisateurs.
     *
     * @return Une réponse contenant la liste de tous les utilisateurs.
     */
    @GetMapping()
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            return ResponseEntity.ok(userService.getAllUsers());
        }catch(Exception e){
            return ResponseEntity.status(400).build();
        }
    }

    /**
     * Gère les requêtes GET pour obtenir un utilisateur par son identifiant.
     *
     * @param id L'identifiant de l'utilisateur.
     * @return Une réponse contenant l'utilisateur correspondant à l'identifiant.
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        try {
            return ResponseEntity.ok(userService.getUserById(id));
        }catch(Exception e){
            return ResponseEntity.status(400).build();
        }
    }

    /**
     * Gère les requêtes DELETE pour supprimer un utilisateur par son identifiant.
     *
     * @param id L'identifiant de l'utilisateur à supprimer.
     * @return Une réponse indiquant si l'opération a réussi.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('OL', 'PL')")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        try{
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        }catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    /**
     * Gère les requêtes DELETE pour supprimer tous les étudiants.
     *
     * @return Une réponse indiquant si l'opération a réussi.
     */
    @DeleteMapping("/students")
    @PreAuthorize("hasAnyAuthority('OL', 'PL')")
    public ResponseEntity<Void> deleteStudents() {
        try{
            userService.deleteStudents();
            teamService.deleteAllTeam();

            return ResponseEntity.ok().build();
        }catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    /**
     * Gère les requêtes PUT pour mettre à jour une liste d'utilisateurs.
     *
     * @param users La liste des utilisateurs à mettre à jour.
     * @return Une réponse indiquant si l'opération a réussi.
     */
    @PutMapping("/list")
    @PreAuthorize("hasAnyAuthority('OL', 'PL')")
    public ResponseEntity<Void> updateUser(@RequestBody List<UserInteract> users) {
        try {
            for (UserInteract user : users) {
                if(!userService.existsByEmail(user.getEmail())) {
                    userService.saveUser(userService.userInteractToUser(user).orElseThrow());
                }else{
                    User userToModify = userService.loadUserByEmail(user.getEmail());
                    userToModify.setGradePast(user.getGradePast());
                    userService.updateUser(userToModify);
                }
            }
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Gère les requêtes GET pour obtenir les équipes d'un utilisateur par son identifiant.
     *
     * @param id L'identifiant de l'utilisateur.
     * @return Une réponse contenant la liste des identifiants des équipes de l'utilisateur.
     */
    @GetMapping("/{id}/teams")
    public ResponseEntity<List<Integer>> getTeams(@PathVariable int id) {
        try {
            Set<Team> teams = userService.getUserById(id).getTeams();
            List<Integer> teamsId = new ArrayList<>();
            for (Team team : teams) {
                teamsId.add(team.getId());
            }
            return ResponseEntity.ok(teamsId);
        }catch(Exception e){
            return ResponseEntity.status(400).build();
        }
    }

    /**
     * Gère les requêtes POST pour enregistrer un utilisateur.
     *
     * @param user L'utilisateur à enregistrer.
     * @return Une réponse contenant l'utilisateur enregistré.
     */
    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('OL', 'PL')")
    public ResponseEntity<User> saveUser(@RequestBody User user) {
        try{
            return ResponseEntity.ok(userService.saveUser(user));
        }catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    /**
     * Gère les requêtes PUT pour mettre à jour un utilisateur.
     *
     * @param user L'utilisateur à mettre à jour.
     * @return Une réponse contenant l'utilisateur mis à jour.
     */
    @PutMapping()
    @PreAuthorize("hasAnyAuthority('OL', 'PL')")
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        try{
            return ResponseEntity.ok(userService.updateUser(user));
        }catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    /**
     * Gère les requêtes GET pour obtenir tous les membres du personnel.
     *
     * @return Une réponse contenant la liste de tous les membres du personnel.
     */
    @GetMapping("/staff")
    @PreAuthorize("hasAnyAuthority('SS', 'PL','OS')")
    public ResponseEntity<List<User>> getAllStaff() {
        try {
            List<User> staff = userService.getAllStaff();
            return ResponseEntity.ok(staff);
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }


}
