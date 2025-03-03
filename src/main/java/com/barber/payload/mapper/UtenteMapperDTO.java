package com.barber.payload.mapper;

import com.barber.enumeration.ERuolo;
import com.barber.model.Utente;
import com.barber.payload.UtenteDTO;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class UtenteMapperDTO {


    public UtenteDTO to_dto(Utente entity){
        UtenteDTO dto = new UtenteDTO();
        dto.setNome(entity.getNome());
        dto.setCognome(entity.getCognome());
        dto.setEmail(entity.getEmail());
        dto.setUsername(entity.getUsername());
        dto.setPassword(entity.getPassword());
        dto.setAvatar(entity.getAvatar());
        dto.setTipoRuolo(entity.getTipoRuolo().name());
        return dto;
    }


    public Utente to_entity(UtenteDTO dto){
        Utente entity = new Utente();
        entity.setNome(dto.getNome());
        entity.setCognome(dto.getCognome());
        entity.setEmail(dto.getEmail());
        entity.setUsername(dto.getUsername());
        entity.setPassword(dto.getPassword());
        ERuolo ruolo = ERuolo.valueOf(dto.getTipoRuolo().toUpperCase());
        entity.setAvatar(dto.getAvatar());
        entity.setTipoRuolo(ruolo);
        return entity;
    }


    // x l'update
    public Utente updateUtente(UtenteDTO utenteDTO, Utente utente){
        if (utenteDTO.getAvatar() != null){
            utente.setAvatar(utenteDTO.getAvatar());
        }
        if (utenteDTO.getCognome() != null){
            utente.setCognome(utenteDTO.getCognome());
        }
        if (utenteDTO.getNome() != null){
            utente.setNome(utenteDTO.getNome());
        }
        if (utenteDTO.getEmail() != null){
            utente.setEmail(utenteDTO.getEmail());
        }
        if (utenteDTO.getUsername() != null){
            utente.setUsername(utenteDTO.getUsername());
        }
        if (utenteDTO.getPassword() != null){
            utente.setPassword(utenteDTO.getPassword());
        }
        if (utenteDTO.getTipoRuolo() != null){
            utente.setTipoRuolo(ERuolo.valueOf(utenteDTO.getTipoRuolo().toUpperCase()));
        }
        return utente;

    }
}
