package com.piniters.pinit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PinitApplication {

	public static void main(String[] args) {
		SpringApplication.run(PinitApplication.class, args);
	}

}
