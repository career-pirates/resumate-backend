package com.careerpirates.resumate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ResumateBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResumateBackendApplication.class, args);
	}

}
