package de.hbznrw.deepgreen.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import lombok.extern.slf4j.Slf4j;

/**
 * Some helpful XML functions
 * @author Alessio Pellerito
 *
 */
@Slf4j
@Component
public class XmlUtil {	
	
	@Autowired
	private DocumentBuilderFactory documentFactory;
	
	@Autowired
	private TransformerFactory transformerFactory; 
	
	
	/**
	 * Creates a DOM Document for the provided xml File
	 * 
	 * @param xmlFile 	a XML file
	 * @return 			the DOM Document
	 * @throws ParserConfigurationException if the parser do not support this feature
	 * @throws SAXException if an error occurs with the SaxParser
	 * @throws IOException if an error occurs parsing the file
	 * 
	 */
	private Document createDocument(File xml) {
		try {
			documentFactory.setValidating(false);
			documentFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			return documentFactory.newDocumentBuilder().parse(xml);
		} catch (ParserConfigurationException e) {
			log.error("The parser do not support the requested features");
		} catch (SAXException e) {
			log.error("An error occurred with the SaxParser");
		} catch (IOException e) {
			log.error("An error occurred parsing the file");
		}
		return null;
	}
	
	/**
	 * Removes the doctype line from the specified XML File
	 * 
	 * @param xmlFile 	a XML file
	 * @return 			the XML File without doctype line
	 * @throws IOException if an error occurs parsing the file
	 * @throws TransformerConfigurationException if an error occurs with the Transformer configuration
	 * @throws TransformerException if an error occurs during transformation process
	 */
	public File removeDoctypeFromXmlFile(File xmlFile) {
		try {
			Document document = createDocument(xmlFile);
			
			Transformer transformer = transformerFactory.newTransformer();
			transformer.transform(new DOMSource(document), new StreamResult(new FileWriter(xmlFile,false)));
			
			return xmlFile;

		} catch (TransformerConfigurationException e) {
			log.error("Transformer configuration error");
		} catch (TransformerException e) {
			log.error("An error occurred during transformation process");
		} catch (IOException e) {
			log.error("An error occurred writing the file");
		}
		return xmlFile;	
	}
	
	public boolean hasTagAttribute(File xml, String tag, String attribKey, String attribValue) {
		// <contrib contrib-type="author">
		Document document = createDocument(xml);
		if(document != null) {
			NodeList nodeList = document.getElementsByTagName(tag);
			for (int i = 0; i < nodeList.getLength(); i++) {
	            Element contribElement = (Element) nodeList.item(i);
	            String value = contribElement.getAttribute(attribKey);
	            if(value.isEmpty() || !value.equalsIgnoreCase(attribValue)) {
	            	continue;
	            }
	            return true; 
			}     
		}
		return false;
	}

}
