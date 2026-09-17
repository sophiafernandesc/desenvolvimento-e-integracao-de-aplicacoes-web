package com.example.LoginPUC.controller;

import com.example.LoginPUC.dto.RegisterDTO;
import com.example.LoginPUC.model.User;
import com.example.LoginPUC.repository.UserRepository;
import jakarta.validation.Valid;
import java.util.Objects;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegisterController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("usuario", new RegisterDTO());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("usuario") RegisterDTO dto,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {

        if (!Objects.equals(dto.getPassword(), dto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "password.mismatch", "As senhas não coincidem");
        }

        // só consulta o banco se o campo já passou nas validações básicas
        if (!result.hasFieldErrors("username") && userRepository.existsByUsername(dto.getUsername())) {
            result.rejectValue("username", "username.duplicate", "Este usuário já está em uso");
        }
        if (!result.hasFieldErrors("email") && userRepository.existsByEmail(dto.getEmail())) {
            result.rejectValue("email", "email.duplicate", "Este email já está cadastrado");
        }

        if (result.hasErrors()) {
            return "register";
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("success", "Cadastro realizado com sucesso! Faça login.");
        return "redirect:/login";
    }
}