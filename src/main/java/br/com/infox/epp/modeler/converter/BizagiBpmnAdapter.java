package br.com.infox.epp.modeler.converter;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Definitions;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Lane;
import org.camunda.bpm.model.bpmn.instance.Participant;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnDiagram;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnPlane;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.camunda.bpm.model.bpmn.instance.dc.Bounds;
import org.camunda.bpm.model.bpmn.instance.di.DiagramElement;

public class BizagiBpmnAdapter {
	public void checkAndConvert(BpmnModelInstance bpmnModel) {
		Definitions definitions = bpmnModel.getDefinitions();
		if (!definitions.getTargetNamespace().contains("bizagi")) {
			return;
		}
		
		Collection<Process> processes = bpmnModel.getModelElementsByType(Process.class);
		Collection<Participant> participants = bpmnModel.getModelElementsByType(Participant.class);
		for (Process process : processes) {
			if (process.getChildElementsByType(FlowNode.class).isEmpty()) {
				Iterator<Participant> it = participants.iterator();
				while (it.hasNext()) {
					Participant participant = it.next();
					if (participant.getProcess().equals(process)) {
						if (!definitions.getBpmDiagrams().isEmpty()) {
							BpmnDiagram diagram = definitions.getBpmDiagrams().iterator().next();
							BpmnShape participantShape = getShapeForElement(participant.getId(), diagram);
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

		Participant processParticipant = bpmnModel.getModelElementsByType(Participant.class).iterator().next();
		BpmnDiagram diagram = definitions.getBpmDiagrams().iterator().next();
		BpmnShape processParticipantShape = getShapeForElement(processParticipant.getId(), diagram);
		for (Lane lane : bpmnModel.getModelElementsByType(Lane.class)) {
			Collection<FlowNode> nodes = getNodesInLaneGraphically(lane, definitions);
			lane.getFlowNodeRefs().addAll(nodes);
			BpmnShape laneShape = getShapeForElement(lane.getId(), diagram);
			Bounds laneBounds = laneShape.getBounds();
			laneBounds.setX(laneBounds.getX() + JpdlBpmnConverter.PARTICIPANT_LANE_OFFSET);
			laneBounds.setWidth(processParticipantShape.getBounds().getWidth() - JpdlBpmnConverter.PARTICIPANT_LANE_OFFSET);
			laneShape.setBounds(laneBounds);
		}
	}
	
	private Collection<FlowNode> getNodesInLaneGraphically(Lane lane, Definitions definitions) {
		List<FlowNode> flowNodes = new ArrayList<>();
		BpmnDiagram diagram = !definitions.getBpmDiagrams().isEmpty() ? definitions.getBpmDiagrams().iterator().next() : null;
		if (diagram != null) {
			BpmnPlane plane = diagram.getBpmnPlane();
			BpmnShape laneShape = getShapeForElement(lane.getId(), diagram);
			if (laneShape != null) {
				Bounds laneBounds = laneShape.getBounds();
				Rectangle laneRectangle = new Rectangle(laneBounds.getX().intValue(), laneBounds.getY().intValue(), 
						laneBounds.getWidth().intValue(), laneBounds.getHeight().intValue());
				for (DiagramElement diagramElement : plane.getDiagramElements()) {
					if (!(diagramElement instanceof BpmnShape) || diagramElement.equals(laneShape)) {
						continue;
					}
					BpmnShape shape = (BpmnShape) diagramElement;
					Bounds shapeBounds = shape.getBounds();
					Rectangle shapeRectangle = new Rectangle(shapeBounds.getX().intValue(), shapeBounds.getY().intValue(), 
							shapeBounds.getWidth().intValue(), shapeBounds.getHeight().intValue());
					if (laneRectangle.contains(shapeRectangle) && shape.getBpmnElement() instanceof FlowNode) {
						flowNodes.add((FlowNode) shape.getBpmnElement());
					}
				}
			}
		}
		return flowNodes;
	}
	
	private BpmnShape getShapeForElement(String elementId, BpmnDiagram diagram) {
		BpmnPlane plane = diagram.getBpmnPlane();
		for (DiagramElement diagramElement : plane.getDiagramElements()) {
			if (!(diagramElement instanceof BpmnShape)) {
				continue;
			}
			BpmnShape shape = (BpmnShape) diagramElement;
			if (shape.getBpmnElement().getId().equals(elementId)) {
				return shape;
			}
		}
		return null;
	}
}
