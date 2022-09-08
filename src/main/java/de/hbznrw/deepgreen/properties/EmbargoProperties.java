package de.hbznrw.deepgreen.properties;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Data;

/**
 * Mapping class of the futureEmbargos.properties file with no prefix
 * @author Alessio Pellerito
 *
 */
@Data
@Configuration
@PropertySource("classpath:/futureEmbargos.properties")
@ConfigurationProperties(prefix = "")
public class EmbargoProperties {

	private final Map<String, String> resource = new HashMap<>();
	
}
