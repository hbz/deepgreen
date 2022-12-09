package de.hbznrw.deepgreen.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import de.hbznrw.deepgreen.models.Notification;
import de.hbznrw.deepgreen.properties.DeepgreenProperties;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailSenderService {
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private DeepgreenProperties prop;
	
	@Value("${spring.mail.from}")
	String emailFrom;

	/**
	 * It sends an email to the specified address with the specified messages
	 * 
	 * @param toEmail email adress to send to
	 * @param subject message in subject line
	 * @param body main message
	 */
	public void sendEmail(String toEmail, String subject, String body) {
		SimpleMailMessage message = new SimpleMailMessage();
		
		message.setFrom(emailFrom);
		message.setTo(toEmail);
		message.setText(body);
		message.setSubject(subject);
		
		mailSender.send(message);
		log.info("Email an {} gesendet!", toEmail);
	}
	
	/**
	 * It builds an email main message for resources without authors
	 * 
	 * @param notification to identify the resource
	 * @return the created message
	 */
	public String messageNoAuthors(Notification notification) {
		String newLine = System.getProperty("line.separator");
		return new StringBuilder()
					.append("Folgende DeepGreen-Publikation konnte nicht automatisch eingespielt werden:")
					.append(newLine)
					.append(prop.getNotificationURL() + "/" + notification.getId())
					.append(newLine)
					.append(newLine)
					.append("Der Grund dafür ist:")
					.append(newLine)
					.append("Ressource hat keine Autoren")
					.append(newLine)
					.append(newLine)
					.append("Bitte prüfen und ggf. mit dem FRL-Nutzer ZBMdeepgreen händisch erfassen.")
					.toString();
	}
	
	/**
	 * It gets the subject for resources without authors
	 * 
	 * @return the subject
	 */
	public String subjectNoAuthors() {
		return "Bitte DeepGreen-Publikation prüfen";
	}
	
	
}
