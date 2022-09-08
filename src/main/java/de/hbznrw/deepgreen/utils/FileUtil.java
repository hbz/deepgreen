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
 * Some more File functions
 * @author Alessio Pellerito
 *
 */
@Slf4j
@Component
public class FileUtil extends FileUtils{	
	
	/**
	 * Gets the file with the specified suffix from the specified path
	 * @param suffix  a suffix 
	 * @param path    the path to search
	 * @return the file with the specified suffix
	 * @throws IOException if an error occurs opening the directory 
	 */
	public static File getFileBySuffix(String suffix, String path) {
		try {
			return Files.list(Paths.get(path))
					    .filter(name -> name.toString()
					    .endsWith("." + suffix))
					    .findFirst()
					    .get()
					    .toFile();
		}
		catch(IOException ioEx) {
			log.error("An error occurred accessing the directory");
			return null;
		}
	}
	
	/**
	 * Deletes the file on the specified path
	 * @param path the path to the file
	 * @throws IOException if an error occurs opening the directory 
	 */
	public static void deleteFile(String path) {
		try {
			Files.delete(Paths.get(path));
		} catch (IOException e) {
			log.error("An error occurred parsing the file");
		}
	}
	
	/**
	 * Deletes the whole Content of the directory on the specified path
	 * @param path the path to the directory
	 * @throws IOException in case cleaning is not successful
	 */
	public static void deleteDirContent(String path) {
		try {
			FileUtil.cleanDirectory(new File(path));
		} catch (IOException e) {
			log.error("Error occurred deleting contents of tmp file");
		}
	}
	
	/**
	 * Removes the extension/suffix from the specified file
	 * @param file a file
	 * @return the name of the file without the extension/suffix
	 */
	public static String getNameWithoutSuffix(File file) {
		return FilenameUtils.removeExtension(file.getName());
	} 

}
