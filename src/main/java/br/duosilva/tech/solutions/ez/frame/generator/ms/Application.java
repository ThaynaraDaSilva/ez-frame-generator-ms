package br.duosilva.tech.solutions.ez.frame.generator.ms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableFeignClients
@EnableScheduling
@ComponentScan(basePackages = 
	{
			"br.duosilva.tech.solutions.ez.frame.generator.ms",
			"br.duosilva.tech.solutions.ez.frame.generator.ms.adapters.in.listener",
			"br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.config"
	})
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
