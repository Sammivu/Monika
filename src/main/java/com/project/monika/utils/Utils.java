package com.project.monika.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.monika.model.User;
import com.project.monika.model.dto.Mail;
import com.project.monika.service.impl.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;


import java.util.HashMap;
import java.util.Map;
@Slf4j
@RequiredArgsConstructor
@Component
public class Utils {

    private final SpringTemplateEngine springTemplateEngine;
    private final EmailService emailService;

    public void welcomeEmail(User user){
        try{
            Map<String, Object> templateMode = new HashMap<>();
            Context thymeleafContext = new Context();
            templateMode.put("name", user.getFirstName());
            thymeleafContext.setVariables(templateMode);
            String htmlBody = springTemplateEngine.process("email/welcome-email.html", thymeleafContext);
            Mail mail = emailService.getMail(user.getEmail(), htmlBody, "WELCOME ONBOARD");
            emailService.sendEmail(mail);
            log.info("Email sent successfully");
        } catch (JsonProcessingException e) {
            log.error("Email not sent::: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
