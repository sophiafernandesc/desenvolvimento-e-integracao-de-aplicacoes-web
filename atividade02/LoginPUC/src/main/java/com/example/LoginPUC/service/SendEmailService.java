package com.example.LoginPUC.service;

import com.example.LoginPUC.exception.SendEmailException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SendEmailService {

    private final JavaMailSender mailSender;
    private final String from;

    public SendEmailService(JavaMailSender mailSender,
                            @Value("${app.mail.from}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom(from);

            mailSender.send(message);
        } catch (MailException e) {
            throw new SendEmailException("Falha ao enviar e-mail: " + e.getMessage());
        }
    }
}
