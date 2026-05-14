package com.agenda.dashboard.adapter.output.mongodb.repos;

import com.agenda.dashboard.adapter.output.mongodb.entities.EstatisticaDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataEstatisticaRepository extends MongoRepository<EstatisticaDocument, String> {
}