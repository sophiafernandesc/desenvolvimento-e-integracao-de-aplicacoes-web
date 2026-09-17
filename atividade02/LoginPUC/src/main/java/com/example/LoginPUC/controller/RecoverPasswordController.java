package com.example.LoginPUC.controller;

import com.example.LoginPUC.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RecoverPasswordController {

    private final UserRepository userRepository;

    public RecoverPasswordController(UserRepository userRepository) {
        this.userRepository = userRepository;
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

        // Aqui entraria o envio real de email com o link/token de redefinição.
        userRepository.findByEmail(email.trim()).ifPresent(user -> {
            // TODO: gerar token e disparar email (ex.: JavaMailSender / serviço externo).
        });

        model.addAttribute("success",
                "Se este email estiver cadastrado, enviamos as instruções de recuperação.");
        return "recoverpassword";
    }
}
