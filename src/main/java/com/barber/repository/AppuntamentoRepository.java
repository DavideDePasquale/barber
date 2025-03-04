package com.barber.repository;

import com.barber.model.Appuntamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AppuntamentoRepository extends JpaRepository<Appuntamento,Long> {
    List<Appuntamento> findByDataBetween(LocalDate startDate, LocalDate endDate);
    List<Appuntamento> findByData(LocalDate data);

    List<Appuntamento> findAllByTrattamento_Id(Long trattamentoId);

    boolean existsByDataAndOraappuntamento(LocalDate data,Time oraappuntamento);


}
