package com.barber.payload;

import com.barber.model.Trattamento;
import com.barber.model.Utente;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.sql.Time;
import java.time.LocalDate;

@Data
public class AppuntamentoDTO {


    private LocalDate data;

    private Time oraappuntamento;

    private Long id_trattamento;

    private Long id_utente;

}
