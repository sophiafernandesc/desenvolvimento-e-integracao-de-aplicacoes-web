package com.example.LoginPUC.controller;

import com.example.LoginPUC.dto.EmailRequestDTO;
import com.example.LoginPUC.service.SendEmailService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/emails")
public class SendEmailController {

    private final SendEmailService sendEmailService;

    public SendEmailController(SendEmailService sendEmailService) {
        this.sendEmailService = sendEmailService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> send(@Valid @RequestBody EmailRequestDTO request) {
        sendEmailService.sendEmail(request.getTo(), request.getSubject(), request.getBody());
        return ResponseEntity.ok(Map.of("message", "Email enviado com sucesso"));
    }
}
