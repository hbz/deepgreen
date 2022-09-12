package de.hbznrw.deepgreen;

import java.io.File;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static de.hbznrw.deepgreen.constants.ContentType.*;
import static de.hbznrw.deepgreen.utils.DateUtil.*;

import de.hbznrw.deepgreen.models.ArticleData;
import de.hbznrw.deepgreen.models.Embargo;
import de.hbznrw.deepgreen.models.Metadata;
import de.hbznrw.deepgreen.models.Notification;
import de.hbznrw.deepgreen.properties.EmbargoProperties;
import de.hbznrw.deepgreen.properties.PropertyWriter;
import de.hbznrw.deepgreen.service.ResourceClient;
import de.hbznrw.deepgreen.utils.FileUtil;
import de.hbznrw.deepgreen.utils.ZipUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Alessio Pellerito
 *
 */
@Slf4j
@SpringBootApplication
public class DeepgreenApplication implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(DeepgreenApplication.class, args);
		
	}
	
	private static final Long DEFAULT_PAGE_SIZE = 25L;
	private static final Long MAX_PAGE_SIZE = 100L;
	
	@Value("${deepgreen.apiKey}")
	private String apiKey;
	
	@Value("${deepgreen.tmpDirPath}")
	private String tmpDirPath;
	
	@Value("${deepgreen.zipFilePath}")
	private String zipFilePath;
	
	@Autowired
	private ResourceClient client;
	
	@Autowired
	private EmbargoProperties embargoProperties;
	
	@Override
	public void run(String... args) {
		
		// i.e. args[0] = 2021-05-01 (must have this date format)
		// i.e. args[1] = 37 (pageSize is optional, range 1-100 allowed, default 25)
		if(args.length > 0) {
			String date = args[0];
			
			if(!isValidFormat(date))
				System.exit(1);

			Long pageSize = DEFAULT_PAGE_SIZE;
			
			if(args.length == 2) {
				try {
					Long userPageSize = Long.parseLong(args[1]);
					if(userPageSize <= MAX_PAGE_SIZE && userPageSize >= 1L)
						pageSize = userPageSize;
				}
				catch (NumberFormatException e) {
					log.warn("Second Argument {} could not be parsed, default page size {} is used", args[1], DEFAULT_PAGE_SIZE);
				}
			}

			Long totalRessources = client.getNotifications(date, pageSize, null).getTotal();
			Long numberOfPages = getNumberOfPages(totalRessources, pageSize);
			
			if(totalRessources > 0) {
				
				/* read futureEmbargos.properties file */
				embargoProperties.getResource().forEach((notificationId,dateStr) -> {
					
					Notification notification = client.getNotification(notificationId);
					Metadata metaData = notification.getMetadata();
					Embargo embargo = notification.getEmbargo();
					
					if(!embargo.isExceeded(dateStr)) {
						String zipUrl = notification.getZipFileUrl();
						
						ZipUtil.getZipFromURL(zipUrl, apiKey, zipFilePath);
						ZipUtil.extractZip(zipFilePath, tmpDirPath);						
			
						String doi = metaData.getDoi();
						boolean doiExists = client.doiExists(doi);	
						
						if(doiExists) {
							log.info("The Resource with doi: {} already exists, no upload to FRL", doi);
							FileUtil.deleteDirContent(tmpDirPath);
							PropertyWriter.deleteKey(notificationId);
							return;
						}
						
						if(!doiExists) {
							// send xmlFile
							File xmlFile = FileUtil.getFileBySuffix(XML, tmpDirPath);
							String mainResource = client.createResource(ARTICLE);
							client.sendXmlToResource(xmlFile, mainResource, embargo.getDuration());
							
							// send pdfFile
							File pdfFile = FileUtil.getFileBySuffix(PDF, tmpDirPath);
							String childResource = client.createChildResource(FILE, mainResource);
							client.sendPdfToResource(pdfFile, childResource);
							
							// delete line in futureEmbargos.properties file
							PropertyWriter.deleteKey(notificationId);
						}
					}
				});		
				/* read futureEmbargos.properties file END */
				
				
				/* read API */
				for(long currentPage = 1L; currentPage <= 1L/*numberOfPages*/; currentPage++) {
					
					ArticleData data = client.getNotifications(date, pageSize, currentPage);
					List<Notification> notificationList = data.getNotifications();
					
					notificationList.stream().limit(13L).forEach( notification -> {

						Metadata metaData = notification.getMetadata();
						Embargo embargo = notification.getEmbargo();

						String zipUrl = notification.getZipFileUrl();
						
						ZipUtil.getZipFromURL(zipUrl, apiKey, zipFilePath);
						ZipUtil.extractZip(zipFilePath, tmpDirPath);						
						
						// check if embargodate exceeded
						if(embargo.isExceeded(metaData.getDate())) {
							log.info("EmbargoDate of notificationId: {} exceeded, write to properties file", notification.getId());
							PropertyWriter.writeKeyValue(notification.getId(), metaData.getDate());
							return;
						}
						// check if embargodate exceeded END 
						
						String doi = metaData.getDoi();
						boolean doiExists = client.doiExists(metaData.getDoi());	

						if(doiExists) {
							log.info("The Resource with doi: {} already exists, no upload to FRL", doi);
							FileUtil.deleteDirContent(tmpDirPath);
							return;
						}
						
						if(!doiExists) {
							// send xmlFile
							File xmlFile = FileUtil.getFileBySuffix(XML, tmpDirPath);
							String mainResource = client.createResource(ARTICLE);
							client.sendXmlToResource(xmlFile, mainResource, embargo.getDuration());
							
							// send pdfFile
							File pdfFile = FileUtil.getFileBySuffix(PDF, tmpDirPath);
							String childResource = client.createChildResource(FILE, mainResource);
							client.sendPdfToResource(pdfFile, childResource);
						}
					
					});
				}
				/* read API END */
				
			}
			if(totalRessources == 0) {
				log.info("There are no Ressources to fetch");
			}
		}
		else {
			log.error("Date argument is missing or is not in format yyyy-MM-DD, i.e. 2021-08-22");
			log.error("A second numerical argument, describing the pageSize, is optional");
		}
		
	}
	
	private Long getNumberOfPages(Long totalRessources, Long pageSize) {
		log.info("Total: {}, pageSize: {}", totalRessources, pageSize);
		return totalRessources % pageSize == 0 ? (totalRessources / pageSize) : (totalRessources / pageSize) + 1;
	}

}
