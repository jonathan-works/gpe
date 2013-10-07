/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.

 Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; versão 2 da Licença.
 Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 
 Consulte a GNU GPL para mais detalhes.
 Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.itx.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public final class XmlUtil {
	
	private static final String ENCODING = "ISO-8859-1";
	
	private static final LogProvider LOG = Logging.getLogProvider(XmlUtil.class);
	
	private XmlUtil() { }
	
	public static Document readDocument(File file) {
		Document doc = null;
		try {
			SAXBuilder builder = getSAXBuilder();
			if (file != null && file.isFile()) {
				doc = builder.build(file);
			}
		} catch (Exception ex) {
			LOG.error(".readDocument()", ex);
		}	
		return doc;
	}
	
	public static Document readDocument(InputStream file) {
		Document doc = null;
		try {
			SAXBuilder builder = getSAXBuilder();
			if (file != null) {
				doc = builder.build(file);
			}
		} catch (Exception ex) {
		    LOG.error(".readDocument()", ex);
		}	
		return doc;
	}
	
	public static SAXBuilder getSAXBuilder() {
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		builder.setFeature("http://apache.org/xml/features/"
				+ "nonvalidating/load-external-dtd", false);
		return builder;
	}

	public static void writeDocument(File file, Document doc) {
		if (file != null && doc != null) {
			try {
				file.getParentFile().mkdirs();
				FileOutputStream fout = new FileOutputStream(file);
				try {
					Writer writer = new OutputStreamWriter(fout, ENCODING);
					XMLOutputter jdom = new XMLOutputter();
					Format format = jdom.getFormat();
					defineFormat(format);
					jdom.setFormat(format);
					jdom.output(doc, writer);
				} catch (UnsupportedEncodingException err) {
					// ignorado
				}
				fout.close();
			} catch (Exception ex) {
			    LOG.error(".writeDocument()", ex);
			}
		}
	}
	
	public static void defineFormat(Format format) {
		format.setOmitDeclaration(false);
		format.setExpandEmptyElements(false);
		format.setEncoding(ENCODING);
		format.setIndent("    ");
		format.setTextMode(Format.TextMode.TRIM_FULL_WHITE);
	}
	
	@SuppressWarnings("unchecked")
	public static List<Element> getChildren(Element element, String name) {
		return element.getChildren(name, element.getNamespace());
	}
	
	/**
	 * Retorna torna o elemento filho na posição especificada no parâmetro index  
	 * @param element
	 * @param name
	 * @param index
	 * @return
	 */
	public static Element getChildByIndex(Element element, String name, int index) {
		return getChildren(element, name).get(index);
	}
	
	public static String getAttributeValue(Element element, String name) {
		String temp = element.getAttributeValue(name);
		if(temp == null) {
		    temp = element.getAttributeValue(name, element.getNamespace());
		}
		return temp;
	}
}