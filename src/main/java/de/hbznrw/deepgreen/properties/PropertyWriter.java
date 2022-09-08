package de.hbznrw.deepgreen.properties;

import java.io.File;
import java.io.IOException;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Alessio Pellerito
 *
 */
@Slf4j
public class PropertyWriter {
	
	final private static File FILE_PROP = new File("src/main/resources/futureEmbargos.properties");

	/**
	 * Writes the notificationId and the date from input to the specified properties file with
	 * prefix "resource." 
	 * @param notificationId   a notificationId
	 * @param dateStr          a date
	 */
	public static void writeKeyValue(String notificationId, String dateStr) {

		try {
			String pattern = "resource." + notificationId + "=" + dateStr;
			FileUtils.write(FILE_PROP, pattern, UTF_8, true);

		} catch (IOException e) {
			log.info("Error occurred writing into properties file");
		}
	}
	
	/**
	 * Deletes the resource with specified notificationId from the properties file 
	 * @param notificationId   a notificationId
	 * @throws IOException if an error occurs writing into properties file
	 */
	public static void deleteKey(String notificationId) {
		try {
			List<String> lines = FileUtils.readLines(FILE_PROP, UTF_8);
			List<String> updatedLines = lines.stream()
					                         .filter(line -> !line.contains(notificationId))
					                         .collect(Collectors.toList());
			FileUtils.writeLines(FILE_PROP, updatedLines, false);	
			
		} catch (IOException e) {
			log.info("Error occurred writing into properties file");
		}
		
	}
	
	

}
