package br.com.infox.epp.fluxo.definicao.modeler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.impl.BpmnModelConstants;
import org.camunda.bpm.model.bpmn.instance.Activity;
import org.camunda.bpm.model.bpmn.instance.BoundaryEvent;
import org.camunda.bpm.model.bpmn.instance.DataAssociation;
import org.camunda.bpm.model.bpmn.instance.DataInputAssociation;
import org.camunda.bpm.model.bpmn.instance.DataObject;
import org.camunda.bpm.model.bpmn.instance.DataObjectReference;
import org.camunda.bpm.model.bpmn.instance.DataOutputAssociation;
import org.camunda.bpm.model.bpmn.instance.EventDefinition;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.Property;
import org.camunda.bpm.model.bpmn.instance.SignalEventDefinition;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.TimerEventDefinition;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnDiagram;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.camunda.bpm.model.bpmn.instance.dc.Bounds;
import org.camunda.bpm.model.bpmn.instance.di.Waypoint;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Node.NodeType;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.node.StartState;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.scheduler.def.CreateTimerAction;

import com.google.gson.Gson;

import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.documento.manager.ClassificacaoDocumentoManager;
import br.com.infox.ibpm.node.handler.NodeHandler;
import br.com.infox.ibpm.process.definition.variable.VariableType;
import br.com.infox.ibpm.task.handler.GenerateDocumentoHandler;
import br.com.infox.ibpm.task.handler.GenerateDocumentoHandler.GenerateDocumentoConfiguration;

public class ConfiguracoesNos {
	
	private List<ConfiguracaoVariavelDocumento> variaveisDocumento;
	private List<ConfiguracaoDocumentoGerado> documentosGerados;
	private BpmnModelInstance bpmnModel;
	
	private static enum Position {
		TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT;
	}
	
	public void resolverMarcadoresBpmn(ProcessDefinition processDefinition, BpmnModelInstance bpmnModel) {
		variaveisDocumento = new ArrayList<>();
		documentosGerados = new ArrayList<>();
		this.bpmnModel = bpmnModel;
		
		for (Node node : processDefinition.getNodes()) {
			if (node.getNodeType().equals(NodeType.Task)) {
				resolverTimer((TaskNode) node);
				resolverVariaveisDocumento((TaskNode) node);
			}
			
			if (node.getNodeType().equals(NodeType.Task) || node.getNodeType().equals(NodeType.ProcessState)) {
				resolverSinalBoundary(node);
			} else if (node.getNodeType().equals(NodeType.StartState)) {
				resolverSinalStart((StartState) node);
			}
			
			if (node.getNodeType().equals(NodeType.Task) || node.getNodeType().equals(NodeType.Node)) {
				resolverDocumentosGerados(node);
			}
		}
		
		resolverDocumentos();
	}
	
	private void resolverDocumentosGerados(Node node) {
		Event exitEvent = node.getEvent(Event.EVENTTYPE_NODE_LEAVE);
		if (exitEvent != null && exitEvent.getAction(NodeHandler.GENERATE_DOCUMENTO_ACTION_NAME) != null) {
			Action generateDocumentoAction = exitEvent.getAction(NodeHandler.GENERATE_DOCUMENTO_ACTION_NAME);
			String jsonConfiguration = new GenerateDocumentoHandler().parseJbpmConfiguration(generateDocumentoAction.getActionDelegation().getConfiguration());
			GenerateDocumentoConfiguration config = new Gson().fromJson(jsonConfiguration, GenerateDocumentoConfiguration.class);
			ClassificacaoDocumentoManager classificacaoDocumentoManager = BeanManager.INSTANCE.getReference(ClassificacaoDocumentoManager.class);
			String codigoClassificacao = config.getCodigoClassificacaoDocumento();
			String classificacao = classificacaoDocumentoManager.getNomeClassificacaoByCodigo(codigoClassificacao);
			documentosGerados.add(new ConfiguracaoDocumentoGerado(node.getKey(), classificacao, codigoClassificacao));
		}
	}

