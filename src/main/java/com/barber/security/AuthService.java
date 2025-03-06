package com.barber.security;

import com.barber.model.Utente;
import com.barber.payload.request.LoginRequest;
import com.barber.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    public AuthService(AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    public ResponseEntity<?> authenticateUtente(LoginRequest loginRequest){
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            Object principal = authentication.getPrincipal();
            String username;
            if (principal instanceof User){
                username = ((User)principal).getUsername();
            } else {
                username = principal.toString();
            }

            username = ((UserDetails) authentication.getPrincipal()).getUsername();

            if (username == null || username.isEmpty()){
                throw new RuntimeException("Username non valido!");
            }
            Utente utente = utenteRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Utente non trovato!"));


           List <String> roles = authentication.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            String jwt = jwtUtils.generateToken(username,utente.getId(),roles);   //OCCHIO QUI....
            return ResponseEntity.ok(jwt);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Username o password non corrette!");
        }
    }
}
