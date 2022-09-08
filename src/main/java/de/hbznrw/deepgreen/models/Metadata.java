package de.hbznrw.deepgreen.models;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * 
 * @author Alessio Pellerito
 *
 */
@JsonIgnoreProperties
@Data
public class Metadata {

	private String publication_date;
	private List<Identifier> identifier;
	
	@JsonIgnoreProperties
	@Data
	public static class Identifier {
		private String type;
		private String id;
	}
	
	/**
	 * @return  the first 10 characters of the publication date  
	 */
	public String getDate() {
		return this.publication_date.substring(0,10);
	}
	
	/**
	 * @return  the doi from the identifier objects
	 */
	public String getDoi() {
		return this.getIdentifier().stream()
				   				   .filter(m -> m.getType().equals("doi"))
				   				   .map(Metadata.Identifier::getId)
				   				   .collect(Collectors.joining());
	}
}
