package br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorConfig {
	

    @Value("${frame.processor.thread-pool-size:5}")
    private int threadPoolSize;

    @Bean
    public ExecutorService frameProcessorExecutor() {
        return Executors.newFixedThreadPool(threadPoolSize);
    }

}
