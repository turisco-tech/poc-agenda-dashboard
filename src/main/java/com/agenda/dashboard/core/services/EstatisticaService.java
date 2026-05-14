package com.agenda.dashboard.core.services;

import com.agenda.dashboard.adapter.output.mongodb.entities.EstatisticaDocument;
import com.agenda.dashboard.adapter.output.mongodb.repos.SpringDataEstatisticaRepository;
import com.agenda.dashboard.core.domain.ContatoCriadoEvent;
import com.agenda.dashboard.core.domain.ContatoDeletadoEvent;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
public class EstatisticaService {

    private final MongoTemplate mongoTemplate;
    private final SpringDataEstatisticaRepository repository;

    // O ID fixo do nosso documento materializado no MongoDB
    private static final String DOC_ID = "GERAL";

    public EstatisticaService(MongoTemplate mongoTemplate, SpringDataEstatisticaRepository repository) {
        this.mongoTemplate = mongoTemplate;
        this.repository = repository;
    }

    public void processarNovoContato(ContatoCriadoEvent evento) {
        String dominio = extrairDominio(evento.email());

        Query query = new Query(Criteria.where("id").is(DOC_ID));

        // Operação Atômica: O banco incrementa sozinho. Zero risco de concorrência!
        Update update = new Update()
                .inc("totalContatos", 1)
                .inc("contatosPorDominio." + dominio, 1); // Incrementa direto a chave do mapa

        // upsert: Se o documento "GERAL" não existir, ele cria na hora.
        mongoTemplate.upsert(query, update, EstatisticaDocument.class);
    }

    public void processarContatoExcluido(ContatoDeletadoEvent evento) {
        String dominio = extrairDominio(evento.email());
        Query query = new Query(Criteria.where("id").is(DOC_ID));

        // Subtrai de forma atômica
        Update update = new Update()
                .inc("totalContatos", -1)
                .inc("contatosPorDominio." + dominio, -1);

        mongoTemplate.upsert(query, update, EstatisticaDocument.class);
    }

    // O Motor de Transferência de Estatísticas
    public void processarContatoAtualizado(String emailAntigo, String emailNovo) {
        String dominioAntigo = extrairDominio(emailAntigo);
        String dominioNovo = extrairDominio(emailNovo);

        if (!dominioAntigo.equals(dominioNovo)) {
            Query query = new Query(Criteria.where("id").is(DOC_ID));

            // 1. Subtrai 1 do domínio velho
            Update updateRemover = new Update().inc("contatosPorDominio." + dominioAntigo, -1);
            mongoTemplate.upsert(query, updateRemover, EstatisticaDocument.class);

            // 2. Soma 1 no domínio novo
            Update updateAdicionar = new Update().inc("contatosPorDominio." + dominioNovo, 1);
            mongoTemplate.upsert(query, updateAdicionar, EstatisticaDocument.class);
        }
    }

    // --- Métodos de Leitura para o Controller (Fast Read) ---

    public int getTotalContatos() {
        return repository.findById(DOC_ID)
                .map(EstatisticaDocument::getTotalContatos)
                .orElse(0); // Se o banco estiver zerado, retorna 0
    }

    public Map<String, Integer> getContatosPorDominio() {
        return repository.findById(DOC_ID)
                .map(EstatisticaDocument::getContatosPorDominio)
                .orElse(Collections.emptyMap());
    }

    // O nosso método privado de extração sanitizado para o MongoDB!
    private String extrairDominio(String email) {
        if (email != null && email.contains("@")) {
            // Extrai o domínio (ex: spread.com.br)
            String dominioBruto = email.substring(email.indexOf("@") + 1);
            // Troca os pontos por underscore para não quebrar a Dot Notation do Mongo (ex: spread_com_br)
            return dominioBruto.replace(".", "_");
        }
        return "desconhecido";
    }
}