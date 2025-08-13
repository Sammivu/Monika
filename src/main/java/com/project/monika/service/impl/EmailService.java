package com.project.monika.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.monika.model.dto.Mail;
import org.springframework.scheduling.annotation.Async;

public interface EmailService {

    Mail getMail(String emailTo, String message, String emailTitle) throws JsonProcessingException;

    @Async
    void sendEmail(Mail mail);
}
