package br.com.infox.epp.modeler.converter;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.BaseElement;
import org.camunda.bpm.model.bpmn.instance.Collaboration;
import org.camunda.bpm.model.bpmn.instance.Definitions;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Lane;
import org.camunda.bpm.model.bpmn.instance.LaneSet;
import org.camunda.bpm.model.bpmn.instance.Participant;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnDiagram;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnLabel;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnPlane;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.camunda.bpm.model.bpmn.instance.dc.Bounds;
import org.camunda.bpm.model.bpmn.instance.di.DiagramElement;
import org.camunda.bpm.model.bpmn.instance.di.Waypoint;

public class BizagiBpmnAdapter {
	
	private static final int MODELER_OFFSET_X = 150; // Modelador X (200) - Bizagi X (50)
	private static final int MODELER_OFFSET_Y = 100; // Modelador Y (150) - Bizagi Y (50)
	
	public BpmnModelInstance checkAndConvert(BpmnModelInstance bpmnModel) {
		Definitions definitions = bpmnModel.getDefinitions();
		if (definitions.getTargetNamespace() == null || !definitions.getTargetNamespace().contains("bizagi")) {
			return bpmnModel;
		}
		
		return normalizeBpmnModel(bpmnModel);
	}
	
	private BpmnModelInstance normalizeBpmnModel(BpmnModelInstance bizagiBpmnModel) {
		removeUnnecessaryProcessesAndParticipants(bizagiBpmnModel);
		adjustLaneSizes(bizagiBpmnModel);
		
		Process bizagiProcess = bizagiBpmnModel.getModelElementsByType(Process.class).iterator().next();
		BpmnModelInstance normalizedModel = Bpmn.createProcess(bizagiProcess.getId()).name(bizagiProcess.getName()).done();
		Process normalizedProcess = normalizedModel.getModelElementById(bizagiProcess.getId());
		
		for (FlowNode bizagiNode : bizagiProcess.getChildElementsByType(FlowNode.class)) {
			FlowNode node = normalizedModel.newInstance(bizagiNode.getElementType());
			node.setId(bizagiNode.getId());
			node.setName(bizagiNode.getName());
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
		
		BpmnDiagram bizagiDiagram = bizagiBpmnModel.getDefinitions().getBpmDiagrams().iterator().next();
		BpmnDiagram diagram = normalizedModel.newInstance(BpmnDiagram.class);
		normalizedModel.getDefinitions().getBpmDiagrams().add(diagram);
		BpmnPlane plane = normalizedModel.newInstance(BpmnPlane.class);
		diagram.setBpmnPlane(plane);
		plane.setBpmnElement(collaboration);
		plane.setId(bizagiDiagram.getBpmnPlane().getId());
		
		for (BpmnShape bizagiShape : bizagiDiagram.getBpmnPlane().getChildElementsByType(BpmnShape.class)) {
			BaseElement bpmnElement = normalizedModel.getModelElementById(bizagiShape.getBpmnElement().getId());
			if (bpmnElement == null) {
				continue;
			}
			
			BpmnShape shape = normalizedModel.newInstance(BpmnShape.class);
			plane.getDiagramElements().add(shape);
			shape.setId(bizagiShape.getId());
			shape.setBpmnElement(bpmnElement);
			shape.setHorizontal(bizagiShape.isHorizontal());
			shape.setExpanded(bizagiShape.isExpanded());
			
			if (bizagiShape.getBounds() != null) {
				Bounds bounds = normalizedModel.newInstance(Bounds.class);
				bounds.setX(bizagiShape.getBounds().getX() + MODELER_OFFSET_X);
				bounds.setY(bizagiShape.getBounds().getY() + MODELER_OFFSET_Y);
				bounds.setWidth(bizagiShape.getBounds().getWidth());
				bounds.setHeight(bizagiShape.getBounds().getHeight());
				shape.setBounds(bounds);
			}
			
			if (bizagiShape.getBpmnLabel() != null) {
				BpmnLabel label = normalizedModel.newInstance(BpmnLabel.class);
				label.setId(bizagiShape.getBpmnLabel().getId());
				Bounds labelBounds = bizagiShape.getBpmnLabel().getBounds();
				if (labelBounds != null && labelBounds.getX() != 0 && labelBounds.getY() != 0) {
					Bounds bounds = normalizedModel.newInstance(Bounds.class);
					bounds.setX(labelBounds.getX() + MODELER_OFFSET_X);
					bounds.setY(labelBounds.getY() + MODELER_OFFSET_Y);
					bounds.setWidth(labelBounds.getWidth());
					bounds.setHeight(labelBounds.getHeight());
					label.setBounds(bounds);
				}
				shape.setBpmnLabel(label);
			}
		}
		
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
				waypoint.setX(bizagiWaypoint.getX() + MODELER_OFFSET_X);
				waypoint.setY(bizagiWaypoint.getY() + MODELER_OFFSET_Y);
				edge.getWaypoints().add(waypoint);
			}
			
			if (bizagiEdge.getBpmnLabel() != null) {
				BpmnLabel label = normalizedModel.newInstance(BpmnLabel.class);
				label.setId(bizagiEdge.getBpmnLabel().getId());
				Bounds labelBounds = bizagiEdge.getBpmnLabel().getBounds();
				if (labelBounds != null && labelBounds.getX() != 0 && labelBounds.getY() != 0) {
					Bounds bounds = normalizedModel.newInstance(Bounds.class);
					bounds.setX(labelBounds.getX() + MODELER_OFFSET_X);
					bounds.setY(labelBounds.getY() + MODELER_OFFSET_Y);
					bounds.setWidth(labelBounds.getWidth());
					bounds.setHeight(labelBounds.getHeight());
					label.setBounds(bounds);
				}
				edge.setBpmnLabel(label);
			}
			plane.addChildElement(edge);
		}
		
		return normalizedModel;
	}
	
	private void removeUnnecessaryProcessesAndParticipants(BpmnModelInstance bizagiBpmnModel) {
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
	}
	
	private void adjustLaneSizes(BpmnModelInstance bizagiBpmnModel) {
		Definitions definitions = bizagiBpmnModel.getDefinitions();
		Participant processParticipant = bizagiBpmnModel.getModelElementsByType(Participant.class).iterator().next();
		BpmnDiagram diagram = definitions.getBpmDiagrams().iterator().next();
		BpmnShape processParticipantShape = getShapeForElement(processParticipant.getId(), diagram);
		for (Lane lane : bizagiBpmnModel.getModelElementsByType(Lane.class)) {
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
