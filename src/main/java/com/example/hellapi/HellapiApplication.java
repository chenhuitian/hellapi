package com.example.hellapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HellapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(HellapiApplication.class, args);
	}

}
