package de.hbznrw.deepgreen.service;

import static de.hbznrw.deepgreen.constants.ContentType.ARTICLE;
import static de.hbznrw.deepgreen.constants.ContentType.XML;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.hbznrw.deepgreen.models.Embargo;
import de.hbznrw.deepgreen.models.Notification;
import de.hbznrw.deepgreen.properties.DeepgreenProperties;
import de.hbznrw.deepgreen.utils.FileUtil;

@Service
public class PublissoService {
	
	@Autowired
	private WebClientService webClient;
	
	@Autowired
	private DeepgreenProperties prop;
	
	/**
	 * Uploads the pdf and the xml file to the (child-)ressource if the ressource 
	 * with doi does not exist yet
	 * 
	 * @param metaData the metadata values from the respective notification
	 * @param embargo  the embargo values from the respective notification
	 * @param tmpPath  the path where the files are
	 */
	public void upload(Embargo embargo, Notification notification , String tmpPath, String frlId) {

		File xmlFile = FileUtil.getFileBySuffix(XML, tmpPath);
			
		webClient.updateResource(ARTICLE, frlId);
		webClient.sendXmlToResource(xmlFile, frlId, embargo.getDuration(), notification.getId());
		
		FileUtil.moveFileToPath(xmlFile, prop.getXmlFilesPath());

	}

}
