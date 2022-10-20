package de.hbznrw.deepgreen.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Data;

/**
 * Mapping class of the application.properties file with prefix "server"
 * @author Alessio Pellerito
 *
 */
@Data
@Configuration
@PropertySource(value = "file:/etc/deepgreen/application.properties", ignoreResourceNotFound = true)
@ConfigurationProperties(prefix = "server")
public class ServerProperties {
	
	private String apiUser;
	private String apiPassword;
    
	private String baseURL;
	private String frlURL;
	private String elasticsearchURL;
	private String resourceURL;

}
