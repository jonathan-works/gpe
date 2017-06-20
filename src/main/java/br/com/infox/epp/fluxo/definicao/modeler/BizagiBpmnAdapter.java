package br.com.infox.epp.fluxo.definicao.modeler;

import java.util.Collection;
import java.util.Iterator;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.impl.BpmnModelConstants;
import org.camunda.bpm.model.bpmn.instance.Association;
import org.camunda.bpm.model.bpmn.instance.BaseElement;
import org.camunda.bpm.model.bpmn.instance.Collaboration;
import org.camunda.bpm.model.bpmn.instance.Definitions;
import org.camunda.bpm.model.bpmn.instance.EventDefinition;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.IntermediateThrowEvent;
import org.camunda.bpm.model.bpmn.instance.Lane;
import org.camunda.bpm.model.bpmn.instance.LaneSet;
import org.camunda.bpm.model.bpmn.instance.Participant;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.Text;
import org.camunda.bpm.model.bpmn.instance.TextAnnotation;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnDiagram;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnLabel;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnPlane;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.camunda.bpm.model.bpmn.instance.dc.Bounds;
import org.camunda.bpm.model.bpmn.instance.di.Waypoint;

import br.com.infox.seam.exception.BusinessException;

class BizagiBpmnAdapter implements BpmnAdapter {
	
	private BpmnModelInstance bizagiBpmnModel;
	private BpmnModelInstance normalizedModel;
	private Process normalizedProcess;
	private BpmnDiagram bizagiDiagram;
	private BpmnPlane normalizedPlane;
	
	@Override
	public BpmnModelInstance checkAndConvert(BpmnModelInstance bpmnModel) {
		Definitions definitions = bpmnModel.getDefinitions();
		if (definitions.getTargetNamespace() == null || !definitions.getTargetNamespace().contains("bizagi")) {
			return bpmnModel;
		}
		bizagiBpmnModel = bpmnModel;
		normalizeBpmnModel();
		return normalizedModel;
	}
	
	private void normalizeBpmnModel() {
		Process bizagiProcess = bizagiBpmnModel.getModelElementsByType(Process.class).iterator().next();
		normalizedModel = EppBpmn.createProcess(bizagiProcess.getId()).name(bizagiProcess.getName()).done();
		normalizedProcess = normalizedModel.getModelElementById(bizagiProcess.getId());
		removeUnnecessaryProcessesAndParticipants();
		putNodesInLanes();
		copyNodes();
		copyAnnotations();
		copyDiagram();
	}
	
	private void copyAnnotations() {
		for (TextAnnotation bizagiTextAnnotation : bizagiBpmnModel.getModelElementsByType(TextAnnotation.class)) {
			TextAnnotation textAnnotation = normalizedModel.newInstance(TextAnnotation.class);
			textAnnotation.setId(bizagiTextAnnotation.getId());
			Text text = normalizedModel.newInstance(Text.class);
			text.setTextContent(bizagiTextAnnotation.getText().getTextContent());
			textAnnotation.setText(text);
			normalizedProcess.addChildElement(textAnnotation);
		}
		
		for (Association bizagiAssociation : bizagiBpmnModel.getModelElementsByType(Association.class)) {
			if (bizagiAssociation.getTarget().getElementType().getTypeName().equals(BpmnModelConstants.BPMN_ELEMENT_TEXT_ANNOTATION)) {
				Association association = normalizedModel.newInstance(Association.class);
				association.setId(bizagiAssociation.getId());
				association.setSource((BaseElement) normalizedProcess.getModelInstance().getModelElementById(bizagiAssociation.getSource().getId()));
				association.setTarget((BaseElement) normalizedProcess.getModelInstance().getModelElementById(bizagiAssociation.getTarget().getId()));
				normalizedProcess.addChildElement(association);
			}
		}
	}

	private void putNodesInLanes() {
		for (Lane lane : bizagiBpmnModel.getModelElementsByType(Lane.class)) {
			Collection<FlowNode> nodes = DiagramUtil.getNodesInLaneGraphically(lane);
			lane.getFlowNodeRefs().addAll(nodes);
		}
	}
	
