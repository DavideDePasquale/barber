package com.barber.payload;

import com.barber.model.Trattamento;
import com.barber.model.Utente;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AppuntamentoDTO {


    private Long id;
    private LocalDate data;

    private LocalTime oraappuntamento;

    private Long id_trattamento;

    private Long id_utente;


    //
    private String utenteNome;
    private String trattamentoNome;

}
