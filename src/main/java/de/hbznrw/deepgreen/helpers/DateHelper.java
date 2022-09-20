package de.hbznrw.deepgreen.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Some helpful Date functions
 * @author Alessio Pellerito
 *
 */
@Slf4j
@Component
public class DateHelper {
	
	@Autowired
	private SimpleDateFormat formatter;

	/**
	 * Converts a string representation of a date to a Date object with
	 * format yyyy-MM-dd
	 * 
	 * @param   date  			a date as string
	 * @return  				a Date object
	 * @throws ParseException  	if the string cannot be parsed
	 */
	public Date toDate(String dateStr) {
		if(dateStr == null || dateStr.isEmpty())
			return null;
		try {
			return formatter.parse(dateStr);
		} catch (ParseException e) {
			log.error("An error occurred converting string to date");
		}
		return null;
	}
	
	/**
	 * Tests if the parameter date is valid and has a valid format
	 * 
	 * @param 		dateToValidate	the date to validate
	 * @return		{@code true} 	if date is valid and has a valid format; {@code false} otherwise. 
	 * @throws		ParseException 	if a problem during parsing occurred
	 */
	public boolean isValidFormat(String dateToValidate) {
		if(dateToValidate == null || dateToValidate.isEmpty())
			return false;
	    formatter.setLenient(false);	    
	    try {	
	        formatter.parse(dateToValidate);
	    } catch (ParseException e) {
	        log.error("Date {} is not valid or not in format {}", dateToValidate, formatter.getNumberFormat());
	        return false;
	    }
	    return true;
	}
	
	/**
	 * Adds the months to the date and checks if the resulting date is after the 
	 * current date
	 *  
	 * @param   dateStr   a unformatted date as string
	 * @param   months    the number of months to add to dateStr
	 * @return  {@code true} if and only if the parameter date with the added 
	 * 			duration months is after than the present day;
     *          {@code false} otherwise.  
	 * 
	 */
	public boolean isDateExceeded(String dateStr, int months) {
		Date publicationDate = toDate(dateStr);
		Date updatedDate = DateUtils.addMonths(publicationDate, months);
		return updatedDate.after(new Date());
	}
	
}
