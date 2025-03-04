package com.barber.service;

import com.barber.configuration.CloudinaryConfig;
import com.barber.model.Utente;
import com.barber.payload.UtenteDTO;
import com.barber.payload.mapper.UtenteMapperDTO;
import com.barber.repository.UtenteRepository;
import com.cloudinary.utils.ObjectUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class UtenteService {

    @Autowired UtenteRepository utenteRepository;
    @Autowired UtenteMapperDTO utenteMapperDTO;
    @Autowired CloudinaryConfig cloudinaryConfig;



    // CRUD BASE
    public UtenteDTO registerUtente(UtenteDTO utenteDTO){
        if (utenteRepository.existsByEmail(utenteDTO.getEmail())){
            throw new RuntimeException("⚠️ Email già in uso! ⚠️");
        }
        if (utenteRepository.existsByUsername(utenteDTO.getUsername())){
            throw new RuntimeException("⚠️ Username già in uso! ⚠️");
        }
        if (utenteDTO.getTipoRuolo() == null){
            utenteDTO.setTipoRuolo("USER");

        }
        Utente utente = utenteMapperDTO.to_entity(utenteDTO);
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

    // x la modifica di un utente
    public UtenteDTO updateUtente(Long id, UtenteDTO utenteDTO){
        Utente utente =
                utenteRepository.findById(id).orElseThrow(()-> new RuntimeException("⚠️ Utente non trovato! ⚠️"));
        utente = utenteMapperDTO.updateUtente(utenteDTO,utente);
        utente = utenteRepository.save(utente);
        return utenteMapperDTO.to_dto(utente);
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


}
