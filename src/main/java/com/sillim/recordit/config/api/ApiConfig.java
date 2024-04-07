package com.sillim.recordit.config.api;

import java.time.Duration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApiConfig {

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplateBuilder().setConnectTimeout(Duration.ofSeconds(3)).build();
	}
}
