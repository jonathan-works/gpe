package br.com.infox.epp.modeler.converter;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Lane;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnDiagram;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnPlane;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.camunda.bpm.model.bpmn.instance.dc.Bounds;
import org.camunda.bpm.model.bpmn.instance.di.DiagramElement;

class DiagramUtil {
	
	static final int ACTIVITY_WIDTH = 100;
	static final int ACTIVITY_HEIGHT = 80;
	static final int GENERAL_FLOWNODE_HEIGHT = 50;
	static final int GENERAL_FLOWNODE_WIDTH = GENERAL_FLOWNODE_HEIGHT;
	static final int PARTICIPANT_LANE_OFFSET = 30;
	static final int FLOW_NODE_X_OFFSET = 30;
	static final int FLOW_NODE_Y_OFFSET = 10;
	static final int PARTICIPANT_X = 200;
	static final int PARTICIPANT_Y = 150;
	
	static Collection<FlowNode> getNodesInLaneGraphically(Lane lane) {
		List<FlowNode> flowNodes = new ArrayList<>();
		BpmnDiagram diagram = getDefaultDiagram((BpmnModelInstance) lane.getModelInstance());
		if (diagram != null) {
			BpmnPlane plane = diagram.getBpmnPlane();
			BpmnShape laneShape = (BpmnShape) lane.getDiagramElement();
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
	
	static BpmnDiagram getDefaultDiagram(BpmnModelInstance model) {
		return model.getDefinitions().getBpmDiagrams().iterator().next();
	}
}
