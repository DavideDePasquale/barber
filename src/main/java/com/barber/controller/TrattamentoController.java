package com.barber.controller;

import com.barber.model.Trattamento;
import com.barber.service.TrattamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/trattamenti")
public class TrattamentoController {
    @Autowired
    TrattamentoService trattamentoService;



    @GetMapping("/all")
    public ResponseEntity<List<Trattamento>> getAllTrattamenti(){
        return ResponseEntity.ok(trattamentoService.getAllTrattamenti());

    }
}
