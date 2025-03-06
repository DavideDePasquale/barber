package com.barber.payload;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AppuntamentoDTOnoID {


    private LocalDate data;

    private LocalTime oraappuntamento;

    private Long id_trattamento;
}
