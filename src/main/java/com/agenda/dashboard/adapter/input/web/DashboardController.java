package com.agenda.dashboard.adapter.input.web;

import com.agenda.dashboard.core.services.EstatisticaService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/estatisticas")
@CrossOrigin(origins = "*")// Libera o CORS para qualquer origem (Ideal para MVP/Dev)
public class DashboardController {

    private final EstatisticaService estatisticaService;

    // Injetamos o nosso serviço que guarda os dados em memória
    public DashboardController(EstatisticaService estatisticaService) {
        this.estatisticaService = estatisticaService;
    }

    @GetMapping
    public EstatisticaResponse obterEstatisticas() {
        // Coleta os dados em tempo real do Service e devolve como JSON
        return new EstatisticaResponse(
                estatisticaService.getTotalContatos(),
                estatisticaService.getContatosPorDominio()
        );
    }

    // Criamos um Record interno apenas para formatar o JSON de saída
    public record EstatisticaResponse(
            int totalContatos,
            Map<String, Integer> porDominio
    ) {
    }
}