package de.hbznrw.deepgreen.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author Alessio Pellerito
 *
 */
@Configuration
public class SingletonBeans {
	
	/**
	 * 
	 * @Bean are Singletons that can be used in the program by injecting them in 
	 * the needed class. 
	 */

	@Bean
	public WebClient getWebClientBuilder() {
		return WebClient.builder().build();
	}
	
	@Bean
	public ObjectMapper getObjectMapper() {
		return new ObjectMapper();
	}
	
}
