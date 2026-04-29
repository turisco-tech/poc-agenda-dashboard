package com.agenda.dashboard.infra.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DashboardConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

}