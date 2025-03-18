package com.barber.security;

import com.barber.model.Utente;
import com.barber.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UtenteRepository utenteRepository;

    public UserDetailsServiceImpl(UtenteRepository utenteRepository) {
        this.utenteRepository = utenteRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Utente> utente = utenteRepository.findByUsername(username);

        if (utente.isEmpty()){
            throw new UsernameNotFoundException("Utente con username ' " + username + " ' non trovato!⚠️");
        }
        return User.builder()
                .username(utente.get().getUsername())
                .password(utente.get().getPassword())
                .authorities(utente.get().getTipoRuolo().name()).build();



        // occhio qui!!!!!!!!!
    }
}
