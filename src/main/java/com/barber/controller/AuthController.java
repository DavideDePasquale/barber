package com.barber.controller;

import com.barber.exception.DuplicateEmailException;
import com.barber.exception.DuplicateUsernameException;
import com.barber.payload.UtenteDTO;
import com.barber.payload.request.LoginRequest;
import com.barber.security.AuthService;
import com.barber.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired UtenteService utenteService;
    @Autowired AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UtenteDTO> registerUtente(@RequestBody UtenteDTO utenteDTO) throws DuplicateEmailException, DuplicateUsernameException {
        UtenteDTO dto = utenteService.registerUtente(utenteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        System.out.println("🔥 Login request received: " + loginRequest.getUsername());
        return authService.authenticateUtente(loginRequest);
    }

    @PostMapping(value = "/avatar",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UtenteDTO> createUtenteWithAvatar(@RequestPart("utente") UtenteDTO utenteDTO, @RequestPart("file") MultipartFile file){
        try {
            UtenteDTO savedUser = utenteService.uploadImage(utenteDTO,file);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /// ///   per la ricezione degli errori nel front!
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<String> handleDuplicateEmail(DuplicateEmailException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<String> handleDuplicateUsername(DuplicateUsernameException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }


}
