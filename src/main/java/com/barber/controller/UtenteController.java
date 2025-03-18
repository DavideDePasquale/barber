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
import java.util.Map;

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
    public ResponseEntity<Map<String,String>> updateUtente(@PathVariable Long id, @RequestBody UtenteDTO utenteDTO){
        Map <String,String> resp = utenteService.updateUtente(id,utenteDTO);

        return ResponseEntity.ok(resp);
    }




    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUtente(@PathVariable Long id){
        utenteService.deleteUtente(id);
        return ResponseEntity.noContent().build();
    }

}
