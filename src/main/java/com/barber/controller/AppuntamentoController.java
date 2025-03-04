package com.barber.controller;

import com.barber.model.Appuntamento;
import com.barber.payload.AppuntamentoDTO;
import com.barber.service.AppuntamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/appuntamento")
public class AppuntamentoController {


    @Autowired AppuntamentoService appuntamentoService;


    @PostMapping("/nuovoappuntamento")
    public ResponseEntity<AppuntamentoDTO> createAppuntamento(@RequestBody @Validated AppuntamentoDTO appuntamentoDTO){
      try {
          AppuntamentoDTO dto = appuntamentoService.createAppuntamento(appuntamentoDTO, appuntamentoDTO.getId_utente());
          return ResponseEntity.status(HttpStatus.CREATED).body(dto);
      } catch (RuntimeException e) {
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
      }
    }

    @GetMapping("/searchappuntamenti")
    public ResponseEntity<List<AppuntamentoDTO>> getAppuntamentiInSpecificyData(@RequestParam(defaultValue = "2025-01-01") LocalDate data){
        List<AppuntamentoDTO> appuntamenti = appuntamentoService.findAppuntamentiByData(data);
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

}
