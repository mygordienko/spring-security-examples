package com.mygordienko.spring.security.examples.x509auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootApplication
public class SecurityExamplesApplication implements CommandLineRunner {
	public static void main(String[] args) {
		SpringApplication.run(SecurityExamplesApplication.class, args);
	}

	@Autowired
	private RestClient restClient;

	private static final String BASE_URL = "https://localhost:8080";
	private static final String HELLO_PATH = "/api/v1/hello";

	@Override
	public void run(String... args) {
		var uri = UriComponentsBuilder.fromUriString(BASE_URL)
				.path(HELLO_PATH)
				.build()
				.toUri();

		var response = restClient.get()
				.uri(uri)
				.retrieve()
				.body(GreetingDto.class);

		if (response == null) {
			IO.println("Null response");
			return;
		}

		IO.println("Response: %s".formatted(response.getMessage()));
	}

	public static class GreetingDto {
		public GreetingDto(String message) {
			this.message = message;
		}
		private String message;

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}
}
