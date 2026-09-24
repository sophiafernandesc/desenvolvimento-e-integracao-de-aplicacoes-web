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
        List<String> cargos = candidatosTseService.listarCargos();
        List<String> partidos = candidatosTseService.listarPartidos();

        model.addAttribute("cargoSelecionado", cargo);
        model.addAttribute("partidoSelecionado", partido);
        model.addAttribute("textoSelecionado", texto);
        model.addAttribute("totalEncontrado", totalEncontrado);
        model.addAttribute("cargos", cargos);
        model.addAttribute("partidos", partidos);
        model.addAttribute("candidatos", candidatos);
        return"index";
    }

}