	private void resolverDocumentos() {
		removerDataObjectsNaoExistentes();
		Process process = bpmnModel.getModelElementsByType(Process.class).iterator().next();
		
		for (ConfiguracaoVariavelDocumento config : variaveisDocumento) {
			DataObjectReference dataObjectReference = bpmnModel.getModelElementById(config.dataObjectReferenceId);
			UserTask userTask = bpmnModel.getModelElementById(config.nodeId);
			
			if (dataObjectReference == null) {
				dataObjectReference = criarDataObjectReference(config, process, userTask.getDiagramElement().getBounds(), config.entrada);
			}
			dataObjectReference.setName(config.label);
			
			if (config.entrada) {
				if (!hasDataInputAssociation(userTask, dataObjectReference)) {
					criarDataInputAssociation(userTask, dataObjectReference);
				}
			} else {
				if (!hasDataOutputAssociation(userTask, dataObjectReference)) {
					criarDataOutputAssociation(userTask, dataObjectReference);
				}
			}
		}
		
		for (ConfiguracaoDocumentoGerado config : documentosGerados) {
			Activity activity = bpmnModel.getModelElementById(config.nodeId);
			DataObjectReference dataObjectReference = bpmnModel.getModelElementById(config.dataObjectReferenceId);
			if (dataObjectReference == null) {
				dataObjectReference = criarDataObjectReference(config, process, ((BpmnShape) activity.getDiagramElement()).getBounds(), false);
			}
			dataObjectReference.setName(config.label);
			
			if (!hasDataOutputAssociation(activity, dataObjectReference)) {
				criarDataOutputAssociation(activity, dataObjectReference);
			}
		}
	}

	private void criarDataOutputAssociation(Activity activity, DataObjectReference dataObjectReference) {
		DataOutputAssociation dataOutputAssociation = bpmnModel.newInstance(DataOutputAssociation.class);
		dataOutputAssociation.setTarget(dataObjectReference);
		activity.getDataOutputAssociations().add(dataOutputAssociation);
		
		criarEdgeAssociation(dataOutputAssociation, ((BpmnShape) activity.getDiagramElement()).getBounds(), ((BpmnShape) dataObjectReference.getDiagramElement()).getBounds());
	}

	private void criarDataInputAssociation(UserTask userTask, DataObjectReference dataObjectReference) {
		Property targetPlaceholder = bpmnModel.newInstance(Property.class);
		userTask.getProperties().add(targetPlaceholder);
		DataInputAssociation dataInputAssociation = bpmnModel.newInstance(DataInputAssociation.class);
		dataInputAssociation.getSources().add(dataObjectReference);
		dataInputAssociation.setTarget(targetPlaceholder);
		userTask.getDataInputAssociations().add(dataInputAssociation);
		
		criarEdgeAssociation(dataInputAssociation, userTask.getDiagramElement().getBounds(), ((BpmnShape) dataObjectReference.getDiagramElement()).getBounds());
	}
	
	private DataObjectReference criarDataObjectReference(ConfiguracaoDocumento configuracaoDocumento, Process process, Bounds taskBounds, boolean left) {
		DataObject dataObject = bpmnModel.newInstance(DataObject.class);
		dataObject.setId(configuracaoDocumento.dataObjectId);
		process.addChildElement(dataObject);
		DataObjectReference dataObjectReference = bpmnModel.newInstance(DataObjectReference.class);
		dataObjectReference.setId(configuracaoDocumento.dataObjectReferenceId);
		process.addChildElement(dataObjectReference);
		dataObjectReference.setDataObject(dataObject);
		
		BpmnDiagram diagram = DiagramUtil.getDefaultDiagram(bpmnModel);
		BpmnShape shape = bpmnModel.newInstance(BpmnShape.class);
		shape.setBpmnElement(dataObjectReference);
		diagram.getBpmnPlane().addChildElement(shape);
		
		Bounds bounds = bpmnModel.newInstance(Bounds.class);
		shape.setBounds(bounds);
		bounds.setWidth(36);
		bounds.setHeight(50);
		
		bounds.setY(taskBounds.getY() + taskBounds.getHeight() + bounds.getHeight());
		if (left) {
			bounds.setX(taskBounds.getX() - bounds.getWidth() / 2 + taskBounds.getWidth() / 2 - 20);
		} else {
			bounds.setX(taskBounds.getX() - bounds.getWidth() / 2 + taskBounds.getWidth() / 2 + 20);
		}
		
		return dataObjectReference;
	}
	
