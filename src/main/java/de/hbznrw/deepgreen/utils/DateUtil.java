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

	/**
	 * Converts a string representation of a date to a Date object with
	 * format yyyy-MM-dd
	 * @param   date  a date as string
	 * @return  a Date object
	 * @exception ParseException  if the string cannot be parsed
	 */
	public static Date toDate(String date) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			return formatter.parse(date);
		} catch (ParseException e) {
			log.error("An error occurred converting string to date");
		}
		return null;
	}
}
