package de.hbznrw.deepgreen.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

/**
 * Mapping class of the application.properties file with prefix "deepgreen"
 * @author Alessio Pellerito
 *
 */
@Data
@Configuration
//@PropertySource(value = "file:/etc/deepgreen/application.properties", ignoreResourceNotFound = true)
@ConfigurationProperties(prefix = "deepgreen")
public class DeepgreenProperties {
	
	private String zipFilePath;
	private String tmpDirPath;
	private String apiURL;
	private String notificationURL;
	private String apiKey;
	private String apiUser;
	private String apiPassword;
    
	private String baseURL;
	private String frlURL;
	private String elasticsearchURL;
	private String resourceURL;

}
