package br.com.infox.epp.service.startup;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class RecursoStarter {

	private Document dom;
	private static String PATH = "C:\\jbdevstudio\\workspaceEpa41\\EPA\\WEB-INF\\src\\model\\recursos.xml";

	public static void main(String[] args) {
		RecursoStarter rs = new RecursoStarter();
		rs.parseDocument();
	}
	
	private void parseDocument(){
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			dom = db.parse(new File(PATH));
			Element docEle = dom.getDocumentElement();
			NodeList nl = docEle.getElementsByTagName("recurso");
			if(nl != null && nl.getLength() > 0) {
				for(int i = 0 ; i < nl.getLength();i++) {
					Element el = (Element)nl.item(i);
					String identificador = el.getAttribute("identificador");
					String nome = el.getAttribute("nome");
					String descricao = el.getAttribute("descricao");
					System.out.println(identificador+nome+descricao);
				}
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}