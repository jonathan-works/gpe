package br.com.infox.ibpm.jpdl;

import java.net.URL;

import org.dom4j.Element;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.NodeCollection;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.jpdl.JpdlException;
import org.jbpm.jpdl.xml.JpdlXmlReader;
import org.jbpm.util.ClassLoaderUtil;
import org.xml.sax.InputSource;

public class InfoxJpdlXmlReader extends JpdlXmlReader {

    public InfoxJpdlXmlReader(String xmlResource) {
        super((InputSource) null);
        URL resourceURL = ClassLoaderUtil.getClassLoader().getResource(xmlResource);
        if (resourceURL == null) {
            throw new JpdlException("resource not found: " + xmlResource);
        }
        this.inputSource = new InputSource(resourceURL.toString());
    }

    public InfoxJpdlXmlReader(InputSource source) {
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
        if (node.getDescription() != null) {
            node.setDescription(nodeElement.elementText("description"));
        }
    }

}
