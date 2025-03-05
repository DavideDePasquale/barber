package com.barber.repository;

import com.barber.model.Appuntamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface AppuntamentoRepository extends JpaRepository<Appuntamento,Long> {
    List<Appuntamento> findByDataBetween(LocalDate startDate, LocalDate endDate);
    List<Appuntamento> findByData(LocalDate data);

    List<Appuntamento> findAllByTrattamento_Id(Long trattamentoId);

    boolean existsByDataAndOraappuntamento(LocalDate data, LocalTime oraappuntamento);





}
