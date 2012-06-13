/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informa��o Ltda.

 Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; vers�o 2 da Licen�a.
 Este programa � distribu�do na expectativa de que seja �til, por�m, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU 
 ADEQUA��O A UMA FINALIDADE ESPEC�FICA.
 
 Consulte a GNU GPL para mais detalhes.
 Voc� deve ter recebido uma c�pia da GNU GPL junto com este programa; se n�o, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.ibpm.component;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class FormularioParser extends DefaultHandler {
	
	private String texto;
	
	private List<Map<String, Object>> attributes = new ArrayList<Map<String,Object>>();
	private Map<String,List<SelectItem>> radios = new HashMap<String, List<SelectItem>>();
	
	public FormularioParser(String texto) {
		this.texto = "<?xml version='1.0' encoding='ISO-8859-1'?>" +
		"<!DOCTYPE composition PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>" +
		"<body>" + texto + "</body>";
	}

	public void parse() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(false);
 
        try {
    		factory.setFeature("http://apache.org/xml/features/"
    				+ "nonvalidating/load-external-dtd", false);
            SAXParser saxParser = factory.newSAXParser();
            InputSource is = new InputSource(new StringReader(texto));
			saxParser.parse( is, this );
        } catch (SAXException e) {
        	e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
 
	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		if ("input".equals(name) || "textarea".equals(name)) {
			Map<String,Object> m = getMap(attributes);
			if ("radio".equals(m.get("type"))) {
				String label = (String) m.get("title");
				List<SelectItem> items = radios.get(m.get("name"));
				if (items == null) {
					items = new ArrayList<SelectItem>();
					radios.put((String) m.get("name"), items);
					m.put("title", m.get("name"));
					this.attributes.add(m);
				}
				items.add(new SelectItem(m.get("id"), label));
				m.put("items", items);
				m.put("componentType", "radio");
			} else {
				m.put("componentType", name);				
				this.attributes.add(m);
			}
		}
	}

	private Map<String, Object> getMap(Attributes att) {
		Map<String,Object> ret = new HashMap<String,Object>();
		for (int i = 0; i < att.getLength(); i++) {
			ret.put(att.getQName(i), att.getValue(i));
		}
		return ret;
	}

	public List<Map<String,Object>> getAttributes() {
		return attributes;
	}


}