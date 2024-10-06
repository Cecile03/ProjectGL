package com.example.backend.controller;

import com.example.backend.dto.UserFlagDto;
import com.example.backend.model.Flag;
import com.example.backend.model.User;
import com.example.backend.model.UserFlag;
import com.example.backend.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Le contrôleur UserFlagController.
 * Gère les requêtes HTTP liées aux opérations des drapeaux des utilisateurs.
 */
@RestController
@RequestMapping("/userFlags")
@PreAuthorize("hasAnyAuthority('TC', 'SS', 'OL', 'PL','OS')")
public class UserFlagController {

    private final UserFlagService userFlagService;
    private final UserService userService;
    private final TeamService teamService;

    /**
     * Constructeur pour l'injection de dépendances.
     *
     * @param userFlagService Le service des drapeaux des utilisateurs à injecter.
     * @param userService Le service des utilisateurs à injecter.
     * @param teamService Le service des équipes à injecter.
     */
    @Autowired
    public UserFlagController(UserFlagService userFlagService, UserService userService, TeamService teamService) {
        this.userFlagService = userFlagService;
        this.userService = userService;
        this.teamService = teamService;
    }

    /**
     * Gère les requêtes GET pour obtenir tous les drapeaux des utilisateurs.
     * Cette méthode renvoie une liste de tous les drapeaux des utilisateurs.
     *
     * @return Une liste de tous les drapeaux des utilisateurs.
     */
    @GetMapping()
    public List<UserFlag> getAllUserFlags() {
        return userFlagService.getAllUserFlags();
    }

    /**
     * Gère les requêtes POST pour créer des drapeaux des utilisateurs.
     * Cette méthode prend une liste de drapeaux des utilisateurs à créer en tant que corps de la requête.
     * Elle renvoie une liste de drapeaux des utilisateurs créés.
     *
     * @param userFlagDtos La liste des drapeaux des utilisateurs à créer.
     * @return Une liste de drapeaux des utilisateurs créés.
     */
    @PostMapping()
    public List<UserFlag> createUserFlags(@RequestBody List<UserFlagDto> userFlagDtos) {
        return userFlagDtos.stream()
                .map(userFlagDto -> {
                    UserFlag userFlag = new UserFlag();
                    userFlag.setId(userFlagDto.getId());
                    userFlag.setTeamSwitched(userFlagDto.isTeamSwitched());
                    userFlag.setValidated(userFlagDto.getValidated());
                    userFlag.setCanceledString(userFlagDto.getCanceledString());

                    Flag flag = new Flag();
                    flag.setId(userFlagDto.getFlagId().getId());
                    flag.setUser(userService.getUserByIdNotOptional(userFlagDto.getFlagId().getUserId()));
                    flag.setTeam1(teamService.getTeamById(userFlagDto.getFlagId().getTeam1Id()));
                    flag.setTeam2(teamService.getTeamById(userFlagDto.getFlagId().getTeam2Id()));
                    flag.setComment(userFlagDto.getFlagId().getComment());
                    flag.setDatetime(userFlagDto.getFlagId().getDatetime());
                    userFlag.setFlag(flag);

                    User user = new User();
                    user.setId(userFlagDto.getUserId().getId());
                    user.setFirstName(userFlagDto.getUserId().getFirstName());
                    user.setLastName(userFlagDto.getUserId().getLastName());
                    userFlag.setUser(user);

                    return userFlagService.saveUserFlag(userFlag);
                })
                .collect(Collectors.toList());
    }

    /**
     * Gère les requêtes GET pour obtenir un drapeau d'utilisateur par son identifiant.
     * Cette méthode renvoie un drapeau d'utilisateur par son identifiant.
     *
     * @param flagId L'identifiant du drapeau d'utilisateur à obtenir.
     * @return Une réponse contenant le drapeau d'utilisateur ou une réponse indiquant que le drapeau d'utilisateur n'a pas été trouvé.
     */
    @GetMapping("/flag/{flagId}")
    public ResponseEntity<List<UserFlag>> getAllUserFlagByFlagId(@PathVariable int flagId) {
        try {
            List<UserFlag> userFlags = userFlagService.getAllUserFlagByFlagId(flagId);
            return new ResponseEntity<>(userFlags, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gère les requêtes PUT pour mettre à jour un drapeau d'utilisateur par son identifiant.
     * Cette méthode met à jour un drapeau d'utilisateur par son identifiant.
     *
     * @param id L'identifiant du drapeau d'utilisateur à mettre à jour.
     * @param validated La valeur de la validation du drapeau d'utilisateur.
     * @return Une réponse contenant le drapeau d'utilisateur mis à jour ou une réponse indiquant que le drapeau d'utilisateur n'a pas été trouvé.
     */
    @PutMapping("/{id}/validated")
    public ResponseEntity<UserFlag> setValidated(@PathVariable int id, @RequestBody boolean validated) {
        try {
            UserFlag userFlag = userFlagService.setValidated(id, validated);
            return new ResponseEntity<>(userFlag, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gère les requêtes DELETE pour supprimer un drapeau d'utilisateur par son identifiant.
     * Cette méthode supprime un drapeau d'utilisateur par son identifiant.
     *
     * @param flagId L'identifiant du drapeau d'utilisateur à obtenir.
     * @return Une réponse contenant le drapeau d'utilisateur ou une réponse indiquant que le drapeau d'utilisateur n'a pas été trouvé.
     */
    @DeleteMapping("/flag/{flagId}")
    public ResponseEntity<Void> deleteUserFlagsByFlagId(@PathVariable int flagId) {
        try {
            userFlagService.deleteUserFlagsByFlagId(flagId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gère les requêtes GET pour obtenir un drapeau d'utilisateur par son identifiant.
     * Cette méthode renvoie un drapeau d'utilisateur par son identifiant.
     *
     * @param flagId L'identifiant du drapeau d'utilisateur à obtenir.
     * @return Une réponse contenant le drapeau d'utilisateur ou une réponse indiquant que le drapeau d'utilisateur n'a pas été trouvé.
     */
    @GetMapping("/validated/{flagId}")
    public ResponseEntity<Boolean> areAllUserFlagsValidatedByFlagId(@PathVariable int flagId) {
        boolean areAllValidated = userFlagService.areAllUserFlagsValidatedByFlagId(flagId);
        return ResponseEntity.ok(areAllValidated);
    }
}