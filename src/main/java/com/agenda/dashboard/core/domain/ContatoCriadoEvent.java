package com.agenda.dashboard.core.domain;

// Usamos Record para criar uma classe imutável de forma extremamente limpa
public record ContatoCriadoEvent(
        String id,
        String nome,
        String email,
        Long timestamp
) {}