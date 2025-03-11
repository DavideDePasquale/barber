package com.barber.service;

import com.barber.model.Trattamento;
import com.barber.repository.TrattamentoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class TrattamentoService {



    @Autowired
    TrattamentoRepository trattamentoRepository;


    public List<Trattamento> getAllTrattamenti(){
        List<Trattamento> listaTrattamenti = trattamentoRepository.findAll();
        return listaTrattamenti;
    }
}
