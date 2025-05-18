package io.github.zapolyarnydev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication()
public class ZapolyarnyNewsBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZapolyarnyNewsBotApplication.class, args);
	}

}
