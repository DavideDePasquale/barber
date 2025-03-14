package com.barber.service;

import com.barber.exception.*;
import com.barber.model.Appuntamento;
import com.barber.model.Trattamento;
import com.barber.model.Utente;
import com.barber.payload.AppuntamentoDTO;
import com.barber.payload.AppuntamentoDTOnoID;
import com.barber.payload.mapper.AppuntamentoMapperDTO;
import com.barber.repository.AppuntamentoRepository;
import com.barber.repository.TrattamentoRepository;
import com.barber.repository.UtenteRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AppuntamentoService {

    @Autowired AppuntamentoRepository appuntamentoRepository;
    @Autowired UtenteRepository utenteRepository;
    @Autowired TrattamentoRepository trattamentoRepository;
    @Autowired AppuntamentoMapperDTO appuntamentoMapperDTO;
    @Autowired EmailService emailService;

    public AppuntamentoDTO createAppuntamento(AppuntamentoDTOnoID appuntamentoDTOnoID, Long userId) throws SundayException, MondayException, OrarioException, ConflittoAppuntamentiException, GiornoException, OrarioPassatoException {

        Utente utente = utenteRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("❌ Utente non trovato! ❌"));

        Appuntamento appuntamento = appuntamentoMapperDTO.fromNoiDDTOto_entity(appuntamentoDTOnoID);

        if (appuntamento.getTrattamento() == null){
            throw new RuntimeException("❌ Trattamento non valido! ❌");
        }

        // Gestione corretta della data con il fuso orario
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("Europe/Rome"));

// Converte il ZonedDateTime in LocalDate (senza fuso orario)
        LocalDate localDate = zonedDateTime.toLocalDate();

