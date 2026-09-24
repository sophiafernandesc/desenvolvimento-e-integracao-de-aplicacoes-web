package com.example.CandidatosTSE.controller;

import com.example.CandidatosTSE.model.Candidato;
import com.example.CandidatosTSE.service.CandidatosTseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CandidatosTseController {

    private final CandidatosTseService candidatosTseService;

    public CandidatosTseController(CandidatosTseService candidatosTseService) {
        this.candidatosTseService = candidatosTseService;
    }

    @GetMapping("/")
    public String index(
            @RequestParam(required = false) String cargo,
            @RequestParam(required = false) String partido,
            @RequestParam(required = false) String texto,
            Model model) {

        cargo = cargo == null ? "" : cargo;
        partido = partido == null ? "" : partido;
        texto = texto == null ? "" : texto;
        List<Candidato> candidatos = candidatosTseService.filtrar(cargo, partido, texto);
        int totalEncontrado = candidatos.size();

        model.addAttribute("cargo", cargo);
        model.addAttribute("partido", partido);
        model.addAttribute("texto", texto);
        model.addAttribute("totalEncontrado", totalEncontrado);
        return ("index");
    }

}
