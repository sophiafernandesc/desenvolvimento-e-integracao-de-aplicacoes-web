package com.example.LoginPUC.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class EmailRequestDTO {

    @NotBlank(message = "Informe o destinatário")
    @Email(message = "Informe um email válido")
    private String to;

    @NotBlank(message = "Informe o assunto")
    private String subject;

    @NotBlank(message = "Informe o corpo da mensagem")
    private String body;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
