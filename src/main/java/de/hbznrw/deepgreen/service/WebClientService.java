package de.hbznrw.deepgreen.service;


import java.io.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import static de.hbznrw.deepgreen.constants.QueryParam.*;

import de.hbznrw.deepgreen.models.Notification;
import de.hbznrw.deepgreen.models.Resource;
import de.hbznrw.deepgreen.properties.DeepgreenProperties;
import de.hbznrw.deepgreen.properties.ServerProperties;
import de.hbznrw.deepgreen.utils.XmlUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * This client class manages all the different requests
 * @author Alessio Pellerito
 *
 */
@Component
@Slf4j
@Data
public class WebClientService {

	@Autowired
	private WebClient webClient;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private DeepgreenProperties prop;
	
	@Autowired
	private ServerProperties server;
	
	@Autowired
	private Resource attr;

	@Autowired
	private XmlUtil xmlUtil;
	
	
	/**
	 * 
	 * Get one specific resource from the deepgreen api in json format and 
	 * maps this to a java object
	 * 
	 * @param notificationId the id of the specific resource
	 * @return Mapped Java Class 
	 */
	public Notification getNotification(String notificationId) {
		return webClient.get()
						.uri(prop.getNotificationURL() + "/" + notificationId, uriBuilder -> uriBuilder
								.queryParam(APIKEY, prop.getApiKey())
								.build() )
						.retrieve()
						.bodyToMono(Notification.class)
						.block();
	}
	
	/**
	 * 
	 * Creates a new sub Resource under the specified parent resource
	 *  
	 * @param type the type of the Resource
	 * @param parentPid the identifier of the parent resource
	 * @return name of the new created child Resource
	 */
	public void updateResource(String type, String frlId) {
		attr.setContentType(type);

		webClient.post() 
				 .uri(server.getResourceURL() + "/" + frlId)
				 .contentType(MediaType.APPLICATION_JSON)
				 .headers(h -> h.setBasicAuth(server.getApiUser(), server.getApiPassword()))
				 .bodyValue(attr)
				 .retrieve()
                 .toBodilessEntity()  
                 .block();
	}
	
	/**
	 * 
	 * Sends the xml file to a to.science.api endpoint, which will map the file 
	 * to the rdf format and then send it to the specified frl ressource.
	 * 
	 * @param xmlFile the xml file that has to be send to frl
	 * @param nameOfRessource the name of the frl ressource, i.e. frl:640838
	 * @param embargoDuration value as month of the embargo duration
	 */
	public void sendXmlToResource(File xmlFile, String nameOfRessource, int embargoDuration, String deepgreenId) {
		File clearedXmlFile = xmlUtil.removeDoctypeFromXmlFile(xmlFile);
		
		if(clearedXmlFile != null) {
			webClient.post()
					 .uri(String.format(server.getResourceURL() + "/%s/DeepGreen?" + EMBARGODURATION + "=%d&" + DEEPGREENID + "=%s", nameOfRessource, embargoDuration, deepgreenId))
			         .contentType(MediaType.APPLICATION_XML)
			         .headers(h -> h.setBasicAuth(server.getApiUser(), server.getApiPassword()))
			         .body(BodyInserters.fromResource(new FileSystemResource(clearedXmlFile)))
			         .retrieve()
			         .toBodilessEntity()
			         .block();
		}
		else
			log.error("File does not exist");
	}
	
}
