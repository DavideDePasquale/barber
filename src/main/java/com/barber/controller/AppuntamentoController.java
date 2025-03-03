package com.barber.controller;

import com.barber.service.AppuntamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/appuntamento")
public class AppuntamentoController {


    @Autowired AppuntamentoService appuntamentoService;

}
