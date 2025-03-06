package com.barber.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {


    @Autowired JavaMailSender mailSender;

        public String inviaMail(String to, String subject, String body) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(to);
                message.setSubject(subject);
                message.setText(body);
                message.setFrom("tuoindirizzo@gmail.com");  // Mittente (può essere il tuo indirizzo email)
                mailSender.send(message);
                return "Mail inviata con successo!";
            } catch (Exception e) {
                e.printStackTrace();
                return "Errore nell'invio della Mail: " + e.getMessage();
            }
        }
    }
