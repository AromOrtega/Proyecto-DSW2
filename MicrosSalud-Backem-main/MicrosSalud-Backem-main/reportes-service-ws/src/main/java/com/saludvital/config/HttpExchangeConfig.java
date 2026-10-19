package com.saludvital.config;

import com.saludvital.client.CitaClient;
import com.saludvital.client.MedicoClient;
import com.saludvital.client.PacienteClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.client.support.RestClientAdapter;

/**
 * Registra los clientes HttpExchange usando RestClient + LoadBalancer (Eureka
 * lb://).
 */
@Configuration
public class HttpExchangeConfig {

    @Bean
    @LoadBalanced
    public RestClient.Builder loadBalancedRestClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public CitaClient citaClient(RestClient.Builder builder) {
        RestClient restClient = builder.baseUrl("http://cita-service-ws").build();
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build()
                .createClient(CitaClient.class);
    }

    @Bean
    public MedicoClient medicoClient(RestClient.Builder builder) {
        RestClient restClient = builder.baseUrl("http://medico-service-ws").build();
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build()
                .createClient(MedicoClient.class);
    }

    @Bean
    public PacienteClient pacienteClient(RestClient.Builder builder) {
        RestClient restClient = builder.baseUrl("http://paciente-service-ws").build();
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build()
                .createClient(PacienteClient.class);
    }
}
