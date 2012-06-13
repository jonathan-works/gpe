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
package br.com.infox.ibpm.jbpm;

import java.net.URL;

import org.dom4j.Element;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.NodeCollection;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.jpdl.JpdlException;
import org.jbpm.util.ClassLoaderUtil;
import org.xml.sax.InputSource;


public class JpdlXmlReader extends org.jbpm.jpdl.xml.JpdlXmlReader {

	private static final long serialVersionUID = 1L;

	public JpdlXmlReader(String xmlResource) {
		super((InputSource) null);
	    URL resourceURL = ClassLoaderUtil.getClassLoader().getResource(xmlResource);
	    if (resourceURL == null) {
	      throw new JpdlException("resource not found: " + xmlResource);
	    }
	    this.inputSource = new InputSource(resourceURL.toString()); 
	}
	
	public JpdlXmlReader(InputSource source) {
		super(source);
	}
	
	@Override
	/**
	 * Tratamento da descri��o
	 */
	public ProcessDefinition readProcessDefinition() {
		ProcessDefinition definition = super.readProcessDefinition();
		String description = definition.getDescription();
		if (description != null) {
			Element root = document.getRootElement();
			definition.setDescription(root.elementText("description"));
		}
		return definition;
	}
	
	@Override
	/**
	 * Tratamento da descri��o
	 */
	public void readNode(Element nodeElement, Node node,
			NodeCollection nodeCollection) {
		super.readNode(nodeElement, node, nodeCollection);
		if(node.getDescription() != null) {
			node.setDescription(nodeElement.elementText("description"));
		}
	}

}