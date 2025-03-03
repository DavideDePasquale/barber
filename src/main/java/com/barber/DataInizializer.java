package com.barber;

import com.barber.enumeration.ETrattamento;
import com.barber.model.Trattamento;
import com.barber.repository.TrattamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInizializer implements CommandLineRunner {

    @Autowired
    private TrattamentoRepository trattamentoRepository;

    @Override
    public void run(String... args) throws Exception {
        if (trattamentoRepository.count() == 0){
            Trattamento barba = new Trattamento();
            barba.setTipotrattamento(ETrattamento.TAGLIO_BARBA);
            barba.setPrezzo(8.00);
            trattamentoRepository.save(barba);
            Trattamento capelli = new Trattamento();
            capelli.setTipotrattamento(ETrattamento.TAGLIO_CAPELLI);
            capelli.setPrezzo(14.00);
            trattamentoRepository.save(capelli);
            Trattamento combo = new Trattamento();
            combo.setTipotrattamento(ETrattamento.COMBO);
            combo.setPrezzo(20.00);
            trattamentoRepository.save(combo);
        }
    }
}
