package com.barber.payload.mapper;

import com.barber.model.Appuntamento;
import com.barber.model.Trattamento;
import com.barber.model.Utente;
import com.barber.payload.AppuntamentoDTO;
import com.barber.payload.UtenteDTO;
import com.barber.repository.TrattamentoRepository;
import com.barber.repository.UtenteRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Data
@Component
public class AppuntamentoMapperDTO {

    @Autowired UtenteRepository utenteRepository;
    @Autowired TrattamentoRepository trattamentoRepository;

    public AppuntamentoDTO to_dto(Appuntamento entity){
        AppuntamentoDTO dto = new AppuntamentoDTO();
        dto.setId(entity.getId());
        dto.setData(entity.getData());
        dto.setOraappuntamento(entity.getOraappuntamento());
        Long idtrattamento = entity.getTrattamento().getId();
        dto.setId_trattamento(idtrattamento);
        Long idutente = entity.getUtente().getId();
        dto.setId_utente(idutente);
        return dto;
    }


    public Appuntamento to_entity(AppuntamentoDTO dto){
        Appuntamento entity = new Appuntamento();
        entity.setData(dto.getData());
        entity.setOraappuntamento(dto.getOraappuntamento());
        entity.setTrattamento(trattamentoRepository.findById(dto.getId_trattamento()).orElseThrow(()-> new RuntimeException("⚠️ Trattaamento non trovato ⚠️")));
        entity.setUtente(utenteRepository.findById(dto.getId_utente()).orElseThrow(()-> new RuntimeException("⚠️ Utente non trovato ⚠️")));
        return entity;
    }

    public Appuntamento updateAppuntamento(Appuntamento appuntamento, AppuntamentoDTO appuntamentoDTO){
        if (appuntamentoDTO.getData() != null){
            appuntamento.setData(appuntamentoDTO.getData());
        }
        if (appuntamentoDTO.getOraappuntamento() != null){
            appuntamento.setOraappuntamento(appuntamentoDTO.getOraappuntamento());
        }
        if (appuntamentoDTO.getId_trattamento() != null){
            Trattamento trattamento = trattamentoRepository.findById(appuntamentoDTO.getId_trattamento()).orElseThrow(()-> new RuntimeException("❌ Trattamento non trovato! ❌"));
            appuntamento.setTrattamento(trattamento);
        }
        if (appuntamentoDTO.getId_utente() != null){
            Utente utente = utenteRepository.findById(appuntamentoDTO.getId_utente()).orElseThrow(()-> new RuntimeException("❌ Utente non trovato! ❌"));
            appuntamento.setUtente(utente);
        }
        return appuntamento;
    }
}
