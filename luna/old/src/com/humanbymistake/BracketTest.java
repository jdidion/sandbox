package com.humanbymistake;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.FileInputStream;

public class BracketTest {
    public static void main(String[] args) {
        // parse the properties file
		Document doc = null;
		try {
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			doc = docBuilder.parse(new FileInputStream("brackettest.xml"));
		} catch (Exception ex) {
            ex.printStackTrace();
		}
        NodeList nodes = doc.getElementsByTagName("[]");
        System.out.println(nodes.getLength());
    }
}
