package com.agenda.dashboard.adapter.input.messaging;

import com.agenda.dashboard.core.domain.ContatoCriadoEvent;
import com.agenda.dashboard.core.services.EstatisticaService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ContatoCriadoListener {

    private final ObjectMapper objectMapper;
    private final EstatisticaService estatisticaService;

    // Injetamos o conversor e o nosso novo serviço
    public ContatoCriadoListener(ObjectMapper objectMapper, EstatisticaService estatisticaService) {
        this.objectMapper = objectMapper;
        this.estatisticaService = estatisticaService;
    }

    @KafkaListener(topics = "contact-created", groupId = "dashboard-group")
    public void consumir(String payload) {
        try {
            ContatoCriadoEvent evento = objectMapper.readValue(payload, ContatoCriadoEvent.class);

            // Repassa a responsabilidade de negócio para o Service
            estatisticaService.processarNovoContato(evento);

            System.out.println("=====================================================");
            System.out.println("📊 [DASHBOARD] ESTATÍSTICAS ATUALIZADAS!");
            System.out.println("📈 Total de Contatos Criados: " + estatisticaService.getTotalContatos());
            System.out.println("🌐 Divisão por Domínio: " + estatisticaService.getContatosPorDominio());
            System.out.println("=====================================================");

        } catch (JsonProcessingException e) {
            System.err.println("Erro ao tentar converter o evento do Kafka: " + e.getMessage());
        }
    }
}