package com.beaston.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}

// mam aplikację w springu i bedę używał dockera, a takze dla testów bazy postgresowej.
//
//flow aplikacji bedzie takie że użytkowicy bedą