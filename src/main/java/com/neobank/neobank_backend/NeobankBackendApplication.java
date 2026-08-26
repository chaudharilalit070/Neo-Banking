package com.neobank.neobank_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableScheduling
@EnableMethodSecurity
public class NeobankBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(NeobankBackendApplication.class, args);
	}

}
