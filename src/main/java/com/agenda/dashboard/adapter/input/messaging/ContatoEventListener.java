package com.agenda.dashboard.adapter.input.messaging;

import com.agenda.dashboard.core.domain.ContatoCriadoEvent;
import com.agenda.dashboard.core.domain.ContatoDeletadoEvent;
import com.agenda.dashboard.core.services.EstatisticaService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ContatoEventListener {

    private static final Logger log = LoggerFactory.getLogger(ContatoEventListener.class);
    private final EstatisticaService estatisticaService;
    private final ObjectMapper objectMapper;

    public ContatoEventListener(EstatisticaService estatisticaService, ObjectMapper objectMapper) {
        this.estatisticaService = estatisticaService;
        this.objectMapper = objectMapper;
    }

    // Escuta o tópico configurado no application.properties
    @KafkaListener(topics = "${app.kafka.topic.contact-events}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumir(String payload) {
        try {
            JsonNode node = objectMapper.readTree(payload);

            String emailExtraido = "";
            if (node.has("email")) {
                JsonNode emailNode = node.get("email");
                emailExtraido = emailNode.isObject() ? emailNode.get("valor").asText() : emailNode.asText();
            }

            // Descobre a ação (se não vier nada, assume o comportamento antigo de CRIAR)
            String acao = node.has("acao") ? node.get("acao").asText() : "CRIAR";

            if ("DELETAR".equals(acao)) {
                ContatoDeletadoEvent eventoDel = new ContatoDeletadoEvent(
                        node.get("id").asText(),
                        emailExtraido,
                        System.currentTimeMillis()
                );
                estatisticaService.processarContatoExcluido(eventoDel);
                log.info("🗑️ Estatística subtraída para o domínio: {}", emailExtraido);

            } else if ("ATUALIZAR".equals(acao)) {
                String emailAntigo = node.has("emailAntigo") ? node.get("emailAntigo").asText() : "";

                estatisticaService.processarContatoAtualizado(emailAntigo, emailExtraido);
                log.info("🔄 Estatística transferida: Saiu de [{}] para [{}]", emailAntigo, emailExtraido);

            } else {
                ContatoCriadoEvent eventoCriar = new ContatoCriadoEvent(
                        node.get("id").asText(),
                        node.has("nome") ? node.get("nome").asText() : "",
                        emailExtraido,
                        System.currentTimeMillis()
                );
                estatisticaService.processarNovoContato(eventoCriar);
                log.info("✅ Estatística somada para o domínio: {}", emailExtraido);
            }

        } catch (Exception e) {
            log.error("❌ Erro ao processar mensagem do Kafka: {}", e.getMessage());
        }
    }
}