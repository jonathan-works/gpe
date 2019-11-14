package br.com.infox.epp.fluxo.definicao.modeler.configuracoes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.impl.BpmnModelConstants;
import org.camunda.bpm.model.bpmn.instance.Activity;
import org.camunda.bpm.model.bpmn.instance.DataAssociation;
import org.camunda.bpm.model.bpmn.instance.DataInputAssociation;
import org.camunda.bpm.model.bpmn.instance.DataObject;
import org.camunda.bpm.model.bpmn.instance.DataObjectReference;
import org.camunda.bpm.model.bpmn.instance.DataOutputAssociation;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.Property;
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
import org.jbpm.graph.node.TaskNode;

import com.google.gson.Gson;

import br.com.infox.epp.cdi.util.Beans;
import br.com.infox.epp.documento.manager.ClassificacaoDocumentoManager;
import br.com.infox.epp.fluxo.definicao.modeler.DiagramUtil;
import br.com.infox.epp.fluxo.definicao.modeler.ModeladorConstants;
import br.com.infox.ibpm.node.handler.NodeHandler;
import br.com.infox.ibpm.process.definition.variable.VariableType;
import br.com.infox.ibpm.task.handler.GenerateDocumentoHandler;
import br.com.infox.ibpm.task.handler.GenerateDocumentoHandler.GenerateDocumentoConfiguration;

class DocumentoResolver {
    private BpmnModelInstance bpmnModel;
    private ProcessDefinition processDefinition;
    private List<ConfiguracaoVariavelDocumento> variaveisDocumento;
    private List<ConfiguracaoDocumentoGerado> documentosGerados;
    
    DocumentoResolver(ProcessDefinition processDefinition, BpmnModelInstance bpmnModel) {
        this.processDefinition = processDefinition;
        this.bpmnModel = bpmnModel;
    }
    
    void resolverMarcadoresDocumentos() {
        variaveisDocumento = new ArrayList<>();
        documentosGerados = new ArrayList<>();
        
        for (Node node : processDefinition.getNodes()) {
            if (node.getNodeType().equals(NodeType.Task)) {
                resolverVariaveisDocumento((TaskNode) node);
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
            ClassificacaoDocumentoManager classificacaoDocumentoManager = Beans.getReference(ClassificacaoDocumentoManager.class);
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
}
