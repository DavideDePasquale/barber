package com.barber.repository;

import com.barber.enumeration.ETrattamento;
import com.barber.model.Trattamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrattamentoRepository extends JpaRepository<Trattamento,Long> {

    Optional<Trattamento> findByTipotrattamento(ETrattamento tipotrattamento);

}
