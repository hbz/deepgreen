package de.hbznrw.deepgreen.helpers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.stereotype.Component;

import static de.hbznrw.deepgreen.constants.QueryParam.APIKEY;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

/**
 * Some helpful zip Functions
 * @author Alessio Pellerito
 *
 */
@Slf4j
@Component
public class ZipHelper {
	
	/**
	 * Gets the zip File from the specified URL authorizing with the api key and
	 * writes it to the directory in the specified path
	 * 
	 * @param url 			the url to get the zip File from
	 * @param apiKey 		the auhorization api key
	 * @param zipPath 		the path to write to
	 * @throws MalformedURLException if the URL has no valid format
	 * @throws IOException 	if an error occurs writing to the directory
	 */
	public static void getZipFromURL(String url, String apiKey, String zipPath) {
		try {
			Path pathToZip = Paths.get(zipPath);
			
			if(!Files.exists(pathToZip))
				Files.createDirectories(pathToZip);
			
			Files.copy(new URL(url.concat("?" + APIKEY + "="+apiKey)).openStream(), 
					   pathToZip, 
					   StandardCopyOption.REPLACE_EXISTING);
			
		} catch (MalformedURLException e) {
			log.error("URL has no valid format");
		} catch (IOException e) {
			log.error("An error occurred writing zip file to directory");
			e.printStackTrace();
		}
	}
	
	/**
	 * Extracts the File and writes the content in the specified path
	 *  
	 * @param zipPath 		the path where the zip File is 
	 * @param extractToPath the path to write the content to
	 * @throws ZipException if an error occurs extracting the file
	 * @throws IOException 	if an error occurs reading/writing zip file
	 */
	public static void extractZip(String zipPath, String extractToPath) {
		
		try(ZipFile zip = new ZipFile(zipPath)) {
			zip.extractAll(extractToPath);
			
		} catch(ZipException zipEx) {
			log.error("An error occurred extracting zip file");
		} catch(IOException ioEx) {
			log.error("An error occurred reading/writing zip file");
			ioEx.printStackTrace();
		}
	}


}
