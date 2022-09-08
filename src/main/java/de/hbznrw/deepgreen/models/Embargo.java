package de.hbznrw.deepgreen.models;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import de.hbznrw.deepgreen.utils.DateUtil;
import lombok.Data;

/**
 * 
 * @author Alessio Pellerito
 *
 */
@JsonIgnoreProperties
@Data
//@Slf4j
public class Embargo {
	
	private int duration;

	/**
	 * @param   dateStr   a unformatted date as string
	 * @return  {@code true} if and only if the parameter date with the added 
	 * 			duration months is after than the present day;
     *          {@code false} otherwise.  
	 * 
	 */
	public boolean isExceeded(String dateStr) {
		
		Date publicationDate = DateUtil.toDate(dateStr);
		Date updatedDate = DateUtil.addMonths(publicationDate, this.duration);
		//log.info("Date after adding duration months: {}", updatedDate);
		//log.info("Today Date: {}", new Date());
		return updatedDate.after(new Date());

	}
	
	


}

