package com.agenda.dashboard.core.services;

import com.agenda.dashboard.core.domain.ContatoCriadoEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EstatisticaServiceTest {

    private EstatisticaService estatisticaService;

    @BeforeEach
    void setUp() {
        // Antes de cada teste, zeramos o nosso serviço
        estatisticaService = new EstatisticaService();
    }

    @Test
    @DisplayName("Deve incrementar o total de contatos ao processar um evento válido")
    void deveIncrementarTotalDeContatos() {
        // Arrange (Preparação)
        ContatoCriadoEvent evento = new ContatoCriadoEvent("1", "Marcos", "marcos@gmail.com", 123L);

        // Act (Ação)
        estatisticaService.processarNovoContato(evento);
        estatisticaService.processarNovoContato(evento); // Simulando 2 eventos

        // Assert (Verificação)
        assertEquals(2, estatisticaService.getTotalContatos());
    }

    @Test
    @DisplayName("Deve agrupar os contatos corretamente pelo domínio do e-mail")
    void deveAgruparContatosPorDominio() {
        // Arrange
        ContatoCriadoEvent eventoGmail1 = new ContatoCriadoEvent("1", "A", "a@gmail.com", 1L);
        ContatoCriadoEvent eventoGmail2 = new ContatoCriadoEvent("2", "B", "b@gmail.com", 2L);
        ContatoCriadoEvent eventoHotmail = new ContatoCriadoEvent("3", "C", "c@hotmail.com", 3L);

        // Act
        estatisticaService.processarNovoContato(eventoGmail1);
        estatisticaService.processarNovoContato(eventoGmail2);
        estatisticaService.processarNovoContato(eventoHotmail);

        // Assert
        Map<String, Integer> dominios = estatisticaService.getContatosPorDominio();

        assertEquals(2, dominios.size(), "Deveria ter encontrado 2 domínios diferentes");
        assertEquals(2, dominios.get("gmail.com"));
        assertEquals(1, dominios.get("hotmail.com"));
    }

    @Test
    @DisplayName("Não deve quebrar se o e-mail vier nulo ou sem formato")
    void deveLidarComEmailInvalido() {
        // Arrange
        ContatoCriadoEvent eventoSemArroba = new ContatoCriadoEvent("1", "Teste", "emailinvalido", 1L);
        ContatoCriadoEvent eventoNulo = new ContatoCriadoEvent("2", "Teste 2", null, 2L);

        // Act
        estatisticaService.processarNovoContato(eventoSemArroba);
        estatisticaService.processarNovoContato(eventoNulo);

        // Assert
        assertEquals(2, estatisticaService.getTotalContatos(), "Os contatos devem ser contados no total geral");

        // A NOVA ASSERÇÃO: Verifica se o nosso fallback entrou em ação e agrupou os 2 na chave 'desconhecido'
        assertEquals(2, estatisticaService.getContatosPorDominio().get("desconhecido"), "Deve agrupar emails inválidos na chave 'desconhecido'");
    }
}