package com.barber.service;

import com.barber.configuration.CloudinaryConfig;
import com.barber.exception.DuplicateEmailException;
import com.barber.exception.DuplicateUsernameException;
import com.barber.model.Utente;
import com.barber.payload.UtenteDTO;
import com.barber.payload.mapper.UtenteMapperDTO;
import com.barber.repository.UtenteRepository;
import com.barber.security.JwtUtils;
import com.cloudinary.utils.ObjectUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UtenteService {

    @Autowired UtenteRepository utenteRepository;
    @Autowired UtenteMapperDTO utenteMapperDTO;
    @Autowired CloudinaryConfig cloudinaryConfig;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired JwtUtils jwtUtils;



    // CRUD BASE
    public UtenteDTO registerUtente(UtenteDTO utenteDTO) throws DuplicateEmailException, DuplicateUsernameException {
        if (utenteRepository.existsByEmail(utenteDTO.getEmail())){
            throw new DuplicateEmailException("⚠️ Email già in uso! ⚠️");
        }
        if (utenteRepository.existsByUsername(utenteDTO.getUsername())){
            throw new DuplicateUsernameException("⚠️ Username già in uso! ⚠️");
        }
        if (utenteDTO.getTipoRuolo() == null){
            utenteDTO.setTipoRuolo("USER");

        }


        Utente utente = utenteMapperDTO.to_entity(utenteDTO);
        utente.setPassword(passwordEncoder.encode(utenteDTO.getPassword()));
        utente = utenteRepository.save(utente);
        return utenteMapperDTO.to_dto(utente);
    }

    public UtenteDTO getUtenteById(Long id){
        Utente utente = utenteRepository.findById(id).orElseThrow(()-> new RuntimeException("⚠️ Utente non trovato! ⚠️"));
        return utenteMapperDTO.to_dto(utente);
    }
    public List<UtenteDTO> getAllUtenti(){
        List<Utente> utenti = utenteRepository.findAll();
        return utenti.stream().map(utenteMapperDTO::to_dto).collect(Collectors.toList());
    }

    public void deleteUtente(Long id){
        Utente utente = utenteRepository.findById(id).orElseThrow(()-> new RuntimeException("⚠️ Utente non trovato! ⚠️"));
        utenteRepository.delete(utente);
    }




//    public Map<String, String> updateUtente(Long id, UtenteDTO utenteDTO) {
//        Utente utente = utenteRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("⚠️ Utente non trovato! ⚠️"));
//
//        // Mantieni la password se non viene modificata
//        String oldPassword = utente.getPassword();
//        utente = utenteMapperDTO.updateUtente(utenteDTO, utente);
//
//        if (utenteDTO.getPassword() == null || utenteDTO.getPassword().isEmpty()) {
//            utente.setPassword(oldPassword);
//        } else {
//            utente.setPassword(passwordEncoder.encode(utenteDTO.getPassword()));
//        }
//
//        utente = utenteRepository.save(utente);
//
//        // Genera un nuovo token aggiornato
//        List<String> roles = List.of(utente.getTipoRuolo().name());
//        String newToken = jwtUtils.generateToken(utente.getUsername(), utente.getId(), roles);
//
//        // Restituisci il nuovo token al frontend
//        Map<String, String> response = new HashMap<>();
//        response.put("token", newToken);
//
//        return response;
//    }



    public Map<String, String> updateUtente(Long id, UtenteDTO utenteDTO) throws DuplicateEmailException, DuplicateUsernameException {
        Utente utente = utenteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("⚠️ Utente non trovato! ⚠️"));

        if (!utente.getEmail().equals(utenteDTO.getEmail())){
            Optional<Utente> utenteEsistente = utenteRepository.findByEmail(utenteDTO.getEmail());
            if (utenteEsistente.isPresent()){
                throw new DuplicateEmailException("⚠️ Email già in uso! ⚠️");
            }
        }
        if (!utente.getUsername().equals(utenteDTO.getUsername())){
            Optional<Utente> utenteEsist = utenteRepository.findByUsername(utenteDTO.getUsername());
            if (utenteEsist.isPresent()){
                throw new DuplicateUsernameException("⚠️ Username già in uso! ⚠️");
            }
        }

        // Mantieni la vecchia password se non viene modificata
        String oldPassword = utente.getPassword();

        // Gestisci la password PRIMA dell'update per evitare sovrascritture
        boolean isPasswordUpdated = utenteDTO.getPassword() != null && !utenteDTO.getPassword().isEmpty();

        if (isPasswordUpdated) {
            utente.setPassword(passwordEncoder.encode(utenteDTO.getPassword()));
        }

        // Ora aggiorniamo i dati dell'utente senza toccare la password
        utente = utenteMapperDTO.updateUtente(utenteDTO, utente);

        // Se la password NON è stata aggiornata, ripristiniamo quella vecchia
        if (!isPasswordUpdated) {
            utente.setPassword(oldPassword);
        }

        // Salva l'utente aggiornato nel database
        utente = utenteRepository.save(utente);

        // Genera un nuovo token aggiornato
        // Ruolo deve essere passato come lista
        List<String> roles = utente.getTipoRuolo() != null ? List.of(utente.getTipoRuolo().name()) : List.of("USER");
        String newToken = jwtUtils.generateToken(utente.getUsername(), utente.getId(), roles);

        // Restituisci il nuovo token al frontend
        Map<String, String> response = new HashMap<>();
        response.put("token", newToken);

        return response;
    }









    // upload avatar
    public UtenteDTO uploadImage(UtenteDTO utenteDTO, MultipartFile file) throws IOException {
        Map uploadResult = cloudinaryConfig.uploader().uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        String imageUrl = (String) uploadResult.get("url");
        utenteDTO.setAvatar(imageUrl);
        Utente utente = utenteMapperDTO.to_entity(utenteDTO);
        utente = utenteRepository.save(utente);
        return utenteMapperDTO.to_dto(utente);
    }





    /// ////////
    public UtenteDTO registerUtente(UtenteDTO utenteDTO, MultipartFile file) throws IOException, DuplicateEmailException {
        // Controlla se la email è già in uso
        if (utenteRepository.existsByEmail(utenteDTO.getEmail())) {
            throw new DuplicateEmailException("⚠️ Email già in uso! ⚠️");
        }

        // Controlla se lo username è già in uso
        if (utenteRepository.existsByUsername(utenteDTO.getUsername())) {
            throw new DuplicateEmailException("⚠️ Username già in uso! ⚠️");
        }

        // Se non viene fornito un tipo di ruolo, imposta 'USER' come predefinito
        if (utenteDTO.getTipoRuolo() == null) {
            utenteDTO.setTipoRuolo("USER");
        }

        // Gestione dell'avatar
        if (file != null && !file.isEmpty()) {
            // Carica l'immagine su Cloudinary (o altro servizio)
            Map uploadResult = cloudinaryConfig.uploader().uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String imageUrl = (String) uploadResult.get("url");
            utenteDTO.setAvatar(imageUrl);  // Imposta l'avatar
        } else {
            utenteDTO.setAvatar(null);  // Se non c'è un avatar, imposta null
        }

        // Mappa il DTO a un'entità
        Utente utente = utenteMapperDTO.to_entity(utenteDTO);

        // Cripta la password prima di salvarla nel database
        utente.setPassword(passwordEncoder.encode(utenteDTO.getPassword()));

        // Salva l'utente nel database
        utente = utenteRepository.save(utente);

        // Restituisci il DTO dell'utente appena registrato
        return utenteMapperDTO.to_dto(utente);
    }



}
