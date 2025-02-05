package com.backstage.curtaincall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class CurtaincallApplication {

	public static void main(String[] args) {
		SpringApplication.run(CurtaincallApplication.class, args);
	}

}
