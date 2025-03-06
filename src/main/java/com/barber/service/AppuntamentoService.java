package com.barber.service;

import com.barber.exception.ConflittoAppuntamentiException;
import com.barber.exception.MondayException;
import com.barber.exception.OrarioException;
import com.barber.exception.SundayException;
import com.barber.model.Appuntamento;
import com.barber.model.Trattamento;
import com.barber.model.Utente;
import com.barber.payload.AppuntamentoDTO;
import com.barber.payload.AppuntamentoDTOnoID;
import com.barber.payload.mapper.AppuntamentoMapperDTO;
import com.barber.repository.AppuntamentoRepository;
import com.barber.repository.UtenteRepository;
import com.barber.security.UtenteDetailsImpl;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
public class AppuntamentoService {

    @Autowired AppuntamentoRepository appuntamentoRepository;
    @Autowired UtenteRepository utenteRepository;
    @Autowired AppuntamentoMapperDTO appuntamentoMapperDTO;
    @Autowired EmailService emailService;

    public AppuntamentoDTO createAppuntamento(AppuntamentoDTOnoID appuntamentoDTOnoID, Long userId) throws SundayException, MondayException, OrarioException, ConflittoAppuntamentiException {

        Utente utente = utenteRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("❌ Utente non trovato! ❌"));

        Appuntamento appuntamento = appuntamentoMapperDTO.fromNoiDDTOto_entity(appuntamentoDTOnoID);


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
        System.out.println("Utente: " + utente);
        appuntamento.setUtente(utente);
        System.out.println("Appuntamento salvato: " + appuntamento);
        appuntamento = appuntamentoRepository.save(appuntamento);

        System.out.println("Appuntamento salvatooo: " + appuntamento);


        // invio mail.
        String subject = "📍Conferma Prenotazione";
        String body = "👋 Ciao " + utente.getNome() + ",\n\nLa tua prenotazione per il " + appuntamento.getData()
                + " alle " + appuntamento.getOraappuntamento() + " è confermata!\n\nTi aspettiamo!\n\n Ricordati sempre di arrivare 5 minuti prima della prenotazione!🕰️" +
                "\n\n BarberApp, l'App che ti da un taglio ✂️ ️";

        emailService.inviaMail(utente.getEmail(),subject,body);

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

    // Metodo per recuperare l'email dell'utente
    public String getUtenteEmailById(Long userId) {
        // Logica per recuperare l'email dell'utente dal database
        // Questo è un esempio e potrebbe variare in base alla tua implementazione
        Utente utente = utenteRepository.getById(userId);
        return utente.getEmail();
    }
}
