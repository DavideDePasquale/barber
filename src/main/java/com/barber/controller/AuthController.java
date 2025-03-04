package com.barber.controller;

import com.barber.payload.UtenteDTO;
import com.barber.payload.request.LoginRequest;
import com.barber.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired UtenteService utenteService;

    @PostMapping("/register")
    public ResponseEntity<UtenteDTO> registerUtente(@RequestBody UtenteDTO utenteDTO){
        UtenteDTO dto = utenteService.registerUtente(utenteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
//        System.out.println("🔥 Login request received: " + loginRequest.getUsername());
//        return
//    }


}
