package com.example.ClimaAPI.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.net.URI;

public class Service {

    private static final String BASE_URL = "https://api.open-meteo.com/v1/forecast";
    private static final String GEO_URL = "https://geocoding-api.open-meteo.com/v1/search";

    private String consultarURL(String apiUrl){
        String dados = "";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(URI.create(apiUrl), String.class);
        if(responseEntity.getStatusCode().is2xxSuccessful()){
            dados = responseEntity.getBody();
        } else{
            dados = "Falha ao obter dados. Código de status: " + responseEntity.getStatusCode();
        }
        return dados;
    }

    private String extrairValor(String json, String campo){
        String busca = "\"" + campo + "\":";
        int inicio = json.indexOf(busca);
        if(inicio == -1){
            return null;
        }
        inicio = inicio + busca.length();
        int fim = inicio;
        while(fim < json.length() && "-0123456789.".indexOf(json.charAt(fim)) >= 0){
            fim++;
        }
        return json.substring(inicio, fim);
    }

    private String montarCoordenadas(String cidade){
        String geo = consultarLocalizacao(cidade);
        String latitude = extrairValor(geo, "latitude");
        String longitude = extrairValor(geo, "longitude");
        if(latitude == null || longitude == null){
            return null;
        }
        return "?latitude=" + latitude + "&longitude=" + longitude
                + "&timezone=America%2FSao_Paulo";
    }

    private String consultarForecast(String cidade, String parametros){
        String coords = montarCoordenadas(cidade);
        if(coords == null){
            return "Cidade não encontrada: " + cidade;
        }
        return consultarURL(BASE_URL + coords + parametros);
    }

    public String consultarLocalizacao(String cidade){
        return consultarURL(GEO_URL + "?name=" + cidade.replace(" ", "%20")
                + "&count=1&language=pt&format=json");
    }

    public String consultarTemperatura(String cidade){
        return consultarForecast(cidade, "&current=temperature_2m");
    }

    public String consultarUmidade(String cidade){
        return consultarForecast(cidade, "&current=relative_humidity_2m");
    }

    public String consultarVelocidadeVento(String cidade){
        return consultarForecast(cidade, "&current=wind_speed_10m");
    }

    public String consultarDirecaoVento(String cidade){
        return consultarForecast(cidade, "&current=wind_direction_10m");
    }

    public String consultarTemperaturaMaxMin(String cidade){
        return consultarForecast(cidade,
                "&daily=temperature_2m_max,temperature_2m_min&forecast_days=1");
    }

    public String consultarCondicoes(String cidade){
        return consultarForecast(cidade, "&current=weather_code");
    }

    public String consultarDataHora(String cidade){
        return consultarForecast(cidade, "&current=temperature_2m");
    }

    public String preverTempo(String cidade){
        return consultarForecast(cidade,
                "&current=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m,wind_direction_10m"
                        + "&daily=temperature_2m_max,temperature_2m_min&forecast_days=1");
    }
}