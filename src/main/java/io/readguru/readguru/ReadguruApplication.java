package io.readguru.readguru;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ReadguruApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReadguruApplication.class, args);
	}

	@GetMapping("/")
	public String index() {
		System.out.println("sherif:");
		return "Hello World from Spring Boot!";
	}

}
