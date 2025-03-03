package com.barber.repository;

import com.barber.model.Appuntamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AppuntamentoRepository extends JpaRepository<Appuntamento,Long> {
    List<Appuntamento> findByDataBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Appuntamento> findByData(LocalDate data);

}
