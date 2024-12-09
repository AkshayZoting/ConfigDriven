package com.webacces.util;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.webacces.model.User;

public class XMLParser {
	
	public static boolean validate(String filePath,String xpression,User user){
		try {
			File inputFile = new File(filePath);           //filePath="C:/data/Users.xml"
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = factory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();
			XPath xPath = XPathFactory.newInstance().newXPath();
			String expression = xpression;
			NodeList nList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);

			for (int i = 0; i < nList.getLength(); i++) {
				Node node = nList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element elem = (Element) node;
					if(user.getEmail().equalsIgnoreCase(elem.getElementsByTagName("email").item(0).getTextContent()) 
							&& user.getPassword().equals(elem.getElementsByTagName("password").item(0).getTextContent()))
					{
						user.setName(elem.getElementsByTagName("name").item(0).getTextContent());
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean addUser(String filePath,User user){
		
		try{
		if(!validate(filePath, "/users/user", user)){
			File xmlFile = new File(filePath);
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(xmlFile);   //Xml to Document
			Element documentElement = document.getDocumentElement();
			Element nodeElement = document.createElement("user");
			
			Element node = document.createElement("email");
			node.setTextContent(user.getEmail());
			nodeElement.appendChild(node);
			
			node = document.createElement("name");
			node.setTextContent(user.getName());
			nodeElement.appendChild(node);
			
			node = document.createElement("password");
			node.setTextContent(user.getPassword());
			nodeElement.appendChild(node);
			
			documentElement.appendChild(nodeElement);
			document.replaceChild(documentElement, documentElement);
			
			Transformer tFormer = TransformerFactory.newInstance().newTransformer();
			tFormer.setOutputProperty(OutputKeys.METHOD, "xml");
			Source source = new DOMSource(document);
			Result result = new StreamResult(xmlFile);   //Document to Xml
			tFormer.transform(source, result);
			
			return true;
			
		}
		 }catch(Exception e){
             e.printStackTrace();
         }
		return false;

	}
	
	public static void createUploadDataFile(List<LinkedHashMap<String, String>> gridData,String filePath){
		try {
	         DocumentBuilderFactory dbFactory =
	         DocumentBuilderFactory.newInstance();
	         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	         Document doc = dBuilder.newDocument();
	         
	         // root element
	         Element rootElement = doc.createElement("Rows");
	         doc.appendChild(rootElement);
	         if(gridData != null){
				
						for (Entry<String, String> rows : gridData.get(0).entrySet()) {
							
						Element rowElement = doc.createElement("Row");
				         rootElement.appendChild(rowElement);

				         // setting attribute to element
				         Attr attr = doc.createAttribute("field");
				         attr.setValue(rows.getKey());
				         rowElement.setAttributeNode(attr);
				         Attr attrVal = doc.createAttribute("Value");
				         attrVal.setValue(rows.getValue());
				         rowElement.setAttributeNode(attrVal);
					}
				}

	         // write the content into xml file
	         TransformerFactory transformerFactory = TransformerFactory.newInstance();
	         Transformer transformer = transformerFactory.newTransformer();
	         DOMSource source = new DOMSource(doc);
	         StreamResult result = new StreamResult(new File(filePath));
	         transformer.transform(source, result);
	         
	         
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
	}

}
