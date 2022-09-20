package de.hbznrw.deepgreen.config;

import java.text.SimpleDateFormat;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;

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
	 * @Bean are Singletons that can be used globally by injecting them in 
	 * the needed class. 
	 */
	
	@Bean
    public SimpleDateFormat getSimpleDateFormat(){
		return new SimpleDateFormat("yyyy-MM-dd");
    }

	@Bean
	public WebClient getWebClientBuilder() {
		return WebClient.builder().build();
	}
	
	@Bean
	public ObjectMapper getObjectMapper() {
		return new ObjectMapper();
	}
	 
	@Bean 
	public DocumentBuilderFactory getDocumentBuilderFactory() {
		return DocumentBuilderFactory.newInstance();
	} 
	 
	@Bean 
	public TransformerFactory getTransformerFactory() {
		return TransformerFactory.newInstance();
	} 
	
}
