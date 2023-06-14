package de.hbznrw.deepgreen.config;

import java.text.SimpleDateFormat;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author Alessio Pellerito
 *
 */
@Configuration
public class SingletonBeans implements WebFluxConfigurer {
	
	/**
	 * 
	 * @Bean are Singletons that can be used globally by injecting them in 
	 * the needed class. 
	 */
	
	@Bean
    public SimpleDateFormat getSimpleDateFormat(){
		return new SimpleDateFormat("yyyy-MM-dd");
    }
	
	@Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        configurer.defaultCodecs().maxInMemorySize(5000 * 1024);
    }

	@Bean
	public WebClient getWebClientBuilder() {
		return WebClient.builder().exchangeStrategies(ExchangeStrategies.builder().codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(5000 * 1024)).build()).build();
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