	private void copyNodes() {
		Process bizagiProcess = bizagiBpmnModel.getModelElementsByType(Process.class).iterator().next();
		for (FlowNode bizagiNode : bizagiProcess.getChildElementsByType(FlowNode.class)) {
			FlowNode node = normalizedModel.newInstance(bizagiNode.getElementType());
			node.setId(bizagiNode.getId());
			node.setName(bizagiNode.getName());
			if (node.getElementType().getTypeName().equals(BpmnModelConstants.BPMN_ELEMENT_INTERMEDIATE_THROW_EVENT)) {
				IntermediateThrowEvent bizagiEvent = (IntermediateThrowEvent) bizagiNode;
				if (!bizagiEvent.getEventDefinitions().isEmpty()) {
					copyEventDefinitions(bizagiEvent, (IntermediateThrowEvent) node);
				}
			}
			normalizedProcess.addChildElement(node);
		}
		
		LaneSet laneSet = normalizedModel.newInstance(LaneSet.class);
		normalizedProcess.addChildElement(laneSet);
		for (Lane bizagiLane : bizagiBpmnModel.getModelElementsByType(Lane.class)) {
			Lane lane = normalizedModel.newInstance(Lane.class);
			lane.setId(bizagiLane.getId());
			lane.setName(bizagiLane.getName());
			for (FlowNode bizagiNode : bizagiLane.getFlowNodeRefs()) {
				lane.getFlowNodeRefs().add((FlowNode) normalizedModel.getModelElementById(bizagiNode.getId()));
			}
			laneSet.getLanes().add(lane);
		}
		
		for (SequenceFlow bizagiSequenceFlow : bizagiProcess.getChildElementsByType(SequenceFlow.class)) {
			SequenceFlow sequenceFlow = normalizedModel.newInstance(SequenceFlow.class);
			sequenceFlow.setId(bizagiSequenceFlow.getId());
			sequenceFlow.setName(bizagiSequenceFlow.getName());
			normalizedProcess.addChildElement(sequenceFlow);
			if (bizagiSequenceFlow.getSource() != null) {
				FlowNode source = normalizedModel.getModelElementById(bizagiSequenceFlow.getSource().getId());
				sequenceFlow.setSource(source);
				source.getOutgoing().add(sequenceFlow);
			}
			if (bizagiSequenceFlow.getTarget() != null) {
				FlowNode target = normalizedModel.getModelElementById(bizagiSequenceFlow.getTarget().getId());
				sequenceFlow.setTarget(target);
				target.getIncoming().add(sequenceFlow);
			}
		}
	}
	
	private void copyEventDefinitions(IntermediateThrowEvent bizagiEvent, IntermediateThrowEvent event) {
		for (EventDefinition bizagiEventDefinition : bizagiEvent.getEventDefinitions()) {
			EventDefinition eventDefinition = event.getModelInstance().newInstance(bizagiEventDefinition.getElementType());
			eventDefinition.setId(bizagiEventDefinition.getId());
			event.getEventDefinitions().add(eventDefinition);
		}
	}

	private void copyDiagram() {
		Collaboration collaboration = normalizedModel.newInstance(Collaboration.class);
		Collaboration bizagiCollaboration = bizagiBpmnModel.getModelElementsByType(Collaboration.class).iterator().next();
		collaboration.setId(bizagiCollaboration.getId());
		normalizedModel.getDefinitions().addChildElement(collaboration);
		
		Participant participant = normalizedModel.newInstance(Participant.class);
		participant.setProcess(normalizedProcess);
		Participant bizagiParticipant = bizagiCollaboration.getParticipants().iterator().next();
		participant.setId(bizagiParticipant.getId());
		participant.setName(bizagiParticipant.getName());
		collaboration.getParticipants().add(participant);
		
		bizagiDiagram = bizagiBpmnModel.getDefinitions().getBpmDiagrams().iterator().next();
		BpmnDiagram diagram = normalizedModel.newInstance(BpmnDiagram.class);
		normalizedModel.getDefinitions().getBpmDiagrams().add(diagram);
		normalizedPlane = normalizedModel.newInstance(BpmnPlane.class);
		diagram.setBpmnPlane(normalizedPlane);
		normalizedPlane.setBpmnElement(collaboration);
		normalizedPlane.setId(bizagiDiagram.getBpmnPlane().getId());
		
		copyNodeShapes();
		copySequenceFlowEdges();
	}
	
