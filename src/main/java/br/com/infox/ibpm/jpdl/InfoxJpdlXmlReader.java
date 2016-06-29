package br.com.infox.ibpm.jpdl;

import java.io.Reader;
import java.net.URL;

import org.dom4j.Element;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.NodeCollection;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.instantiation.Delegation;
import org.jbpm.jpdl.JpdlException;
import org.jbpm.jpdl.xml.JpdlXmlReader;
import org.jbpm.jpdl.xml.Problem;
import org.jbpm.taskmgmt.def.TaskController;
import org.jbpm.util.ClassLoaderUtil;
import org.xml.sax.InputSource;

import br.com.infox.core.util.ReflectionsUtil;

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
    
    public InfoxJpdlXmlReader(Reader reader) {
    	super(reader);
	}

    @Override
    /**
     * Tratamento da descrição
     */
    public ProcessDefinition readProcessDefinition() {
    	ProcessDefinition definition;
    	try {
    		definition = super.readProcessDefinition();
    	} catch (JpdlException e) {
    		StringBuilder sb = new StringBuilder(e.getMessage());
    		for (Object o : e.getProblems()) {
    			sb.append("\n");
    			sb.append(((Problem) o).getDescription());
    		}
    		ReflectionsUtil.setValue(e, "detailMessage", sb.toString());
    		throw e;
    	}
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

    @Override
    protected TaskController readTaskController(Element taskControllerElement) {
    	TaskController taskController = new TaskController();
        if (taskControllerElement.attributeValue("class") != null) {
	        Delegation taskControllerDelegation = new Delegation();
	        taskControllerDelegation.read(taskControllerElement, this);
	        taskController.setTaskControllerDelegation(taskControllerDelegation);
	    }
        taskController.setVariableAccesses(readVariableAccesses(taskControllerElement));
        return taskController;
    }
}
