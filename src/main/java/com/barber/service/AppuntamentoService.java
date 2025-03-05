package com.barber.service;

import com.barber.exception.ConflittoAppuntamentiException;
import com.barber.exception.MondayException;
import com.barber.exception.OrarioException;
import com.barber.exception.SundayException;
import com.barber.model.Appuntamento;
import com.barber.model.Trattamento;
import com.barber.model.Utente;
import com.barber.payload.AppuntamentoDTO;
import com.barber.payload.mapper.AppuntamentoMapperDTO;
import com.barber.repository.AppuntamentoRepository;
import com.barber.repository.UtenteRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
public class AppuntamentoService {

    @Autowired AppuntamentoRepository appuntamentoRepository;
    @Autowired UtenteRepository utenteRepository;
    @Autowired AppuntamentoMapperDTO appuntamentoMapperDTO;

    public AppuntamentoDTO createAppuntamento(AppuntamentoDTO appuntamentoDTO, Long id_utente) throws SundayException, MondayException, OrarioException, ConflittoAppuntamentiException {
        Utente utente = utenteRepository.findById(id_utente)
                .orElseThrow(() -> new RuntimeException("❌ Utente non trovato! ❌"));

        Appuntamento appuntamento = appuntamentoMapperDTO.to_entity(appuntamentoDTO);

        // Recupero tutti gli appuntamenti nella stessa data
        List<Appuntamento> appuntamentiDelGiorno = appuntamentoRepository.findByData(appuntamento.getData());
        //faccio un controllo sul giorno della settimana : se è domenica non si può prenotare/idem lunedi
        if (appuntamento.getData().getDayOfWeek() == DayOfWeek.SUNDAY){
            throw new SundayException("❌ Non è possibile prenotare la domenica! ❌ ");
        }
        if (appuntamento.getData().getDayOfWeek() == DayOfWeek.MONDAY){
            throw new MondayException("❌ Il salone è chiuso il Lunedì! ❌ ");
        }
        //definisco gli orari di chiusura e apertura del locale cosi poi da metterci qualche controllo
        LocalTime orachiusuraSera = LocalTime.of(19,0);
        LocalTime oraChiusuraPranzo = LocalTime.of(13,30);
        LocalTime oraAperturaPomeridiana = LocalTime.of(15,0);
        LocalTime oraAperturaMattina = LocalTime.of(8,0);
        // controllo sull'orario di prenotazione
        if (appuntamento.getOraappuntamento().isBefore(oraAperturaMattina) && appuntamento.getOraappuntamento().isAfter(orachiusuraSera)
        || appuntamento.getOraFineAppuntamento().isAfter(oraChiusuraPranzo) && appuntamento.getOraFineAppuntamento().isBefore(oraAperturaPomeridiana)
        ){
            throw new OrarioException("❌ Prenotazione non valida! Il salone è chiuso a quell'ora! ❌");
        }
        // Calcolo ora inizio e fine del nuovo appuntamento
        LocalTime oraInizio = appuntamento.getOraappuntamento();
        LocalTime oraFine = oraInizio.plusMinutes(appuntamento.getTrattamento().getDurataMinuti());
        // Controllo sovrapposizione con gli appuntamenti esistenti
        for (Appuntamento esistente : appuntamentiDelGiorno) {
            LocalTime esistenteInizio = esistente.getOraappuntamento();
            LocalTime esistenteFine = esistenteInizio.plusMinutes(esistente.getTrattamento().getDurataMinuti());

            boolean sovrapposto = !(oraFine.isBefore(esistenteInizio) || oraInizio.isAfter(esistenteFine));

            if (sovrapposto) {
                throw new ConflittoAppuntamentiException("❌ Orario non disponibile! Scegli un altro orario. ❌");
            }
        }

        // Se non ci sono sovrapposizioni, salvo l'appuntamento
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
