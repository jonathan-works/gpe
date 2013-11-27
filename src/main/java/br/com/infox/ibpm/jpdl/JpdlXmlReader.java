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
package br.com.infox.ibpm.jpdl;

import java.net.URL;

import org.dom4j.Element;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.NodeCollection;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.jpdl.JpdlException;
import org.jbpm.util.ClassLoaderUtil;
import org.xml.sax.InputSource;


public class JpdlXmlReader extends org.jbpm.jpdl.xml.JpdlXmlReader {

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
	 * Tratamento da descrição
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
	 * Tratamento da descrição
	 */
	public void readNode(Element nodeElement, Node node,
			NodeCollection nodeCollection) {
		super.readNode(nodeElement, node, nodeCollection);
		if(node.getDescription() != null) {
			node.setDescription(nodeElement.elementText("description"));
		}
	}

}