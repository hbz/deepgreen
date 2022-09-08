package de.hbznrw.deepgreen.models;

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
public class ArticleData {

	private Long pageSize;
	private List<Notification> notifications;
	private Long total;

}
