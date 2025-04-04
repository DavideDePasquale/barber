package com.barber.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.expirationMs}")
    private Long jwtExpirations;

    private Key getSigningKey(){
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(String username,Long idUtente, List<String> roles){
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roles",roles);
        claims.put("utenteId",idUtente);
        return Jwts.builder()
                .setSubject(username)
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirations))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromToken(String token){
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token){
        try {
            Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .parseClaimsJws(token);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    public List<String> getRolesFromToken(String token){
        Claims claims = Jwts.parser()
                .setSigningKey(getSigningKey())
                .parseClaimsJws(token)
                .getBody();
        return claims.get("roles",List.class);
    }

    //estraggo tutti i clamis dal token
    public Claims estraiTuttiIClaims(String token){
        return Jwts.parser()
                .setSigningKey(getSigningKey())   //impostiamo la chiave per la validazione
                .parseClaimsJws(token)  //qui avviene la decodifica del token
                .getBody();             // dove ci restituisce il claims (ciò che voglio indietro)
    }

    //estrazione dell'id dell'utente dal token
    public Long estraiUtenteId(String token){
        Claims claims = estraiTuttiIClaims(token);         //estrazione dei claims dal token
        return claims.get("utenteId",Long.class);  //estrazione id utente
    }

}
