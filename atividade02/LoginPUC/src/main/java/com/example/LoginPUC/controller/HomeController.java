package com.example.LoginPUC.controller;

import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // Raiz da aplicação: manda o usuário para a área protegida.
    @GetMapping("/")
    public String index() {
        return "redirect:/home";
    }

    // Página acessível somente por usuários autenticados
    @GetMapping("/home")
    public String home(Principal principal, Model model) {
        model.addAttribute("username", principal.getName());
        return "home";
    }
}
