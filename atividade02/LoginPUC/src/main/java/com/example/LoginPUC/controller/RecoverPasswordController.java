package com.example.LoginPUC.controller;

import com.example.LoginPUC.exception.SendEmailException;
import com.example.LoginPUC.service.PasswordResetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RecoverPasswordController {

    private static final Logger log = LoggerFactory.getLogger(RecoverPasswordController.class);

    private static final String MENSAGEM_GENERICA =
            "Se este email estiver cadastrado, enviamos as instruções de recuperação.";

    private final PasswordResetService passwordResetService;

    public RecoverPasswordController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @GetMapping("/recoverpassword")
    public String showRecoverForm() {
        return "recoverpassword";
    }

    @PostMapping("/recoverpassword")
    public String recover(@RequestParam("email") String email, Model model) {
        if (!StringUtils.hasText(email)) {
            model.addAttribute("error", "Informe um email.");
            return "recoverpassword";
        }

        try {
            passwordResetService.requestReset(email);
        } catch (SendEmailException ex) {
            // A falha é registrada no log, mas a resposta continua idêntica.
            // Qualquer diferença aqui revelaria quais emails estão cadastrados.
            log.warn("Falha ao enviar email de recuperação: {}", ex.getMessage());
        }

        model.addAttribute("success", MENSAGEM_GENERICA);
        return "recoverpassword";
    }
}
