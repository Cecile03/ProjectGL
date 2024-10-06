package com.example.backend.controller;

import com.example.backend.dto.UserInteract;
import com.example.backend.model.User;
import com.example.backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * Le contrôleur d'authentification.
 * Gère les requêtes HTTP liées à l'authentification des utilisateurs.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * Constructeur pour l'injection de dépendances.
     *
     * @param authService Le service d'authentification à injecter.
     */
    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Gère les requêtes POST pour l'inscription d'un nouvel utilisateur.
     *
     * @param signUpRequest Les informations de l'utilisateur à inscrire.
     * @return Une réponse contenant les informations de l'utilisateur inscrit.
     */
    @PostMapping("/signup")
    public ResponseEntity<UserInteract> signUp(@RequestBody UserInteract signUpRequest){
        try{
            return ResponseEntity.ok(authService.signUp(signUpRequest));
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    /**
     * Gère les requêtes POST pour la connexion d'un utilisateur.
     *
     * @param signInRequest Les informations de l'utilisateur à connecter.
     * @return Une réponse contenant les informations de l'utilisateur connecté.
     */
    @PostMapping("/signin")
    public ResponseEntity<UserInteract> signIn(@RequestBody UserInteract signInRequest){
        return ResponseEntity.ok(authService.signIn(signInRequest));
    }

    /**
     * Gère les requêtes POST pour le rafraîchissement du jeton d'un utilisateur.
     *
     * @param refreshTokenRequest Les informations de l'utilisateur pour le rafraîchissement du jeton.
     * @return Une réponse contenant les informations de l'utilisateur avec le jeton rafraîchi.
     */
    @PostMapping("/refresh")
    public ResponseEntity<UserInteract> refreshToken(@RequestBody UserInteract refreshTokenRequest){
        return ResponseEntity.ok(authService.refreshToken(refreshTokenRequest));
    }

    /**
     * Gère les requêtes GET pour récupérer l'utilisateur actuellement connecté.
     *
     * @param request La requête HTTP.
     * @return Une réponse contenant les informations de l'utilisateur connecté.
     */
    @GetMapping("/getme")
    public ResponseEntity<User> getCurrentUser(HttpServletRequest request) {
        // Extraire le jeton d'authentification de la requête
        String authToken = extractAuthToken(request);

        try {
            // Récupérer l'utilisateur actuel à partir du jeton d'authentification
            User currentUser = authService.getUserFromToken(authToken);
            return ResponseEntity.ok(currentUser);
        }catch (Exception e){
            // Retourner une erreur si l'utilisateur n'est pas trouvé
            return ResponseEntity.status(401).build();
        }
    }

    /**
     * Extrait le jeton d'authentification du header Authorization de la requête HTTP.
     *
     * @param request La requête HTTP.
     * @return Le jeton d'authentification.
     */
    private String extractAuthToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
