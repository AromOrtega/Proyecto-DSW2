package com.saludvital.config;

import com.saludvital.client.MedicoHttpClient;
import com.saludvital.client.PacienteHttpClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * Configura los clientes HttpExchange usando RestClient con carga balanceada
 * (Eureka).
 *
 * Flujo:
 * 1. RestClient con @LoadBalanced → resuelve lb://servicio-name via Eureka
 * 2. HttpServiceProxyFactory crea el proxy dinámico de las
 * interfaces @HttpExchange
 */
@Configuration
public class HttpExchangeConfig {

    @Bean
    @LoadBalanced
    public RestClient.Builder loadBalancedRestClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public MedicoHttpClient medicoHttpClient(RestClient.Builder builder) {
        RestClient restClient = builder.baseUrl("http://medico-service-ws").build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(org.springframework.web.client.support.RestClientAdapter.create(restClient))
                .build();
        return factory.createClient(MedicoHttpClient.class);
    }

    @Bean
    public PacienteHttpClient pacienteHttpClient(RestClient.Builder builder) {
        RestClient restClient = builder.baseUrl("http://paciente-service-ws").build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(org.springframework.web.client.support.RestClientAdapter.create(restClient))
                .build();
        return factory.createClient(PacienteHttpClient.class);
    }
}
