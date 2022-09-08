package de.hbznrw.deepgreen.models;

import java.net.URL;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * 
 * @author Alessio Pellerito
 *
 */
@JsonIgnoreProperties
@Data
public class Notification {
	
	private String id;
	private Embargo embargo;
	private Metadata metadata;
	private List<Link> links;
	
	@JsonIgnoreProperties
	@Data
	public static class Link {
		private String format;
		private URL url;
		private String packaging;
	}
	
	/**
	 * @return  the first url of the links as string
	 */
	public String getZipFileUrl() {
		return this.getLinks().get(0).getUrl().toString();
	} 
	
}
