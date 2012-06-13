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
package br.com.itx.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public final class XmlUtil {
	
	private static final String encoding = "ISO-8859-1";
	
	private XmlUtil() { }
	
	public static Document readDocument(File file) {
		Document doc = null;
		try {
			SAXBuilder builder = getSAXBuilder();
			if (file != null && file.isFile()) {
				doc = builder.build(file);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
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
					Writer writer = new OutputStreamWriter(fout, encoding);
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
				ex.printStackTrace();
			}
		}
	}
	
	public static void defineFormat(Format format) {
		format.setOmitDeclaration(false);
		format.setExpandEmptyElements(false);
		format.setEncoding(encoding);
		format.setIndent("    ");
		format.setTextMode(Format.TextMode.TRIM_FULL_WHITE);
	}
	
}