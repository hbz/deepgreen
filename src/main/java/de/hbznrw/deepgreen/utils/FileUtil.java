package de.hbznrw.deepgreen.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Some helpful File functions
 * @author Alessio Pellerito
 *
 */
@Slf4j
@Component
public class FileUtil {	
	
	/**
	 * Gets the file with the specified suffix from the specified path
	 * 
	 * @param suffix 	a suffix 
	 * @param path   	the path to search
	 * @return 			the file with the specified suffix
	 * @throws IOException if an error occurs opening the directory 
	 */
	public static File getFileBySuffix(String suffix, String path) {
		try {
			return Files.list(Paths.get(path))
					    .filter(name -> FilenameUtils.isExtension(name.toString(), suffix))
					    .findFirst()
					    .get()
					    .toFile();
		} catch(IOException ioEx) {
			log.error("An error occurred accessing the directory");
			return null;
		}
	}
	
	/**
	 * Removes the extension/suffix from the specified file
	 * 
	 * @param file a file
	 * @return the name of the file without the extension/suffix
	 */
	public static String getFileNameWithoutSuffix(File file) {
		return FilenameUtils.removeExtension(file.getName());
	} 
	
	/**
	 * Deletes the file on the specified path
	 * 
	 * @param path the path to the file
	 * @throws IOException if an error occurs opening the directory 
	 */
	public static void delete(String path) {
		try {
			Files.delete(Paths.get(path));
		} catch (IOException e) {
			log.error("An error occurred parsing the file");
		}
	}	
	
	public static void moveFileToPath(File file, String path) {
		try {
			FileUtils.moveFileToDirectory(file, new File(path), true);
		} catch (IOException e) {
			log.error("Error occurred moving file to specific path");
		}
	}

}
