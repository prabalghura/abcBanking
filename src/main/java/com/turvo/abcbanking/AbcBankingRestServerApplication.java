package com.turvo.abcbanking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Application class for starting Spring Boot application
 * 
 * @author Prabal Ghura
 *
 */
@SpringBootApplication
@EnableJpaAuditing
public class AbcBankingRestServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AbcBankingRestServerApplication.class, args);
	}
}
