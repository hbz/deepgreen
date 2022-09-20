package de.hbznrw.deepgreen.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * 
 * @author Alessio Pellerito
 *
 */
@JsonIgnoreProperties
@Data
public class Embargo {
	
	private int duration;

}

