package de.hbznrw.deepgreen.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Alessio Pellerito
 *
 */
@JsonIgnoreProperties
@Data
@Slf4j
public class ArticleData {

	private int pageSize;
	private List<Notification> notifications;
	private int total;
	
	public Integer calcNumOfPages(Integer pageSize) {
		log.info("Total: {}, pageSize: {}", this.total, pageSize);
		return this.total % pageSize == 0 ? (this.total / pageSize) : (this.total / pageSize) + 1;
	}

}
