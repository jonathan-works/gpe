package br.com.infox.epp.fluxo.definicao.modeler;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.util.IteratorIterable;

import com.google.common.base.Strings;

import br.com.infox.seam.exception.BusinessRollbackException;

public class BpmnCleaner {
    private static final String ATTR_BPMN_ELEMENT = "bpmnElement";
    private static final String ATTR_ID = "id";
    private static final String ATTR_SOURCE_REF = "sourceRef";
    private static final String ATTR_TARGET_REF = "targetRef";
    private static final String ELEMENT_BPMN_SHAPE = "BPMNShape";
    private static final String ELEMENT_BPMN_EDGE = "BPMNEdge";
    private static final String ELEMENT_INCOMING = "incoming";
    private static final String ELEMENT_OUTGOING = "outgoing";
    private static final String ELEMENT_GROUP = "group";
    private static final String ELEMENT_SEQUENCE_FLOW = "sequenceFlow";
    private static final String ELEMENT_ASSOCIATION = "association";
    private static final String ELEMENT_FLOW_NODE_REF = "flowNodeRef";
    
    private List<String> changedIds = new ArrayList<>();
    private List<String> allIds = new ArrayList<>();
    private Map<String, Element> edgesByElementId = new HashMap<>();
    private Map<String, Element> shapesByElementId = new HashMap<>();
    private List<String> groupIds = new ArrayList<>();
    private Map<String, List<Element>> incomingAndOutgoingReferencesByElementId = new HashMap<>();
    
    public String cleanBpmn(String bpmn) {
        try {
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(new StringReader(bpmn));
            
            // Remove todos os grupos
            removeGroups(doc.getRootElement());
            
            // Corrige os ids
            normalizeIds(doc.getRootElement());
            
            // Atualiza as referências
            changeReferences(doc.getRootElement());
            
            // Carrega as referências de BPMNShape, BPMNEdge, incoming, outgoing, já removendo se possível
            // Também remove referências de flowNodeRef caso seja possível
            loadShapesEdgesTransitionAndNodeReferences(doc.getRootElement());
            
            // Remove sequenceFlows e associations que não possuam referência de source ou target
            // Remove também os elementos BPMNEdge e incoming/outgoing que referenciam a sequenceFlow/association removida
            removeUnreferencedSequenceFlowsAndAssociations(doc.getRootElement());
            
            // Remove elementos BPMNShape que referenciam nós inexistentes
            removeUnreferencedShapes();
            return new XMLOutputter(Format.getPrettyFormat()).outputString(doc);
        } catch (JDOMException | IOException e) {
            throw new BusinessRollbackException(e);
        }
    }

    private void removeUnreferencedShapes() {
        // Remove todos os elementos BPMNShape que referenciem nós inexistentes 
        for (String elementId : shapesByElementId.keySet()) {
            if (!allIds.contains(elementId)) {
                shapesByElementId.get(elementId).detach();
            }
        }
    }

    private void removeGroups(Element rootElement) {
        // Remove todos os elementos group
        IteratorIterable<Element> groups = rootElement.getDescendants(new ElementFilter(ELEMENT_GROUP));
        while (groups.hasNext()) {
            Element group = groups.next();
            if (!Strings.isNullOrEmpty(group.getAttributeValue(ATTR_ID))) {
                groupIds.add(group.getAttributeValue(ATTR_ID));
            }
            groups.remove();
        }
    }

    private void removeUnreferencedSequenceFlowsAndAssociations(Element rootElement) {
        IteratorIterable<Element> sequenceFlowsAndAssociations = rootElement.getDescendants(new FilterSequenceFlowsAndAssociations());
        while (sequenceFlowsAndAssociations.hasNext()) {
            Element element = sequenceFlowsAndAssociations.next();
            String sourceRef = element.getAttributeValue(ATTR_SOURCE_REF);
            String targetRef = element.getAttributeValue(ATTR_TARGET_REF);
            
            if (sourceRef == null || targetRef == null || !allIds.contains(sourceRef) || !allIds.contains(targetRef)) {
                // Remove a sequenceFlow ou association caso não possua referência de source ou target
                sequenceFlowsAndAssociations.remove();
                Element edge = edgesByElementId.get(element.getAttributeValue(ATTR_ID));
                if (edge != null) {
                    // Remove a edge correspondente
                    edge.detach();
                    if (incomingAndOutgoingReferencesByElementId.containsKey(element.getAttributeValue(ATTR_ID))) {
                        // Remove os nós incoming e outgoing que referenciam a sequenceFlow/association removida
                        for (Element reference : incomingAndOutgoingReferencesByElementId.get(element.getAttributeValue(ATTR_ID))) {
                            reference.detach();
                        }
                    }
                }
            }
        }
    }

