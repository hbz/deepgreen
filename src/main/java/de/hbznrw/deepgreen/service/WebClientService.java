package de.hbznrw.deepgreen.service;


import java.io.File;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static de.hbznrw.deepgreen.constants.ContentType.ARTICLE;
import static de.hbznrw.deepgreen.constants.ContentType.FILE;
import static de.hbznrw.deepgreen.constants.ContentType.PDF;
import static de.hbznrw.deepgreen.constants.ContentType.XML;
import static de.hbznrw.deepgreen.constants.QueryParam.*;

import de.hbznrw.deepgreen.models.ArticleData;
import de.hbznrw.deepgreen.models.Embargo;
import de.hbznrw.deepgreen.models.Metadata;
import de.hbznrw.deepgreen.models.Notification;
import de.hbznrw.deepgreen.models.Resource;
import de.hbznrw.deepgreen.properties.DeepgreenProperties;
import de.hbznrw.deepgreen.properties.ServerProperties;
import de.hbznrw.deepgreen.utils.FileUtil;
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
	private XmlUtil xmlHelper;
	
	/**
	 * 
	 * Gets all resources from the deepgreen api from the specified date in json 
	 * format and maps these to a java object
	 * 
	 * @param arg first argument from console as date format yyyy-mm-dd
	 * @return Mapped Java Class 
	 */
	public ArticleData getNotifications(String date, Integer pageSize, Integer page) {
		return webClient.get()
						.uri(prop.getApiURL(), uriBuilder -> uriBuilder
								.queryParam(SINCE, date)
								.queryParamIfPresent(PAGESIZE, Optional.ofNullable(pageSize))
								.queryParamIfPresent(PAGE, Optional.ofNullable(page))
								.build() )
						.retrieve()
						.bodyToMono(ArticleData.class)
						.block();
	}
	
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
	 * Tests whether the publisherVersion.@id (doi) already exists by calling the elasticsearch api.
	 * 
	 * @param doiValue
	 * @return true if the doi already exists, false otherwise
	 */
	public boolean doiExists(String doiValue) {
    	
		// *** Query Requestbody ***
		// "query":{"bool": {"should": [
		// {"term": {"@id": "frl:6435877"}},
		// {"term": {"publisherVersion.@id": "https://doi.org/10.1024/0300-9831/a000643"}}]}}}
			
		ObjectNode doiNode = mapper.createObjectNode();
		doiNode.put("doi", doiValue);
		
		ObjectNode publisherVersionNode = mapper.createObjectNode();
		publisherVersionNode.put("publisherVersion.@id", "https://doi.org/" + doiValue);
		
		ObjectNode term1Node = mapper.createObjectNode();
		term1Node.set("term", doiNode);
		
		ObjectNode term2Node = mapper.createObjectNode();
		term2Node.set("term", publisherVersionNode);
		
		ArrayNode shouldArray = mapper.createArrayNode();
		shouldArray.add(term1Node);
		shouldArray.add(term2Node);
		
		ObjectNode boolNode = mapper.createObjectNode();
		boolNode.set("should", shouldArray);
		
		ObjectNode queryNode = mapper.createObjectNode();
		queryNode.set("bool", boolNode);
		
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.set("query", queryNode);

		JsonNode node = webClient.post()
								 .uri(server.getElasticsearchURL())
								 .contentType(MediaType.APPLICATION_JSON)
								 .bodyValue(rootNode)
								 .retrieve()
				                 .bodyToMono(JsonNode.class)   
				                 .block();

		return node.at("/hits/total").asInt() > 0;
	}
	
	/**
	 * 
	 * Creates a new sub Resource under the specified parent resource
	 *  
	 * @param type the type of the Resource
	 * @param parentPid the identifier of the parent resource
	 * @return name of the new created child Resource
	 */
	public String createChildResource(String type, String parentPid) {
		attr.setContentType(type);
		attr.setParentPid(parentPid);

		JsonNode node = webClient.post() 
								 .uri(server.getFrlURL())
								 .contentType(MediaType.APPLICATION_JSON)
								 .headers(h -> h.setBasicAuth(server.getApiUser(), server.getApiPassword()))
								 .bodyValue(attr)
								 .retrieve()
				                 .bodyToMono(JsonNode.class)   
				                 .block();
				                 
		log.info(node.get("text").asText());
		String textResponse = node.get("text").asText(); 
		return textResponse.substring(0, textResponse.indexOf(" "));
	}
	
	/**
	 * 
	 * Creates a new Resource
	 * 
	 * @param type the type of the Resource
	 * @return name of the new created Ressource
	 */
	public String createResource(String type) {
		return createChildResource(type, null);
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
		File clearedXmlFile = xmlHelper.removeDoctypeFromXmlFile(xmlFile);
		
		if(clearedXmlFile != null) {
			webClient.post()
					 .uri(String.format(server.getResourceURL() + "/%s/DeepGreen?" + EMBARGODURATION + "=%d&" + DEEPGREENID + "=%s", nameOfRessource, embargoDuration, deepgreenId))
			         .contentType(MediaType.APPLICATION_XML)
			         .headers(h -> h.setBasicAuth(server.getApiUser(), server.getApiPassword()))
			         .body(BodyInserters.fromResource(new FileSystemResource(clearedXmlFile)))
			         .retrieve()
			         .toBodilessEntity()
			         .block();
			
			FileUtil.moveFileToPath(xmlFile, prop.getXmlFilesPath());
		}
		else
			log.error("File does not exist");
	}
	
	/**
	 * Sends the pdf file to the specified frl resource
	 * 
	 * @param pdf the pdf file that has to be send to frl
	 * @param nameOfResource name of the frl Resource
	 */
	public void sendPdfToResource(File pdf, String nameOfResource) {
		MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
		bodyBuilder.part("data", new FileSystemResource(pdf));
		bodyBuilder.part("type", MediaType.APPLICATION_PDF);
		webClient.put()
		         .uri(String.format(server.getResourceURL() + "/%s/data", nameOfResource))
		         .headers(h -> h.setBasicAuth(server.getApiUser(), server.getApiPassword()))
		         .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
		         .retrieve()
		         .toBodilessEntity()
		         .block();
		
		FileUtil.delete(pdf.getAbsolutePath());
	}
	
	/**
	 * Sends the pdf and the xml file to the (child-)ressource if the ressource 
	 * with doi does not exist yet
	 * 
	 * @param metaData the metadata values from the respective notification
	 * @param embargo  the embargo values from the respective notification
	 * @param tmpPath  the path where the files are
	 */
	public void sendToFRL(Metadata metaData, Embargo embargo, Notification notification ,String tmpPath) {
		String doi = metaData.getDoi();
		
		File xmlFile = FileUtil.getFileBySuffix(XML, tmpPath);
		File pdfFile = FileUtil.getFileBySuffix(PDF, tmpPath);

		if (!xmlHelper.hasTagAttribute(xmlFile, "contrib", "contrib-type", "author")) {
			log.info("Resource with doi {} has no authors, no upload", doi);
			xmlFile.delete();
			pdfFile.delete();
			return;
		}
		
		if(doiExists(doi)) {
			log.info("Resource with doi {} already exists, no upload", doi);
			xmlFile.delete();
			pdfFile.delete();
			return;
		}
			
		String mainResource = createResource(ARTICLE);
		sendXmlToResource(xmlFile, mainResource, embargo.getDuration(), notification.getId());
		
		String childResource = createChildResource(FILE, mainResource);
		sendPdfToResource(pdfFile, childResource);

	}
	
	
}
