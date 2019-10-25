package com.techelevator.lang;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Strings {
	private static Map<String,String> strings = new HashMap<>();
	
	public Strings(String language) throws ParserConfigurationException, SAXException, IOException {
		// read language file
		File file = new File(Strings.class.getResource(language+".xml").getFile());
		if( !file.exists() ) {
			throw new FileNotFoundException(String.format("Language file for %s not found", language.toLowerCase()));
		}
		parseFile(file);
	}
	
	private void parseFile(File file) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(file);
		doc.getDocumentElement().normalize();
		
		NodeList nList = doc.getElementsByTagName("string");
		
		for( int i=0; i < nList.getLength(); i++ ) {
			Node node = nList.item(i);
			
			String internalName = ((Element)node).getAttribute("id");
			String string = node.getTextContent();
			
			if( internalName == null || string == null ) {
				continue;
			}
			strings.put(internalName, string);
		}
	}
	
	public String get(String term) {
		return strings.get(term);
	}
}
