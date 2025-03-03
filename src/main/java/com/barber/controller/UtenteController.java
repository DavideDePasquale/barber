package com.barber.controller;

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
import java.util.List;

@RestController
@RequestMapping("/utente")
public class UtenteController {

    @Autowired UtenteService utenteService;

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
    public ResponseEntity<UtenteDTO> updateUtente(@PathVariable Long id, UtenteDTO utenteDTO){
        return ResponseEntity.ok(utenteService.updateUtente(id,utenteDTO));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUtente(@PathVariable Long id){
        return ResponseEntity.noContent().build();
    }
    @PostMapping(value = "/avatar",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UtenteDTO> createUtenteWithAvatar(@RequestPart("utente") UtenteDTO utenteDTO, @RequestPart("file")MultipartFile file){
        try {
            UtenteDTO savedUser = utenteService.uploadImage(utenteDTO,file);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
