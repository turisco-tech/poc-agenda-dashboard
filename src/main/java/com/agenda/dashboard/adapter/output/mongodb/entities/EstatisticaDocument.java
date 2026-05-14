package com.agenda.dashboard.adapter.output.mongodb.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

@Document(collection = "estatisticas")
public class EstatisticaDocument {

    @Id
    private String id; // Vamos travar esse ID como "GERAL"
    private int totalContatos;
    private Map<String, Integer> contatosPorDominio = new HashMap<>();

    // Construtor vazio necessário para o Spring Data
    public EstatisticaDocument() {
    }

    // Getters e Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTotalContatos() {
        return totalContatos;
    }

    public void setTotalContatos(int totalContatos) {
        this.totalContatos = totalContatos;
    }

    public Map<String, Integer> getContatosPorDominio() {
        return contatosPorDominio;
    }

    public void setContatosPorDominio(Map<String, Integer> contatosPorDominio) {
        this.contatosPorDominio = contatosPorDominio;
    }
}