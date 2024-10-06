package com.example.backend.service;

import com.example.backend.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.Getter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * Classe utilitaire pour la gestion des tokens JWT.
 */
@Getter
@Component
public class JWTUtils {

    private SecretKey Key;
    private  static  final long EXPIRATION_TIME = 86400000; //24hours or 86400000 milisecs

    /**
     * Constructeur de la classe JWTUtils.
     */
    public JWTUtils(){
        String secreteString = "843567893696976453275974432697R634976R738467TR678T34865R6834R8763T478378637664538745673865783678548735687R3";
        byte[] keyBytes = Base64.getDecoder().decode(secreteString.getBytes(StandardCharsets.UTF_8));
        this.Key = new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    /**
     * Génère un token JWT à partir d'un utilisateur.
     *
     * @param user L'utilisateur.
     * @return Le token généré.
     */
    public String generateToken(User user){
        return Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(Key)
                .compact();
    }

    /**
     * Génère un token JWT à partir d'un utilisateur et de ses claims.
     *
     * @param claims Les claims.
     * @param userDetails Les détails de l'utilisateur.
     * @return Le token généré.
     */
    public String generateRefreshToken(Map<String, Object> claims, UserDetails userDetails){
        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(Key)
                .compact();
    }

    /**
     * Extrait l'email d'un token.
     *
     * @param token Le token.
     * @return L'email extrait.
     */
    public String extractEmail(String token){
        return extractClaims(token, Claims::getSubject);
    }

    /**
     * Extrait les claims d'un token.
     *
     * @param token Le token.
     * @return Les claims extraits.
     */
    private <T> T extractClaims(String token, Function<Claims, T> claimsTFunction){
        try {
            return claimsTFunction.apply(Jwts.parser().verifyWith(Key).build().parseSignedClaims(token).getPayload());
        }catch (Exception e){
            return null;
        }
    }

    /**
     * Vérifie si un token est valide.
     *
     * @param token Le token.
     * @param user L'utilisateur.
     * @return Vrai si le token est valide, faux sinon.
     */
    public boolean isTokenValid(String token, User user){
        final String email = extractEmail(token);
        return (email.equals(user.getEmail()) && !isTokenExpired(token));
    }

    /**
     * Vérifie si un token est expiré.
     *
     * @param token Le token.
     * @return Vrai si le token est expiré, faux sinon.
     */
    public boolean isTokenExpired(String token){
        Date expirationDate = extractClaims(token, Claims::getExpiration);
        return expirationDate != null && expirationDate.before(new Date());
    }

}
