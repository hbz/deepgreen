package de.hbznrw.deepgreen;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.hbznrw.deepgreen.clients.ResourceClient;
import de.hbznrw.deepgreen.entities.FuturEmbargo;
import de.hbznrw.deepgreen.helpers.DateHelper;
import de.hbznrw.deepgreen.helpers.ZipHelper;
import de.hbznrw.deepgreen.models.ArticleData;
import de.hbznrw.deepgreen.models.Embargo;
import de.hbznrw.deepgreen.models.Metadata;
import de.hbznrw.deepgreen.models.Notification;
import de.hbznrw.deepgreen.repositories.FuturEmbargoRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Alessio Pellerito
 *
 */
@Slf4j
@SpringBootApplication
public class StartApplication implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(StartApplication.class);
		application.setWebApplicationType(WebApplicationType.NONE);
		application.run(args);	
	}
	
	/* Injecting values */
	@Value("25")
	private Integer defaultPageSize;
	
	@Value("100")
	private Integer maxPageSize;
	
	@Value("${deepgreen.apiKey}")
	private String apiKey;
	
	@Value("${deepgreen.tmpDirPath}")
	private String tmpDirPath;
	
	@Value("${deepgreen.zipFilePath}")
	private String zipFilePath;
	
	/* Injecting objects */
	@Autowired
	private DateHelper dateHelper;
	
	@Autowired
	private ResourceClient client;
	
	@Autowired
	private FuturEmbargoRepository repo;
	
	/* Run application */
	@Override
	public void run(String... args) {
		
		if(args.length > 0) {
			String date = args[0];
			
			if(!dateHelper.isValidFormat(date))
				System.exit(1);

			int pageSize = defaultPageSize;
			
			if(args.length == 2) {
				try {
					int userPageSize = Integer.parseInt(args[1]);
					if(userPageSize <= maxPageSize && userPageSize >= 1)
						pageSize = userPageSize;
				}
				catch (NumberFormatException e) {
					log.warn("Second Argument {} could not be parsed, default page size {} is used", args[1], defaultPageSize);
				}
			}

			ArticleData artData = client.getNotifications(date, pageSize, null);
			int numOfRessources = artData.getTotal();
			int numberOfPages = artData.calcNumOfPages(pageSize);
			
			if(numOfRessources > 0) {
				/* Step 1: Read database for exceeded embargodates */
				repo.findAll().forEach( entity -> {
					
					Notification notification = client.getNotification(entity.getNotificationId());
					Metadata metaData = notification.getMetadata();
					Embargo embargo = notification.getEmbargo();
					
					if(!dateHelper.isDateExceeded(entity.getDate(), embargo.getDuration())) {
						String zipUrl = notification.getZipFileUrl();
						
						ZipHelper.getZipFromURL(zipUrl, apiKey, zipFilePath);
						ZipHelper.extractZip(zipFilePath, tmpDirPath);						
			
						// if doi does not exist, send xml and pdf to frl
						client.sendToFRL(metaData, embargo, tmpDirPath);
						
						repo.deleteById(entity.getNotificationId());
					}
				});		
				/* Read database for exceeded embargodates END */
				
				/* Step 2: Read API of deepgreen */
				for(int page = 1; page <= numberOfPages; page++) {
					 
					ArticleData data = client.getNotifications(date, pageSize, page);
					List<Notification> notificationList = data.getNotifications();
					
					notificationList.stream().forEach( notification -> {

						Metadata metaData = notification.getMetadata();
						Embargo embargo = notification.getEmbargo();
						String zipUrl = notification.getZipFileUrl();
						
						ZipHelper.getZipFromURL(zipUrl, apiKey, zipFilePath);
						ZipHelper.extractZip(zipFilePath, tmpDirPath);						
						
						// check if embargodate exceeded
						if(dateHelper.isDateExceeded(metaData.getDate(), embargo.getDuration()) && 
						   !repo.existsById(notification.getId())) {
							log.info("EmbargoDate of notification {} exceeded, write to database", notification.getId());
							FuturEmbargo futurEmbargo = new FuturEmbargo();
							futurEmbargo.setNotificationId(notification.getId());
							futurEmbargo.setDate(metaData.getDate());
							repo.save(futurEmbargo);
							return;
						}
						
						// if doi does not exist, send xml and pdf to frl
						client.sendToFRL(metaData, embargo, tmpDirPath);
					});
				}
				/* Read API of deepgreen END */	
			}
			if(numOfRessources == 0) {
				log.info("No Ressources to fetch");
			}
		}
		else {
			log.error("Date argument is missing or is not in format yyyy-MM-DD, i.e. 2021-08-22");
			log.error("Optionally you can put a second numerical argument, describing the pageSize");
		}
		
	}

}
