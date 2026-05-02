package com.agenda.dashboard.adapter.input.messaging;

import com.agenda.dashboard.core.domain.ContatoDeletadoEvent;
import com.agenda.dashboard.core.services.EstatisticaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ContatoDeletadoListener {

    private final ObjectMapper objectMapper;
    private final EstatisticaService estatisticaService;

    // Injetamos o conversor e o nosso novo serviço
    public ContatoDeletadoListener(ObjectMapper objectMapper, EstatisticaService estatisticaService) {
        this.objectMapper = objectMapper;
        this.estatisticaService = estatisticaService;
    }

    @KafkaListener(topics = "${app.kafka.topic.contact-deleted}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumirExclusao(String payload) {
        try {
            ContatoDeletadoEvent evento = objectMapper.readValue(payload, ContatoDeletadoEvent.class);
            estatisticaService.processarContatoExcluido(evento);

            System.out.println("📉 [DASHBOARD] Contato removido! Novo total: " + estatisticaService.getTotalContatos());
        } catch (Exception e) {
            System.err.println("Erro ao processar exclusão: " + e.getMessage());
        }
    }
}