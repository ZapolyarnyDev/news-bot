package io.github.zapolyarnydev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ZapolyarnyNewsBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZapolyarnyNewsBotApplication.class, args);
	}

}
