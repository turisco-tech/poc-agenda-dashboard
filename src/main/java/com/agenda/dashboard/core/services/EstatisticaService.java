package com.agenda.dashboard.core.services;

import com.agenda.dashboard.core.domain.ContatoCriadoEvent;
import com.agenda.dashboard.core.domain.ContatoDeletadoEvent;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class EstatisticaService {

    private final AtomicInteger totalContatos = new AtomicInteger(0);
    private final Map<String, Integer> contatosPorDominio = new ConcurrentHashMap<>();

    public void processarNovoContato(ContatoCriadoEvent evento) {
        // 1. Incrementa o total geral
        totalContatos.incrementAndGet();

        // 2. Extrai o domínio e soma
        String dominio = extrairDominio(evento.email());
        contatosPorDominio.merge(dominio, 1, Integer::sum);
    }

    public void processarContatoExcluido(ContatoDeletadoEvent evento) {
        // 1. Subtrai o total geral (Corrigido para AtomicInteger)
        if (this.totalContatos.get() > 0) {
            this.totalContatos.decrementAndGet();
        }

        // 2. Subtrai a estatística do domínio
        String dominio = extrairDominio(evento.email());
        int contagemAtual = this.contatosPorDominio.getOrDefault(dominio, 0);

        if (contagemAtual <= 1) {
            // Se era o último contato desse domínio, remove a chave do mapa
            this.contatosPorDominio.remove(dominio);
        } else {
            // Se tinha mais, apenas subtrai 1
            this.contatosPorDominio.put(dominio, contagemAtual - 1);
        }
    }

    // O nosso novo método privado de extração!
    private String extrairDominio(String email) {
        if (email != null && email.contains("@")) {
            return email.substring(email.indexOf("@") + 1);
        }
        return "desconhecido"; // Fallback seguro caso chegue um email mal formatado
    }

    public int getTotalContatos() {
        return totalContatos.get();
    }

    public Map<String, Integer> getContatosPorDominio() {
        return contatosPorDominio;
    }
}