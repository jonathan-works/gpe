package br.com.infox.epp.modeler.converter;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Definitions;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Lane;
import org.camunda.bpm.model.bpmn.instance.LaneSet;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnDiagram;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnPlane;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.camunda.bpm.model.bpmn.instance.dc.Bounds;
import org.camunda.bpm.model.bpmn.instance.di.DiagramElement;

public class BizagiBpmnAdapter {
	public void checkAndConvert(BpmnModelInstance bpmnModel) {
		if (!bpmnModel.getDefinitions().getTargetNamespace().contains("bizagi")) {
			return;
		}
		
		Collection<Process> processes = bpmnModel.getModelElementsByType(Process.class);
		for (Process process : processes) {
			if (process.getChildElementsByType(LaneSet.class).isEmpty()) {
				process.getParentElement().removeChildElement(process);
			}
		}
		
		for (Lane lane : bpmnModel.getModelElementsByType(Lane.class)) {
			Collection<FlowNode> nodes = getNodesInLaneGraphically(lane, bpmnModel.getDefinitions());
			lane.getFlowNodeRefs().addAll(nodes);
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
