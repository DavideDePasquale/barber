package com.barber.controller;

import com.barber.exception.DuplicateEmailException;
import com.barber.exception.DuplicateUsernameException;
import com.barber.model.Utente;
import com.barber.payload.UtenteDTO;
import com.barber.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/utente")
public class UtenteController {

    @Autowired UtenteService utenteService;





    // per gestire il tipo di eccezione nel front!
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage()); // Messaggio di errore
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // Restituisce codice 400
    }


    @GetMapping("/getAll")
    public ResponseEntity<List<UtenteDTO>> getAllUtenti(){
        List<UtenteDTO> utenti = utenteService.getAllUtenti();
        return ResponseEntity.ok(utenti);
    }
    @GetMapping("/{id}")
    public ResponseEntity<UtenteDTO> getUtenteById(@PathVariable Long id){
        return ResponseEntity.ok(utenteService.getUtenteById(id));
    }



    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateUtente(@PathVariable Long id, @RequestBody UtenteDTO utenteDTO) {
        try {
            Map<String, String> resp = utenteService.updateUtente(id, utenteDTO);
            return ResponseEntity.ok(resp);
        } catch (DuplicateEmailException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("❌", "Email già in uso"));
        } catch (DuplicateUsernameException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("❌", "Username già in uso"));
        }
    }





    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUtente(@PathVariable Long id){
        utenteService.deleteUtente(id);
        return ResponseEntity.noContent().build();
    }


}
