package br.com.infox.epp.modeler;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Activity;
import org.camunda.bpm.model.bpmn.instance.BaseElement;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Lane;
import org.camunda.bpm.model.bpmn.instance.Participant;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.camunda.bpm.model.bpmn.instance.dc.Bounds;
import org.camunda.bpm.model.bpmn.instance.di.Waypoint;

class BpmnDiagramAdapter implements BpmnAdapter {

	private BpmnModelInstance bpmnModel;
	
	@Override
	public BpmnModelInstance checkAndConvert(BpmnModelInstance bpmnModel) {
		this.bpmnModel = bpmnModel;
		adjustLaneSizes();
		normalizeNodeSizes();
		normalizeSequenceFlowSizes();
		translateDiagram();
		return bpmnModel;
	}
	
	private void normalizeNodeSizes() {
		for (FlowNode flowNode : bpmnModel.getModelElementsByType(FlowNode.class)) {
			BpmnShape shape = (BpmnShape) flowNode.getDiagramElement();
			if (!isDefinedBounds(shape.getBounds())) {
				shape.removeChildElement(shape.getBounds());
			} else {
				shape.getBounds().setWidth(getDefaultWidth(flowNode));
				shape.getBounds().setHeight(getDefaultHeight(flowNode));
			}
			if (shape.getBpmnLabel() != null && !isDefinedBounds(shape.getBpmnLabel().getBounds())) {
				shape.removeChildElement(shape.getBpmnLabel());
			}
		}
	}
	
	private void normalizeSequenceFlowSizes() {
		for (SequenceFlow sequenceFlow : bpmnModel.getModelElementsByType(SequenceFlow.class)) {
			BpmnEdge edge = (BpmnEdge) sequenceFlow.getDiagramElement();
			if (edge.getBpmnLabel() != null && !isDefinedBounds(edge.getBpmnLabel().getBounds())) {
				edge.removeChildElement(edge.getBpmnLabel());
			}
		}
	}

	private void adjustLaneSizes() {
		Participant processParticipant = bpmnModel.getModelElementsByType(Participant.class).iterator().next();
		BpmnShape processParticipantShape = (BpmnShape) processParticipant.getDiagramElement();
		for (Lane lane : bpmnModel.getModelElementsByType(Lane.class)) {
			BpmnShape laneShape = (BpmnShape) lane.getDiagramElement();
			Bounds laneBounds = laneShape.getBounds();
			laneBounds.setX(processParticipantShape.getBounds().getX() + DiagramUtil.PARTICIPANT_LANE_OFFSET);
			laneBounds.setWidth(processParticipantShape.getBounds().getWidth() - DiagramUtil.PARTICIPANT_LANE_OFFSET);
			laneShape.setBounds(laneBounds);
		}
	}
	
	private void translateDiagram() {
		Participant participant = bpmnModel.getModelElementsByType(Participant.class).iterator().next();
		BpmnShape participantShape = (BpmnShape) participant.getDiagramElement();
		double offsetX = DiagramUtil.PARTICIPANT_X - participantShape.getBounds().getX();
		double offsetY = DiagramUtil.PARTICIPANT_Y - participantShape.getBounds().getY();
		for (Bounds bounds : bpmnModel.getModelElementsByType(Bounds.class)) {
			bounds.setX(bounds.getX() + offsetX);
			bounds.setY(bounds.getY() + offsetY);
		}
		for (Waypoint waypoint : bpmnModel.getModelElementsByType(Waypoint.class)) {
			waypoint.setX(waypoint.getX() + offsetX);
			waypoint.setY(waypoint.getY() + offsetY);
		}
	}
	
	private boolean isDefinedBounds(Bounds bounds) {
		return bounds != null && bounds.getX() != 0 && bounds.getY() != 0 && bounds.getWidth() != 0 && bounds.getHeight() != 0;
	}
	
	private double getDefaultWidth(BaseElement element) {
		if (element instanceof Activity) {
			return DiagramUtil.ACTIVITY_WIDTH;
		} else if (element instanceof FlowNode) {
			return DiagramUtil.GENERAL_FLOWNODE_WIDTH;
		} else {
			return ((BpmnShape) element.getDiagramElement()).getBounds().getWidth();
		}
	}
	
	private double getDefaultHeight(BaseElement element) {
		if (element instanceof Activity) {
			return DiagramUtil.ACTIVITY_HEIGHT;
		} else if (element instanceof FlowNode) {
			return DiagramUtil.GENERAL_FLOWNODE_HEIGHT;
		} else {
			return ((BpmnShape) element.getDiagramElement()).getBounds().getHeight();
		}
	}
}