    private void changeReferences(Element element) {
        if (element.getName().equalsIgnoreCase(ELEMENT_FLOW_NODE_REF) || element.getName().equalsIgnoreCase(ELEMENT_INCOMING)
                || element.getName().equalsIgnoreCase(ELEMENT_OUTGOING)) {
            // Se o elemento for flowNodeRef, incoming ou outgoing, a referência é o texto do nó
            if (changedIds.contains(element.getText())) {
                element.setText("Id_" + element.getText());
            }
        } else {
            for (Attribute attr  : element.getAttributes()) {
                // Altera a referência de todos os atributos cujo valor seja algum dos valores modificados
                if (!attr.getName().equals(ATTR_ID) && changedIds.contains(attr.getValue())) {
                    attr.setValue("Id_" + attr.getValue());
                }
            }
        }
        
        for (Element child : element.getChildren()) {
            changeReferences(child);
        }
    }

    private void normalizeIds(Element element) {
        String id = element.getAttributeValue(ATTR_ID);
        if (id != null) {
            if (id.matches("[0-9]+.*?")) {
                changedIds.add(id);
                id = "Id_" + id;
                element.setAttribute(ATTR_ID, id);
            }
            
            allIds.add(id);
        }
        
        for (Element child : element.getChildren()) {
            normalizeIds(child);
        }
    }
    
    private void loadShapesEdgesTransitionAndNodeReferences(Element element) {
        if (element.getName().equalsIgnoreCase(ELEMENT_OUTGOING) || element.getName().equalsIgnoreCase(ELEMENT_INCOMING)) {
            // Guarda elementos <incoming> e <outgoing> dos flowNodes para removê-los caso a sequenceFlow referenciada seja removida
            // durante o processo
            if (!incomingAndOutgoingReferencesByElementId.containsKey(element.getText())) {
                incomingAndOutgoingReferencesByElementId.put(element.getText(), new ArrayList<Element>());
            }
            incomingAndOutgoingReferencesByElementId.get(element.getText()).add(element);
        } else if (element.getName().equalsIgnoreCase(ELEMENT_BPMN_EDGE)) {
            // Guarda elementos BPMNEdge para removê-los caso suas sequenceFlows correspondentes sejam removidas
            // (já removendo caso não haja referência)
            if (element.getAttributeValue(ATTR_BPMN_ELEMENT) != null) {
                edgesByElementId.put(element.getAttributeValue(ATTR_BPMN_ELEMENT), element);
            } else {
                element.detach();
            }
        } else if (element.getName().equalsIgnoreCase(ELEMENT_BPMN_SHAPE)) {
            // Guarda elementos BPMNShape para removê-los caso seus nodes correspondentes sejam removidos
            // (já removendo caso não haja referência)
            if (element.getAttributeValue(ATTR_BPMN_ELEMENT) != null) {
                shapesByElementId.put(element.getAttributeValue(ATTR_BPMN_ELEMENT), element);
            } else {
                element.detach();
            }
        } else if (element.getName().equalsIgnoreCase(ELEMENT_FLOW_NODE_REF)) {
            // Remove elementos <flowNodeRef> caso não referenciem nós existentes
            if (!allIds.contains(element.getText())) {
                element.detach();
            }
        }
        
        for (Element child : element.getChildren()) {
            loadShapesEdgesTransitionAndNodeReferences(child);
        }
    }
    
    private static class FilterSequenceFlowsAndAssociations extends ElementFilter {
        private static final long serialVersionUID = 1L;
        
        @Override
        public Element filter(Object arg0) {
            Element element = super.filter(arg0);
            if (element != null && (element.getName().equalsIgnoreCase(ELEMENT_SEQUENCE_FLOW) || element.getName().equalsIgnoreCase(ELEMENT_ASSOCIATION))) {
                return element;
            }
            return null;
        }
    }
}
