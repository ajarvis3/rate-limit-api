package com.ratelimit.dunning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DunningServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DunningServiceApplication.class, args);
	}

}
