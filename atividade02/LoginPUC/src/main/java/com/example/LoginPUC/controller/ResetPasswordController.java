package com.example.LoginPUC.controller;

import com.example.LoginPUC.dto.ResetPasswordDTO;
import com.example.LoginPUC.service.PasswordResetService;
import jakarta.validation.Valid;
import java.util.Objects;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ResetPasswordController {

    private static final String LINK_INVALIDO =
            "Link inválido ou expirado. Solicite uma nova recuperação de senha.";

    private final PasswordResetService passwordResetService;

    public ResetPasswordController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    /** Tela aberta pelo link do email. O token chega na query string. */
    @GetMapping("/resetpassword")
    public String showResetForm(@RequestParam(value = "token", required = false) String token,
                                Model model) {

        if (passwordResetService.findUserByValidToken(token).isEmpty()) {
            model.addAttribute("error", LINK_INVALIDO);
            model.addAttribute("tokenValido", false);
            return "resetpassword";
        }

        ResetPasswordDTO dto = new ResetPasswordDTO();
        dto.setToken(token);

        model.addAttribute("resetPassword", dto);
        model.addAttribute("tokenValido", true);
        return "resetpassword";
    }

    @PostMapping("/resetpassword")
    public String reset(@Valid @ModelAttribute("resetPassword") ResetPasswordDTO dto,
                        BindingResult result,
                        Model model,
                        RedirectAttributes redirectAttributes) {

        if (!Objects.equals(dto.getPassword(), dto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "password.mismatch", "As senhas não coincidem");
        }

        if (result.hasErrors()) {
            model.addAttribute("tokenValido", true);
            return "resetpassword";
        }

        // O token é revalidado aqui: ele pode ter expirado entre abrir a tela e enviar o form.
        if (!passwordResetService.resetPassword(dto.getToken(), dto.getPassword())) {
            model.addAttribute("error", LINK_INVALIDO);
            model.addAttribute("tokenValido", false);
            return "resetpassword";
        }

        redirectAttributes.addFlashAttribute("success", "Senha alterada com sucesso! Faça login.");
        return "redirect:/login";
    }
}
