package com.barber.repository;

import com.barber.model.Utente;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UtenteRepository extends JpaRepository<Utente,Long> {
    //recupero utente dall'username
    Optional<Utente> findByUsername(String username);
    List<Utente> findAll();
    //chiavi che non devono essere duplicate, quindi ci faccio dei controlli
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
