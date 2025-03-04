package com.barber.service;

import com.barber.model.Appuntamento;
import com.barber.model.Utente;
import com.barber.payload.AppuntamentoDTO;
import com.barber.payload.mapper.AppuntamentoMapperDTO;
import com.barber.repository.AppuntamentoRepository;
import com.barber.repository.UtenteRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AppuntamentoService {

    @Autowired AppuntamentoRepository appuntamentoRepository;
    @Autowired UtenteRepository utenteRepository;
    @Autowired AppuntamentoMapperDTO appuntamentoMapperDTO;

    public AppuntamentoDTO createAppuntamento(AppuntamentoDTO appuntamentoDTO, Long id_utente){
        Utente utente = utenteRepository.findById(id_utente).orElseThrow(()-> new RuntimeException("❌ Utente non trovato! ❌"));

        Appuntamento appuntamento = appuntamentoMapperDTO.to_entity(appuntamentoDTO);

        boolean existAppointment = appuntamentoRepository.existsByDataAndOraappuntamento(appuntamento.getData(),appuntamento.getOraappuntamento());

        if (existAppointment){
            throw new RuntimeException("❌ L'orario " + appuntamento.getOraappuntamento() + " nel giorno " + appuntamento.getData() + " è già occupato! ❌");
        }

        appuntamento.setUtente(utente);
        appuntamento = appuntamentoRepository.save(appuntamento);
        return appuntamentoMapperDTO.to_dto(appuntamento);
    }

    public AppuntamentoDTO getAppuntamentoById(Long id){
        Appuntamento appuntamento = appuntamentoRepository.findById(id).orElseThrow(() -> new RuntimeException("❌ Appuntamento non trovato! ❌"));
        return appuntamentoMapperDTO.to_dto(appuntamento);
    }

    public void deleteAppuntamento(Long id){
        if(!appuntamentoRepository.existsById(id)){
            throw new EntityNotFoundException("❌ Appuntamento non trovato! ❌");
        }
        appuntamentoRepository.deleteById(id);
    }

    public AppuntamentoDTO updateAppuntamento(Long id, AppuntamentoDTO appuntamentoDTO){
        Appuntamento appuntamento = appuntamentoRepository.findById(id).orElseThrow(()-> new RuntimeException("❌ Appuntamento non trovato! ❌"));
      appuntamento = appuntamentoMapperDTO.updateAppuntamento(appuntamento,appuntamentoDTO);
      appuntamento = appuntamentoRepository.save(appuntamento);
       return appuntamentoMapperDTO.to_dto(appuntamento);
    }

    public List<AppuntamentoDTO> findAppuntamentoByDataBetween(LocalDate startDate, LocalDate endDate){
        List<Appuntamento> appuntamenti = appuntamentoRepository.findByDataBetween(startDate,endDate);
        return appuntamenti.stream().map(appuntamentoMapperDTO::to_dto).toList();
    }

    public List<AppuntamentoDTO> findAppuntamentiByData(LocalDate data){
       List<Appuntamento> appuntamenti =  appuntamentoRepository.findByData(data);
       return appuntamenti.stream().map(appuntamentoMapperDTO::to_dto).toList();
    }

    public List<AppuntamentoDTO> findAllByTrattamento(Long id){
        List<Appuntamento> appuntamentiPerTrattamento = appuntamentoRepository.findAllByTrattamento_Id(id);
        return appuntamentiPerTrattamento.stream().map(appuntamentoMapperDTO::to_dto).toList();
    }


}
