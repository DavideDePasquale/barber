package com.barber.controller;

import com.barber.exception.*;
import com.barber.model.Appuntamento;
import com.barber.payload.AppuntamentoDTO;
import com.barber.payload.AppuntamentoDTOnoID;
import com.barber.payload.mapper.AppuntamentoMapperDTO;
import com.barber.repository.AppuntamentoRepository;
import com.barber.security.JwtUtils;
import com.barber.service.AppuntamentoService;
import com.barber.service.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/appuntamento")
public class AppuntamentoController {


    @Autowired AppuntamentoService appuntamentoService;
    @Autowired AppuntamentoRepository appuntamentoRepository;
    @Autowired
    private AppuntamentoMapperDTO appuntamentoMapperDTO;
    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/nuovoappuntamento")
    public ResponseEntity<?> createAppuntamento(HttpServletRequest request, @RequestBody @Validated AppuntamentoDTOnoID appuntamentoDTOnoID){

        try {
            Long userId = (Long) request.getAttribute("userId");

            AppuntamentoDTO appuntamentoCreato;
          try {
              try {
                  appuntamentoCreato = appuntamentoService.createAppuntamento(appuntamentoDTOnoID,userId);
                  System.out.println("Appuntamento creato con successo: " + appuntamentoCreato);
              } catch (ConflittoAppuntamentiException mess) {
                  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mess.getMessage());
              } catch (GiornoException e) {
                  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
              } catch (OrarioPassatoException er) {
                  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(er.getMessage());
              }
          } catch (SundayException ms) {
              return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ms.getMessage());
          } catch (MondayException ex) {
              return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
          } catch (OrarioException mx) {
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mx.getMessage());
          }
          return ResponseEntity.status(HttpStatus.CREATED).body(appuntamentoCreato);
      } catch (RuntimeException e) {
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
      }
    }

    @GetMapping("/searchappuntamenti")
    public ResponseEntity<List<AppuntamentoDTO>> getAppuntamentiInSpecificyData(@RequestParam(defaultValue = "2025-01-01") LocalDate data){
        List<AppuntamentoDTO> appuntamenti = appuntamentoService.findAppuntamentiByData(data);
        return ResponseEntity.ok(appuntamenti);
    }



    @GetMapping("/search")
    public ResponseEntity<List<AppuntamentoDTO>> getAppuntamentoByDataBetween(@RequestParam(defaultValue = "2025-01-01") LocalDate inizio,
                                                                              @RequestParam(defaultValue = "2025-01-01") LocalDate fine){
        List<AppuntamentoDTO> appuntamenti = appuntamentoService.findAppuntamentoByDataBetween(inizio,fine);
        return ResponseEntity.ok(appuntamenti);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppuntamentoDTO> getAppuntamentoById(@PathVariable Long id){
        return ResponseEntity.ok(appuntamentoService.getAppuntamentoById(id));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppuntamentoById(@PathVariable Long id){
        appuntamentoService.deleteAppuntamento(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}")
    public ResponseEntity<AppuntamentoDTO> updateAppuntamento(@PathVariable Long id, @RequestBody AppuntamentoDTO appuntamentoDTO){
       return ResponseEntity.ok(appuntamentoService.updateAppuntamento(id,appuntamentoDTO));
    }
    @GetMapping("/searchbytrattamento")
    public ResponseEntity<List<AppuntamentoDTO>> getAppuntamentiByTrattamento(@RequestParam(defaultValue = "1") Long idtrattamento){
        return ResponseEntity.ok(appuntamentoService.findAllByTrattamento(idtrattamento));
    }
    @GetMapping("/barber/appuntamenti")
    public ResponseEntity<List<AppuntamentoDTO>> getAllAppointmentsForAdmin(){
        List<AppuntamentoDTO> appuntamenti = appuntamentoService.getAppointmentsWithDetails();
        return ResponseEntity.ok(appuntamenti);
    }


    @GetMapping("/orariodisponibile")
    public List<String> getOrariDisponibili(@RequestParam("data") String data) {
        LocalDate selectedDate = LocalDate.parse(data); // Converte la data passata come stringa a LocalDate

        // Recupera gli appuntamenti del giorno dal database
        List<Appuntamento> appuntamentiDelGiorno = appuntamentoRepository.findByData(selectedDate);

        // Ottieni gli orari disponibili per la data specificata
        List<String> orariDisponibili = appuntamentoService.getOrariDisponibili(selectedDate, appuntamentiDelGiorno);

        return orariDisponibili; // Restituisce la lista degli orari disponibili
    }



    @GetMapping("/mieiappuntamenti")
    public ResponseEntity<List<Appuntamento>> getMieiAppuntamenti(@RequestHeader("Authorization") String token) {
        try {
            String username = jwtUtils.getUsernameFromToken(token.substring(7)); // Rimuove "Bearer " dal token
            List<Appuntamento> appuntamenti = appuntamentoService.getAppuntamentiByUsername(username);
            return ResponseEntity.ok(appuntamenti);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // Aggiungi null per evitare risposte errate
        }
    }

}
