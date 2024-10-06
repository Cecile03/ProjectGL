package com.example.backend.service;

import com.example.backend.dto.UserInteract;
import com.example.backend.model.Role;
import com.example.backend.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Objects;

/**
 * Service d'authentification qui gère les opérations d'inscription, de connexion et de rafraîchissement du jeton.
 */
@Service
public class AuthService {

    private final UserService userService;
    private final JWTUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    @Value("${default.password}")
    private String defaultPassword;

    /**
     * Constructeur de la classe AuthService.
     *
     * @param userService Le service utilisateur.
     * @param jwtUtils    Le service de gestion des jetons JWT.
     * @param passwordEncoder Le service de cryptage des mots de passe.
     * @param authenticationManager Le service de gestion de l'authentification.
     */
    @Autowired
    public AuthService(UserService userService, JWTUtils jwtUtils, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Inscription d'un nouvel utilisateur.
     * Si le mot de passe n'est pas fourni, un mot de passe par défaut est utilisé.
     * Le rôle de l'utilisateur est défini en fonction de la demande d'inscription.
     *
     * @param registrationRequest La demande d'inscription contenant les informations de l'utilisateur.
     * @return Un objet UserInteract contenant les informations de l'utilisateur enregistré ou une erreur.
     */
    public UserInteract signUp(UserInteract registrationRequest){
        UserInteract resp = new UserInteract();
        try {
            User user = new User();
            user.setEmail(registrationRequest.getEmail());
            if(Objects.equals(registrationRequest.getPassword(), "") || registrationRequest.getPassword()==null) {
                setDefaultPassword(user);
            }else{
                user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            }
            defineRole(user, registrationRequest);
            user.setLastName(registrationRequest.getLastName());
            user.setFirstName(registrationRequest.getFirstName());
            user.setGender(registrationRequest.getGender());
            user.setOption(registrationRequest.getOption());
            user.setBachelor(false);
            user.setGradePast(0.0);
            User ourUserResult = userService.saveUser(user);
            if (ourUserResult != null && ourUserResult.getId()>0) {
                resp.setUser(ourUserResult);
                resp.setMessage("User Saved Successfully");
                resp.setStatusCode(200);
            }
        }catch (Exception e){
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

    /**
     * Connexion d'un utilisateur existant.
     * Un jeton JWT est généré pour l'utilisateur après une authentification réussie.
     *
     * @param signinRequest La demande de connexion contenant l'e-mail et le mot de passe de l'utilisateur.
     * @return Un objet UserInteract contenant le jeton JWT, le jeton de rafraîchissement et d'autres informations ou une erreur.
     */
    public UserInteract signIn(UserInteract signinRequest){
        UserInteract response = new UserInteract();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signinRequest.getEmail(),signinRequest.getPassword()));
            var user = userService.loadUserByEmail(signinRequest.getEmail());
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24Hr");
            response.setMessage("Successfully Signed In");
        }catch (Exception e){
            response.setStatusCode(500);
            response.setError(e.getMessage());
        }
        return response;
    }

    /**
     * Définit le rôle de l'utilisateur en fonction de la demande d'inscription.
     * Si aucun rôle n'est fourni, des rôles par défaut sont attribués à l'utilisateur.
     *
     * @param user L'utilisateur pour lequel définir le rôle.
     * @param registrationRequest La demande d'inscription contenant les rôles de l'utilisateur.
     */
    private void defineRole(User user, UserInteract registrationRequest){
        try{
            for (String role : registrationRequest.getRoles()) {
                Role ourRole = userService.getRoleByName(role);
                user.addRole(ourRole);
            }
        }catch (Exception e){
            userService.setDefaultRoles(user);
        }
    }

    /**
     * Définit le mot de passe par défaut pour un utilisateur.
     *
     * @param user L'utilisateur pour lequel définir le mot de passe.
     */
    public void setDefaultPassword(User user) {
        user.setPassword(passwordEncoder.encode(defaultPassword));
    }

    /**
     * Rafraîchit le jeton JWT d'un utilisateur.
     * Un nouveau jeton JWT est généré si le jeton de rafraîchissement est valide.
     *
     * @param refreshTokenRequest La demande de rafraîchissement du jeton contenant le jeton de rafraîchissement.
     * @return Un objet UserInteract contenant le nouveau jeton JWT et d'autres informations ou une erreur.
     */
    public UserInteract refreshToken(UserInteract refreshTokenRequest) {
        UserInteract response = new UserInteract();
        String ourEmail = jwtUtils.extractEmail(refreshTokenRequest.getToken());
        User user = userService.loadUserByEmail(ourEmail);
        if (jwtUtils.isTokenValid(refreshTokenRequest.getToken(), user)) {
            var jwt = jwtUtils.generateToken(user);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRefreshToken(refreshTokenRequest.getToken());
            response.setExpirationTime("24Hr");
            response.setMessage("Successfully Refreshed Token");
        } else {
            response.setStatusCode(401); // Unauthorized
            response.setMessage("Invalid or Expired Token");
        }
        return response;
    }

    /**
     * Récupère un utilisateur à partir d'un jeton JWT.
     *
     * @param token Le jeton JWT.
     * @return L'utilisateur correspondant au jeton JWT.
     * @throws IllegalArgumentException Si le jeton est null ou vide.
     * @throws EntityNotFoundException Si l'utilisateur n'est pas trouvé.
     */
    public User getUserFromToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }
        try {
            Claims claims = Jwts.parser().verifyWith(jwtUtils.getKey()).build().parseSignedClaims(token).getPayload();
            String email = claims.getSubject();

            return userService.loadUserByEmail(email);
        } catch (Exception e) {
            // Gérer l'exception en fonction de vos besoins
            throw new EntityNotFoundException("User not found");
        }
    }

}
