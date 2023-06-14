package de.hbznrw.deepgreen;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.hbznrw.deepgreen.models.Embargo;
import de.hbznrw.deepgreen.models.Notification;
import de.hbznrw.deepgreen.properties.DeepgreenProperties;
import de.hbznrw.deepgreen.service.PublissoService;
import de.hbznrw.deepgreen.service.WebClientService;
import de.hbznrw.deepgreen.utils.ZipUtil;

/**
 * 
 * @author Alessio Pellerito
 *
 */

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
	
	/* Injecting objects */
	@Autowired
	private DeepgreenProperties prop;
	
	@Autowired
	private WebClientService webClient;
	
	@Autowired
	private PublissoService publisso;
	
	/* Run application */
	@Override
	public void run(String... args) {
		
		String line;
		try (BufferedReader br = new BufferedReader(new FileReader("/opt/deepgreen/test.csv"))) {
	        while ((line = br.readLine()) != null) {
	
	          String[] split = line.split(";");
	          Notification notification = webClient.getNotification(split[1]);
	          Embargo embargo = notification.getEmbargo();
	          List<Notification.Link> links = notification.getLinks();
	          
	          String zipUrl = links.get(0).getUrl().toString();
				
			  ZipUtil.copyURLTo(zipUrl, prop.getApiKey(), Paths.get(prop.getZipFilePath()));
			  ZipUtil.extract(prop.getZipFilePath(), prop.getTmpDirPath());						
	
			  publisso.upload(embargo, notification, prop.getTmpDirPath(), split[0]);

	          
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
		
	}

}
