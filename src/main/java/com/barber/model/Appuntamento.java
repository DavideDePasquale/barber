package com.barber.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "appuntamenti")
public class Appuntamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private LocalDate data;
    @Column(nullable = false)
    private LocalTime oraappuntamento;
    @ManyToOne
    @JoinColumn(name = "id_trattammento")
    private Trattamento trattamento;
    @ManyToOne
    @JoinColumn(name = "id_utente")
    private Utente utente;

    public LocalTime getOraFineAppuntamento(){
        return oraappuntamento.plusMinutes(trattamento.getDurataMinuti());
    }

}
