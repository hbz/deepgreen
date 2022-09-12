package de.hbznrw.deepgreen.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Some more Date functions
 * @author Alessio Pellerito
 *
 */
@Slf4j
public class DateUtil extends DateUtils{
	
	private static final String DATE_FORMAT = "yyyy-MM-dd";

	/**
	 * Converts a string representation of a date to a Date object with
	 * format yyyy-MM-dd
	 * @param   date  a date as string
	 * @return  a Date object
	 * @exception ParseException  if the string cannot be parsed
	 */
	public static Date toDate(String dateString) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
			return formatter.parse(dateString);
		} catch (ParseException e) {
			log.error("An error occurred converting string to date");
		}
		return null;
	}
	
	/**
	 * 
	 */
	public static boolean isValidFormat(String dateToValidate) {
		if(dateToValidate == null || dateToValidate.isEmpty())
			return false;
	 
	    SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
	    formatter.setLenient(false);	    
	    try {	
	        formatter.parse(dateToValidate);
	    } catch (ParseException e) {
	        log.error("Date {} is not valid or not in format {}", dateToValidate, DATE_FORMAT);
	        return false;
	    }
	    return true;
	}
}
