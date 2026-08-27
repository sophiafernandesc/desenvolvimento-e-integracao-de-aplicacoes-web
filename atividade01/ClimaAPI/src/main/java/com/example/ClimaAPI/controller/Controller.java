package com.example.ClimaAPI.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.example.ClimaAPI.service.Service;

@RestController
public class Controller {

    Service service = new Service();

    @GetMapping("/localizacao/{cidade}") //http://localhost:8080/localizacao/belo%20horizonte
    public String consultarLocalizacao(@PathVariable String cidade) {
        return service.consultarLocalizacao(cidade);
    }

    @GetMapping("/temperatura/{cidade}")
    public String consultarTemperatura(@PathVariable String cidade) {
        return service.consultarTemperatura(cidade);
    }

    @GetMapping("/umidade/{cidade}")
    public String consultarUmidade(@PathVariable String cidade) {
        return service.consultarUmidade(cidade);
    }

    @GetMapping("/vento/velocidade/{cidade}")
    public String consultarVelocidadeVento(@PathVariable String cidade) {
        return service.consultarVelocidadeVento(cidade);
    }

    @GetMapping("/vento/direcao/{cidade}")
    public String consultarDirecaoVento(@PathVariable String cidade) {
        return service.consultarDirecaoVento(cidade);
    }

    @GetMapping("/temperatura/maxmin/{cidade}")
    public String consultarTemperaturaMaxMin(@PathVariable String cidade) {
        return service.consultarTemperaturaMaxMin(cidade);
    }

    @GetMapping("/condicoes/{cidade}")
    public String consultarCondicoes(@PathVariable String cidade) {
        return service.consultarCondicoes(cidade);
    }

    @GetMapping("/datahora/{cidade}")
    public String consultarDataHora(@PathVariable String cidade) {
        return service.consultarDataHora(cidade);
    }

    @GetMapping("/clima/{cidade}")
    public String preverTempo(@PathVariable String cidade) {
        return service.preverTempo(cidade);
    }
}