package com.project.monika.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.monika.model.dto.Mail;
import com.project.monika.service.impl.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Override
    public Mail getMail(String emailTo, String message, String emailTitle) throws JsonProcessingException {
        Mail mail = new Mail();
        mail.setFrom(this.senderEmail);
        mail.setTo(emailTo);
        mail.setSubject(emailTitle);
        mail.setModel(message);
        return mail;
    }

    @Async
    @Override
    public void sendEmail(Mail mail) {
        if (mail == null) {
            log.error("Mail object is null, cannot send email.");
            return;
        }
        if (Objects.isNull(mail.getTo()) || mail.getTo().isEmpty()) {
            log.warn("Email recipient is null/empty, ignoring...");
            return;
        }

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            helper.setFrom(mail.getFrom());
            helper.setTo(mail.getTo());
            helper.setSubject(mail.getSubject());
            helper.setText(mail.getModel(), false);
            javaMailSender.send(message);
            log.info("HTML email sent to {}", mail.getTo());

        } catch (MessagingException e) {
            log.error("Failed to send email: {}", e.getMessage(), e);
        }
    }

}