	private void copyNodeShapes() {
		for (BpmnShape bizagiShape : bizagiDiagram.getBpmnPlane().getChildElementsByType(BpmnShape.class)) {
			BaseElement bpmnElement = normalizedModel.getModelElementById(bizagiShape.getBpmnElement().getId());
			if (bpmnElement == null) {
				continue;
			}
			
			BpmnShape shape = normalizedModel.newInstance(BpmnShape.class);
			normalizedPlane.getDiagramElements().add(shape);
			shape.setId(bizagiShape.getId());
			shape.setBpmnElement(bpmnElement);
			shape.setHorizontal(bizagiShape.isHorizontal());
			shape.setExpanded(bizagiShape.isExpanded());
			
			if (bizagiShape.getBounds() != null) {
				Bounds bounds = normalizedModel.newInstance(Bounds.class);
				bounds.setX(bizagiShape.getBounds().getX());
				bounds.setY(bizagiShape.getBounds().getY());
				bounds.setWidth(bizagiShape.getBounds().getWidth());
				bounds.setHeight(bizagiShape.getBounds().getHeight());
				shape.setBounds(bounds);
			}
			
			if (bizagiShape.getBpmnLabel() != null) {
				BpmnLabel label = normalizedModel.newInstance(BpmnLabel.class);
				label.setId(bizagiShape.getBpmnLabel().getId());
				Bounds labelBounds = bizagiShape.getBpmnLabel().getBounds();
				if (labelBounds != null) {
					Bounds bounds = normalizedModel.newInstance(Bounds.class);
					bounds.setX(labelBounds.getX());
					bounds.setY(labelBounds.getY());
					bounds.setWidth(labelBounds.getWidth());
					bounds.setHeight(labelBounds.getHeight());
					label.setBounds(bounds);
				}
				shape.setBpmnLabel(label);
			}
		}
	}
	
	private void copySequenceFlowEdges() {
		for (BpmnEdge bizagiEdge : bizagiDiagram.getBpmnPlane().getChildElementsByType(BpmnEdge.class)) {
			BaseElement bpmnElement = normalizedModel.getModelElementById(bizagiEdge.getBpmnElement().getId());
			if (bpmnElement == null) {
				continue;
			}

			BpmnEdge edge = normalizedModel.newInstance(BpmnEdge.class);
			edge.setId(bizagiEdge.getId());
			edge.setBpmnElement(bpmnElement);
			for (Waypoint bizagiWaypoint : bizagiEdge.getWaypoints()) {
				Waypoint waypoint = normalizedModel.newInstance(Waypoint.class);
				waypoint.setX(bizagiWaypoint.getX());
				waypoint.setY(bizagiWaypoint.getY());
				edge.getWaypoints().add(waypoint);
			}
			
			if (bizagiEdge.getBpmnLabel() != null) {
				BpmnLabel label = normalizedModel.newInstance(BpmnLabel.class);
				label.setId(bizagiEdge.getBpmnLabel().getId());
				Bounds labelBounds = bizagiEdge.getBpmnLabel().getBounds();
				if (labelBounds != null) {
					Bounds bounds = normalizedModel.newInstance(Bounds.class);
					bounds.setX(labelBounds.getX());
					bounds.setY(labelBounds.getY());
					bounds.setWidth(labelBounds.getWidth());
					bounds.setHeight(labelBounds.getHeight());
					label.setBounds(bounds);
				}
				edge.setBpmnLabel(label);
			}
			normalizedPlane.addChildElement(edge);
		}
	}
	
	private void removeUnnecessaryProcessesAndParticipants() {
		Definitions definitions = bizagiBpmnModel.getDefinitions();
		Collection<Process> processes = bizagiBpmnModel.getModelElementsByType(Process.class);
		Collection<Participant> participants = bizagiBpmnModel.getModelElementsByType(Participant.class);
		for (Process process : processes) {
			if (process.getChildElementsByType(FlowNode.class).isEmpty()) {
				Iterator<Participant> it = participants.iterator();
				while (it.hasNext()) {
					Participant participant = it.next();
					if (participant.getProcess().equals(process)) {
						if (!definitions.getBpmDiagrams().isEmpty()) {
							BpmnShape participantShape = (BpmnShape) participant.getDiagramElement();
							if (participantShape != null) {
								participantShape.getParentElement().removeChildElement(participantShape);
							}
						}
						participant.getParentElement().removeChildElement(participant);
						it.remove();
						break;
					}
				}
				process.getParentElement().removeChildElement(process);
			}
		}
		if (bizagiBpmnModel.getModelElementsByType(Process.class).isEmpty()) {
			throw new BusinessException("A definição de processo não contém um processo com nós");
		}
	}
}
