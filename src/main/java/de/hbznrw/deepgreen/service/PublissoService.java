package de.hbznrw.deepgreen.service;

import static de.hbznrw.deepgreen.constants.ContentType.ARTICLE;
import static de.hbznrw.deepgreen.constants.ContentType.FILE;
import static de.hbznrw.deepgreen.constants.ContentType.PDF;
import static de.hbznrw.deepgreen.constants.ContentType.XML;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.hbznrw.deepgreen.models.Embargo;
import de.hbznrw.deepgreen.models.Metadata;
import de.hbznrw.deepgreen.models.Notification;
import de.hbznrw.deepgreen.utils.FileUtil;
import de.hbznrw.deepgreen.utils.XmlUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PublissoService {
	
	@Value("${spring.mail.to}")
	String emailTo;
	
	@Autowired
	private XmlUtil xmlUtil;
	
	@Autowired
	private EmailSenderService mailService;
	
	@Autowired
	WebClientService webClient;
	
	/**
	 * Uploads the pdf and the xml file to the (child-)ressource if the ressource 
	 * with doi does not exist yet
	 * 
	 * @param metaData the metadata values from the respective notification
	 * @param embargo  the embargo values from the respective notification
	 * @param tmpPath  the path where the files are
	 */
	public void upload(Metadata metaData, Embargo embargo, Notification notification , String tmpPath) {
		String doi = metaData.getDoi();
		
		File xmlFile = FileUtil.getFileBySuffix(XML, tmpPath);
		File pdfFile = FileUtil.getFileBySuffix(PDF, tmpPath);

		if (!xmlUtil.hasTagAttribute(xmlFile, "contrib", "contrib-type", "author")) {
			log.info("Resource with doi {} has no authors, no upload", doi);
			
			// send email to zbmed
			String subject = mailService.subjectNoAuthors();
			String message = mailService.messageNoAuthors(notification);			
			mailService.sendEmail(emailTo, subject, message);
			
			xmlFile.delete();
			pdfFile.delete();
			return;
		}
		
		if(webClient.doiExists(doi)) {
			log.info("Resource with doi {} already exists, no upload", doi);
			xmlFile.delete();
			pdfFile.delete();
			return;
		}
			
		String mainResource = webClient.createResource(ARTICLE);
		webClient.sendXmlToResource(xmlFile, mainResource, embargo.getDuration(), notification.getId());
		
		String childResource = webClient.createChildResource(FILE, mainResource);
		webClient.sendPdfToResource(pdfFile, childResource);

	}

}
