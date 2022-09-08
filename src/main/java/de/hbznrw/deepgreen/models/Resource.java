package de.hbznrw.deepgreen.models;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

/**
 * 
 * @author Alessio Pellerito
 *
 */
@Component
@Data
@ConfigurationProperties(prefix = "attr") 
public class Resource {
	
	private String contentType;
	private String accessScheme;
	private String publishScheme;
	private Author isDescribedBy;
	
	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	private String parentPid;
	
	@Data
	public static class Author {
		private String createdBy;
	}
	

}
