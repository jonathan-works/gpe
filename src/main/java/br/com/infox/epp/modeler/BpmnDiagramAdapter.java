package br.com.infox.epp.modeler;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	private Map<String, Set<SequenceFlow>> sequenceFlowsByNodeIds;
	
	@Override
	public BpmnModelInstance checkAndConvert(BpmnModelInstance bpmnModel) {
		this.bpmnModel = bpmnModel;
		this.sequenceFlowsByNodeIds = new HashMap<>();
		for (SequenceFlow sequenceFlow : bpmnModel.getModelElementsByType(SequenceFlow.class)) {
		    if (!sequenceFlowsByNodeIds.containsKey(sequenceFlow.getSource().getId())) {
		        sequenceFlowsByNodeIds.put(sequenceFlow.getSource().getId(), new HashSet<SequenceFlow>());
		    }
		    if (!sequenceFlowsByNodeIds.containsKey(sequenceFlow.getTarget().getId())) {
                sequenceFlowsByNodeIds.put(sequenceFlow.getTarget().getId(), new HashSet<SequenceFlow>());
            }
		    
		    sequenceFlowsByNodeIds.get(sequenceFlow.getSource().getId()).add(sequenceFlow);
		    sequenceFlowsByNodeIds.get(sequenceFlow.getTarget().getId()).add(sequenceFlow);
		}
		
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
			} else if (sequenceFlowsByNodeIds.containsKey(flowNode.getId())) {
			    adjustEdgePoints(shape);
			}
			if (shape.getBpmnLabel() != null && !isDefinedBounds(shape.getBpmnLabel().getBounds())) {
				shape.removeChildElement(shape.getBpmnLabel());
			}
		}
	}
	
	private void adjustEdgePoints(BpmnShape flowNodeShape) {
	    FlowNode flowNode = (FlowNode) flowNodeShape.getBpmnElement();
	    
	    double originalWidth = flowNodeShape.getBounds().getWidth();
        double originalHeight = flowNodeShape.getBounds().getHeight();
        flowNodeShape.getBounds().setWidth(getDefaultWidth(flowNode));
        flowNodeShape.getBounds().setHeight(getDefaultHeight(flowNode));
        
        double x1 = flowNodeShape.getBounds().getX();
        double y1 = flowNodeShape.getBounds().getY();
        double x2 = x1 + flowNodeShape.getBounds().getWidth();
        double y2 = y1 + flowNodeShape.getBounds().getHeight();
        double x2Original = x1 + originalWidth;
        double y2Original = y1 + originalHeight;
        
        Line2D topLine = new Line2D.Double(x1, y1, x2Original, y1);
        Line2D bottomLine = new Line2D.Double(x1, y2Original, x2Original, y2Original);
        Line2D leftLine = new Line2D.Double(x1, y1, x1, y2Original);
        Line2D rightLine = new Line2D.Double(x2Original, y1, x2Original, y2Original);
        
        for (SequenceFlow sequenceFlow : sequenceFlowsByNodeIds.get(flowNode.getId())) {
            BpmnEdge edge = sequenceFlow.getDiagramElement();
            Waypoint waypoint1, waypoint2;
            List<Waypoint> waypoints = new ArrayList<>(edge.getWaypoints());
            if (flowNode.equals(sequenceFlow.getSource())) {
                waypoint1 = waypoints.get(0);
                waypoint2 = waypoints.get(1);
            } else {
                waypoint1 = waypoints.get(waypoints.size() - 1);
                waypoint2 = waypoints.get(waypoints.size() - 2);
            }
            
            Line2D waypointLine = new Line2D.Double(waypoint1.getX(), waypoint1.getY(), waypoint2.getX(), waypoint2.getY());
            
            if (waypointLine.intersectsLine(topLine)) {
                waypoint1.setY(y1);
            } else if (waypointLine.intersectsLine(bottomLine)) {
                waypoint1.setY(y2);
            } else if (waypointLine.intersectsLine(rightLine)) {
                waypoint1.setX(x2);
            } else if (waypointLine.intersectsLine(leftLine)) {
                waypoint1.setX(x1);
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