// Log della data finale
        System.out.println("Data finale: " + localDate);

        System.out.println("Data appuntamento con fuso orario: " + zonedDateTime); // Debug per vedere la data con fuso orario

        // Controllo sul giorno della prenotazione: non deve essere possibile prenotare per un giorno già vissuto
        if (appuntamento.getData().isBefore(LocalDate.now())){
            throw new IllegalArgumentException("❌ Non puoi prenotare per un giorno passato! ❌");
        }

        // Controllo sull'appuntamento che non deve essere oltre i 6 mesi da oggi
        if (appuntamento.getData().isAfter(LocalDate.now().plusMonths(6))){
            throw new IllegalArgumentException("❌ Non puoi prenotare oltre i 6 mesi da oggi! ❌");
        }

        // Recupero tutti gli appuntamenti nella stessa data
        List<Appuntamento> appuntamentiDelGiorno = appuntamentoRepository.findByData(appuntamento.getData());

        // Controllo sul giorno della settimana: se è domenica o lunedì, non si può prenotare
        if (appuntamento.getData().getDayOfWeek() == DayOfWeek.SUNDAY){
            throw new SundayException("❌ Non è possibile prenotare la domenica! ❌ ");
        }
        if (appuntamento.getData().getDayOfWeek() == DayOfWeek.MONDAY){
            throw new MondayException("❌ Il salone è chiuso il Lunedì! ❌ ");
        }

        // Definizione orari di apertura/chiusura
        LocalTime orachiusuraSera = LocalTime.of(19,0);
        LocalTime oraChiusuraPranzo = LocalTime.of(13,30);
        LocalTime oraAperturaPomeridiana = LocalTime.of(15,0);
        LocalTime oraAperturaMattina = LocalTime.of(8,0);
        LocalDate giornoCorrente = LocalDate.now();
        LocalTime orarioCorrente = LocalTime.now(ZoneId.of("Europe/Rome"));

        // Controllo sull'orario di prenotazione
        if (appuntamento.getData().isBefore(giornoCorrente)){
            throw new GiornoException("❌ Non puoi prenotare per un giorno già passato! ❌");
        }
        if (appuntamento.getData().equals(giornoCorrente) && appuntamento.getOraappuntamento().isBefore(orarioCorrente)){
            throw new OrarioPassatoException("❌ Non puoi prenotare per un orario già passato nella giornata! ❌");
        }

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

        // Invio email di conferma
        String subject = "📍Conferma Prenotazione";
        String body = "👋 Ciao " + utente.getNome() + ",\n\nLa tua prenotazione per il " + appuntamento.getData()
                + " alle " + appuntamento.getOraappuntamento() + " è confermata!\n\nTi aspettiamo!\n\n Ricordati sempre di arrivare 5 minuti prima della prenotazione!🕰️" +
                "\n\n BarberApp, l'App che ti da un taglio ✂️ ️";

        emailService.inviaMail(utente.getEmail(), subject, body);

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
    //metodo che mi restituisce gli appuntamenti tramite username
    public List<Appuntamento> getAppuntamentiByUsername(String username){
        return appuntamentoRepository.findByUtenteUsername(username);
    }
    //metodo che mi restituisce tutti gli appuntamenti!
    public List<AppuntamentoDTO> getAllAppointmentsForAdmin(){
        List<Appuntamento> appuntamenti = appuntamentoRepository.findAll();
        return appuntamenti.stream().map(appuntamentoMapperDTO::to_dto).collect(Collectors.toList());
    }




    //
    public List<AppuntamentoDTO> getAppointmentsWithDetails() {
        List<Appuntamento> appuntamenti = appuntamentoRepository.findAll(); // O la query che usi per recuperare gli appuntamenti
        List<AppuntamentoDTO> appuntamentiDTO = new ArrayList<>();

        for (Appuntamento appuntamento : appuntamenti) {
            // Recupera i dettagli dell'utente tramite l'ID
            Utente utente = utenteRepository.findById(appuntamento.getUtente().getId()).orElse(null);
            // Recupera i dettagli del trattamento tramite l'ID
            Trattamento trattamento = trattamentoRepository.findById(appuntamento.getTrattamento().getId()).orElse(null);

            // Aggiungi i dettagli nell'AppuntamentoDTO
            if (utente != null && trattamento != null) {
                AppuntamentoDTO dto = new AppuntamentoDTO();
                dto.setId(appuntamento.getId());
                dto.setData(appuntamento.getData());
                dto.setOraappuntamento(appuntamento.getOraappuntamento());
                dto.setUtenteNome(utente.getNome() + " " + utente.getCognome());
                dto.setTrattamentoNome(String.valueOf(trattamento.getTipotrattamento()));

                appuntamentiDTO.add(dto);
            }
        }
        return appuntamentiDTO;
    }










    //metodo che mi serve per estrapolarmi gli orari disponibili da visualizzare per il barbiere!
    public List<Appuntamento> getAppuntamentoByData(LocalDate data){
        return appuntamentoRepository.findByData(data);
    }



    // qui mi sto creando le basi per il calendario che vorrei fare nel front
    public List<String> getOrariDisponibili(LocalDate data, List<Appuntamento> appuntamentiDelGiorno) {
        LocalTime orachiusuraSera = LocalTime.of(19, 0);
        LocalTime oraChiusuraPranzo = LocalTime.of(13, 19);
        LocalTime oraAperturaPomeridiana = LocalTime.of(15, 0);
        LocalTime oraAperturaMattina = LocalTime.of(8, 0);
        List<String> listaOrari = new ArrayList<>();
        LocalTime currentTime = oraAperturaMattina;

        while (currentTime.plusMinutes(39).isBefore(orachiusuraSera)) {
            if (currentTime.isBefore(oraChiusuraPranzo)
                    || currentTime.plusMinutes(39).isBefore(oraChiusuraPranzo)
                    || currentTime.isAfter(oraAperturaPomeridiana)) {
                String formattedTime = currentTime.format(DateTimeFormatter.ofPattern("HH:mm"));

                final LocalTime finalTimeSlot = currentTime;
                // Verifica dell'orario se è già stato preso
                boolean isTaken = appuntamentiDelGiorno.stream()
                        .anyMatch(appuntamento -> isOrarioSovrapposto(appuntamento, finalTimeSlot));
                if (!isTaken) {
                    listaOrari.add(formattedTime);
                }
            }
            // Aumento di 40 minuti per il prossimo orario!
            currentTime = currentTime.plusMinutes(40);
        }
        return listaOrari;
    }


    private boolean isOrarioSovrapposto(Appuntamento appuntamento, LocalTime currentTime) {
        LocalTime appuntamentoInizio = appuntamento.getOraappuntamento();
        int durataTrattamento = appuntamento.getTrattamento().getDurataMinuti();
        LocalTime appuntamentoFine = appuntamentoInizio.plusMinutes(durataTrattamento);

        return !(currentTime.plusMinutes(39).isBefore(appuntamentoInizio) || currentTime.isAfter(appuntamentoFine));
    }



}
