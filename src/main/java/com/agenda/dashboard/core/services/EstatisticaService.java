package com.agenda.dashboard.core.services;

import com.agenda.dashboard.core.domain.ContatoCriadoEvent;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class EstatisticaService {

    // Contador atômico seguro para múltiplas threads
    private final AtomicInteger totalContatos = new AtomicInteger(0);

    // Um mapa que vai agrupar os contatos pelo domínio do e-mail (ex: gmail.com = 3)
    private final Map<String, Integer> contatosPorDominio = new ConcurrentHashMap<>();

    public void processarNovoContato(ContatoCriadoEvent evento) {
        // 1. Incrementa o total geral
        totalContatos.incrementAndGet();

        // 2. Extrai o domínio do e-mail (tudo que vem depois do '@')
        String email = evento.email();
        if (email != null && email.contains("@")) {
            String dominio = email.substring(email.indexOf("@") + 1);
            // Se o domínio já existir, soma +1. Se não, cria com valor 1.
            contatosPorDominio.merge(dominio, 1, Integer::sum);
        }
    }

    public int getTotalContatos() {
        return totalContatos.get();
    }

    public Map<String, Integer> getContatosPorDominio() {
        return contatosPorDominio;
    }
}