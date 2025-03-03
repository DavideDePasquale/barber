package com.barber.service;

import com.barber.repository.AppuntamentoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AppuntamentoService {

    @Autowired AppuntamentoRepository appuntamentoRepository;
}