	private void criarEdgeAssociation(DataAssociation association, Bounds taskBounds, Bounds dataObjectReferenceBounds) {
		BpmnDiagram diagram = DiagramUtil.getDefaultDiagram(bpmnModel);
		
		BpmnEdge edge = bpmnModel.newInstance(BpmnEdge.class);
		edge.setBpmnElement(association);
		diagram.getBpmnPlane().addChildElement(edge);
		edge.getDomElement().setAttribute(ModeladorConstants.BPMN_IO_COLOR_NAMESPACE, "stroke", "#969696");
		edge.getDomElement().setAttribute(ModeladorConstants.BPMN_IO_COLOR_NAMESPACE, "fill", "#969696");
		
		Waypoint waypoint1 = bpmnModel.newInstance(Waypoint.class);
		waypoint1.setX(taskBounds.getX() + taskBounds.getWidth() / 2);
		waypoint1.setY(taskBounds.getY() + taskBounds.getHeight());
		
		Waypoint waypoint2 = bpmnModel.newInstance(Waypoint.class);
		waypoint2.setX(dataObjectReferenceBounds.getX() + dataObjectReferenceBounds.getWidth() / 2);
		waypoint2.setY(dataObjectReferenceBounds.getY());
		
		if (association.getElementType().getTypeName().equals(BpmnModelConstants.BPMN_ELEMENT_DATA_OUTPUT_ASSOCIATION)) {
			edge.getWaypoints().add(waypoint1);
			edge.getWaypoints().add(waypoint2);
		} else {
			edge.getWaypoints().add(waypoint2);
			edge.getWaypoints().add(waypoint1);
		}
	}
	
