package com.agenda.dashboard.core.services;

import com.agenda.dashboard.adapter.output.mongodb.entities.EstatisticaDocument;
import com.agenda.dashboard.adapter.output.mongodb.repos.SpringDataEstatisticaRepository;
import com.agenda.dashboard.core.domain.ContatoCriadoEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class) // Habilita o uso das anotações @Mock
class EstatisticaServiceTest {

    // Criamos dublês (Mocks) das dependências do Spring Data
    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private SpringDataEstatisticaRepository repository;

    private EstatisticaService estatisticaService;

    @BeforeEach
    void setUp() {
        // Agora o construtor é satisfeito com os nossos dublês!
        estatisticaService = new EstatisticaService(mongoTemplate, repository);
    }

    @Test
    @DisplayName("Deve enviar comando de incremento ao processar um evento válido")
    void deveIncrementarTotalDeContatos() {
        // Arrange (Preparação)
        ContatoCriadoEvent evento = new ContatoCriadoEvent("1", "Marcos", "marcos@gmail.com", 123L);

        // Act (Ação)
        estatisticaService.processarNovoContato(evento);

        // Assert (Verificação)
        // Como é um mock, nós verificamos se o Service chamou o método "upsert"
        // do MongoTemplate pelo menos uma vez, garantindo que o fluxo não quebrou.
        verify(mongoTemplate).upsert(
                any(Query.class),
                any(Update.class),
                eq(EstatisticaDocument.class)
        );
    }
}