	private boolean hasDataOutputAssociation(Activity activity, DataObjectReference dataObjectReference) {
		for (DataOutputAssociation dataOutputAssociation : activity.getDataOutputAssociations()) {
			if (dataObjectReference.equals(dataOutputAssociation.getTarget())) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean hasDataInputAssociation(UserTask userTask, DataObjectReference dataObjectReference) {
		for (DataInputAssociation dataInputAssociation : userTask.getDataInputAssociations()) {
			if (dataObjectReference.equals(dataInputAssociation.getSources().iterator().next())) {
				return true;
			}
		}
		
		return false;
	}
	
	private void removerDataObjectsNaoExistentes() {
		Set<DataObjectReference> dataObjectReferencesToRemove = new HashSet<>();

		for (Activity activity : bpmnModel.getModelElementsByType(Activity.class)) {
			if (activity.getElementType().getTypeName().equals(BpmnModelConstants.BPMN_ELEMENT_USER_TASK)) {
				for (DataInputAssociation dataInputAssociation : activity.getDataInputAssociations()) {
					DataObjectReference dataObjectReference = (DataObjectReference) dataInputAssociation.getSources().iterator().next();
					if (!ConfiguracaoDocumento.contains(variaveisDocumento, dataObjectReference.getId())) {
						activity.getDataInputAssociations().remove(dataInputAssociation);
						dataObjectReferencesToRemove.add(dataObjectReference);
					}
				}
			}
			
			for (DataOutputAssociation dataOutputAssociation : activity.getDataOutputAssociations()) {
				DataObjectReference dataObjectReference = (DataObjectReference) dataOutputAssociation.getTarget();
				if (ConfiguracaoDocumentoGerado.isGeneratedDocument(dataObjectReference.getId())) {
				    if (!ConfiguracaoDocumento.contains(documentosGerados, dataObjectReference.getId())) {
	                    activity.getDataOutputAssociations().remove(dataOutputAssociation);
	                    dataObjectReferencesToRemove.add(dataObjectReference);
				    }
				} else  {
				    if (!ConfiguracaoDocumento.contains(variaveisDocumento, dataObjectReference.getId())) {
	                    activity.getDataOutputAssociations().remove(dataOutputAssociation);
	                    dataObjectReferencesToRemove.add(dataObjectReference);
				    }
				}
			}
		}
		
		for (DataObjectReference dataObjectReference : dataObjectReferencesToRemove) {
			DataObject dataObject = dataObjectReference.getDataObject();
			dataObjectReference.getParentElement().removeChildElement(dataObjectReference);
			dataObject.getParentElement().removeChildElement(dataObject);
		}
	}

	private void resolverVariaveisDocumento(TaskNode node) {
		if (node.getTasks() != null) {
			for (org.jbpm.taskmgmt.def.Task task : node.getTasks()) {
				List<VariableAccess> variables = task.getTaskController() == null ? null : task.getTaskController().getVariableAccesses();
				if (variables != null) {
					for (VariableAccess var : variables) {
						VariableType variableType = VariableType.valueOf(var.getType());
						if (variableType == VariableType.EDITOR || variableType == VariableType.FILE) {
						    variaveisDocumento.add(new ConfiguracaoVariavelDocumento(node.getKey(), var.getLabel(), var.getVariableName(), !var.isWritable()));
						}
					}
				}
			}
		}
	}

	private void resolverTimer(TaskNode node) {
		UserTask userTask = bpmnModel.getModelElementById(node.getKey());
		BoundaryEvent boundaryEvent = getBoundaryEvent(userTask, TimerEventDefinition.class);
		boolean hasTimers = hasTimers(node);
		if (hasTimers && boundaryEvent == null) {
			BoundaryEvent event = createBoundaryEvent(userTask, Position.BOTTOM_RIGHT);
			event.getEventDefinitions().add(bpmnModel.newInstance(TimerEventDefinition.class));
		} else if (!hasTimers && boundaryEvent != null) {
			boundaryEvent.getParentElement().removeChildElement(boundaryEvent);
		}
	}

	private BoundaryEvent getBoundaryEvent(Activity activity, Class<? extends EventDefinition> eventDefinitionClass) {
		for (BoundaryEvent boundaryEvent : activity.getParentElement().getChildElementsByType(BoundaryEvent.class)) {
			if (boundaryEvent.getAttachedTo().equals(activity)) {
				if (eventDefinitionClass == null && boundaryEvent.getEventDefinitions().isEmpty()) {
					return boundaryEvent;
				} else if (eventDefinitionClass != null) {
					for (EventDefinition eventDefinition : boundaryEvent.getEventDefinitions()) {
						if (eventDefinitionClass.isInstance(eventDefinition)) {
							return boundaryEvent;
						}
					}
				}
			}
		}
		
		return null;
	}

	private void resolverSinalBoundary(Node node) {
		Activity activity = bpmnModel.getModelElementById(node.getKey());
		BoundaryEvent boundaryEvent = getBoundaryEvent(activity, SignalEventDefinition.class);
		Map<String, Event> events = node.getEvents();
		boolean removerBoundaryEvent = boundaryEvent != null;
		
		if (events != null) {
			for (Event event : events.values()) {
				if (event.isListener()) {
					removerBoundaryEvent = false;
					if (boundaryEvent == null) {
						boundaryEvent = createBoundaryEvent(activity, Position.TOP_RIGHT);
						boundaryEvent.getEventDefinitions().add(bpmnModel.newInstance(SignalEventDefinition.class));
					}
					break;
				}
			}
		}
		
		if (removerBoundaryEvent) {
			boundaryEvent.getParentElement().removeChildElement(boundaryEvent);
		}
	}
	
	private void resolverSinalStart(StartState startState) {
		StartEvent startEvent = bpmnModel.getModelElementById(startState.getKey());
		SignalEventDefinition signalEventDefinition = null;
		for (EventDefinition eventDefinition : startEvent.getEventDefinitions()) {
			if (eventDefinition.getElementType().getTypeName().equals(BpmnModelConstants.BPMN_ELEMENT_SIGNAL_EVENT_DEFINITION)) {
				signalEventDefinition = (SignalEventDefinition) eventDefinition;
				break;
			}
		}
		
		Map<String, Event> events = startState.getEvents();
		boolean removerSignalDefinition = signalEventDefinition != null;
		
		if (events != null) {
			for (Event event : events.values()) {
				if (event.isListener()) {
					removerSignalDefinition = false;
					if (signalEventDefinition == null) {
						startEvent.getEventDefinitions().add(bpmnModel.newInstance(SignalEventDefinition.class));
					}
					break;
				}
			}
		}
		
		if (removerSignalDefinition) {
			startEvent.getEventDefinitions().remove(signalEventDefinition);
		}
	}
	
	private boolean hasTimers(TaskNode node) {
		if (node.hasEvent(Event.EVENTTYPE_NODE_ENTER)) {
			Event event = node.getEvent(Event.EVENTTYPE_NODE_ENTER);
			if (event.getActions() != null) {
				for (Action action : event.getActions()) {
					if (action instanceof CreateTimerAction) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private BoundaryEvent createBoundaryEvent(Activity activity, Position position) {
		BpmnModelInstance bpmnModel = (BpmnModelInstance) activity.getModelInstance();
		
		BoundaryEvent boundaryEvent = bpmnModel.newInstance(BoundaryEvent.class);
		boundaryEvent.setAttachedTo(activity);
		activity.getParentElement().addChildElement(boundaryEvent);
		
		BpmnDiagram diagram = DiagramUtil.getDefaultDiagram(bpmnModel);
		BpmnShape shape = bpmnModel.newInstance(BpmnShape.class);
		diagram.getBpmnPlane().addChildElement(shape);
		shape.setBpmnElement(boundaryEvent);
		Bounds bounds = bpmnModel.newInstance(Bounds.class);
		shape.setBounds(bounds);
		
		Bounds activityBounds = ((BpmnShape) activity.getDiagramElement()).getBounds();
		bounds.setWidth(36);
		bounds.setHeight(36);
		
		switch (position) {
		case TOP_RIGHT:
			bounds.setX(activityBounds.getX() + activityBounds.getWidth() - (bounds.getWidth() / 2));
			bounds.setY(activityBounds.getY() - (bounds.getHeight() / 2));
			break;
		case TOP_LEFT:
			bounds.setX(activityBounds.getX() - (bounds.getWidth() / 2));
			bounds.setY(activityBounds.getY() - (bounds.getHeight() / 2));
			break;
		case BOTTOM_RIGHT:
			bounds.setX(activityBounds.getX() + activityBounds.getWidth() - (bounds.getWidth() / 2));
			bounds.setY(activityBounds.getY() + activityBounds.getHeight() - (bounds.getHeight() / 2));
			break;
		case BOTTOM_LEFT:
			bounds.setX(activityBounds.getX() - (bounds.getWidth() / 2));
			bounds.setY(activityBounds.getY() + activityBounds.getHeight() - (bounds.getHeight() / 2));
			break;
		default:
			break;
		}
		
		return boundaryEvent;
	}
	
	private static abstract class ConfiguracaoDocumento {
	    protected String nodeId;
	    protected String label;
	    protected String dataObjectId;
	    protected String dataObjectReferenceId;
        
        public ConfiguracaoDocumento(String nodeId, String label, String baseId) {
            this.nodeId = nodeId;
            this.label = label;
            this.dataObjectId = generateDataObjectId(baseId);
            this.dataObjectReferenceId = generateDataObjectReferenceId(baseId);
        }
        
        private String generateDataObjectReferenceId(String baseId) {
            return getPrefix() + "_" + DigestUtils.sha1Hex(baseId);
        }
        
        private String generateDataObjectId(String baseId) {
            return getPrefix() + "_DataObject_" + DigestUtils.sha1Hex(baseId);
        }
        
        private static <T extends ConfiguracaoDocumento> boolean contains(List<T> list, String dataObjectReferenceId) {
            for (ConfiguracaoDocumento config : list) {
                if (config.dataObjectReferenceId.equals(dataObjectReferenceId)) {
                    return true;
                }
            }
            return false;
        }
        
        protected abstract String getPrefix();
	}
	
	private static final class ConfiguracaoVariavelDocumento extends ConfiguracaoDocumento {
		private boolean entrada;

		public ConfiguracaoVariavelDocumento(String nodeId, String label, String variavel, boolean entrada) {
		    super(nodeId, label, variavel);
		    this.entrada = entrada;
		}
		
        @Override
        protected String getPrefix() {
            return "Variable";
        }
	}
	
	private static final class ConfiguracaoDocumentoGerado extends ConfiguracaoDocumento {
	    private static final String PREFIX = "GeneratedDocument";
	    
		public ConfiguracaoDocumentoGerado(String nodeId, String label, String codigoClassificacao) {
		    super(nodeId, label, nodeId + codigoClassificacao);
		}
		
		private static boolean isGeneratedDocument(String id) {
		    return id.startsWith(PREFIX);
		}

        @Override
        protected String getPrefix() {
            return PREFIX;
        }
	}
}
