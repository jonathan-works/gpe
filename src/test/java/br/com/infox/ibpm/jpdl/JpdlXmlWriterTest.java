package br.com.infox.ibpm.jpdl;

import static br.com.infox.epp.processo.status.entity.StatusProcesso.STATUS_PROCESSO_ACTION_NAME;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.def.node.loop.LoopConfiguration;
import org.jbpm.graph.def.node.loop.LoopConfigurationMultiInstance;
import org.jbpm.graph.def.node.loop.LoopConfigurationMultiInstanceFlowCondition;
import org.jbpm.graph.def.node.loop.LoopConfigurationStandard;
import org.jbpm.graph.node.EndState;
import org.jbpm.graph.node.ProcessState;
import org.jbpm.graph.node.StartState;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.instantiation.Delegation;
import org.jbpm.jpdl.JpdlException;
import org.jbpm.jpdl.xml.JpdlXmlReader;
import org.jbpm.jpdl.xml.Problem;
import org.junit.Assert;
import org.junit.Test;

import com.google.gson.Gson;

import br.com.infox.ibpm.task.handler.GenerateDocumentoHandler;
import br.com.infox.ibpm.task.handler.GenerateDocumentoHandler.GenerateDocumentoConfiguration;
import br.com.infox.ibpm.task.handler.StatusHandler;

public class JpdlXmlWriterTest {

	private void addTransition(Node from, Node to) {
		from.addLeavingTransition(to.addArrivingTransition(new Transition()));
	}

	@SuppressWarnings("unchecked")
	private <T extends Node> T addNode(ProcessDefinition processDef, T node) {
		return (T) processDef.addNode(node);
	}

	private void addAction(Node node, String eventType, Action action) {
		Event event = ObjectUtils.defaultIfNull(node.getEvent(eventType), node.addEvent(new Event(eventType)));
		event.addAction(action);
	}

	private ProcessDefinition criarComGenerateDocumento() {
		ProcessDefinition processDefinition = ProcessDefinition.createNewProcessDefinition();
		processDefinition.setName("teste");
		StartState startNode = addNode(processDefinition, new StartState("Início"));
		TaskNode taskNode = addNode(processDefinition, new TaskNode("taskNode"));
		EndState endState = addNode(processDefinition, new EndState("Término"));
		addTransition(startNode, taskNode);
		addTransition(taskNode, endState);

		Action action = new Action();
		action.setName("generateDocumentoAction");
		Delegation delegation = new Delegation(GenerateDocumentoHandler.class.getName());
		delegation.setConfigType("constructor");
		GenerateDocumentoConfiguration configuration = new GenerateDocumentoConfiguration();
		configuration.setIdClassificacaoDocumento(1);
		configuration.setIdModeloDocumento(1);
		delegation.setConfiguration(new Gson().toJson(configuration));
		action.setActionDelegation(delegation);
		addAction(taskNode, Event.EVENTTYPE_NODE_ENTER, action);
		return processDefinition;
	}

	private ProcessDefinition criarComLoop(LoopConfiguration loopConfiguration) {
		ProcessDefinition processDefinition = ProcessDefinition.createNewProcessDefinition();
		processDefinition.setName("teste");
		StartState startNode = addNode(processDefinition, new StartState("Início"));
		TaskNode loopNode1 = addNode(processDefinition, new TaskNode("loopNode"));
		ProcessState processState1 = addNode(processDefinition, new ProcessState("processState"));
		processState1.setLoopConfiguration(loopConfiguration);
		processState1.setSubProcessDefinition(criarComStatusProcesso());
		loopNode1.setLoopConfiguration(loopConfiguration);
		EndState endState = addNode(processDefinition, new EndState("Término"));
		addTransition(startNode, loopNode1);
		addTransition(loopNode1, processState1);
		addTransition(processState1, endState);
		return processDefinition;
	}

	private ProcessDefinition criarComStatusProcesso() {
		ProcessDefinition processDefinition = ProcessDefinition.createNewProcessDefinition();
		processDefinition.setName("teste");
		StartState startNode = addNode(processDefinition, new StartState("Início"));
		TaskNode taskNode = addNode(processDefinition, new TaskNode("taskNode"));
		EndState endState = addNode(processDefinition, new EndState("Término"));
		addTransition(startNode, taskNode);
		addTransition(taskNode, endState);
		Action action = new Action();
		action.setName(STATUS_PROCESSO_ACTION_NAME);
		Delegation delegation = new Delegation(StatusHandler.class.getName());
		delegation.setConfigType("constructor");
		delegation.setConfiguration("<statusProcesso>1</statusProcesso>");
		action.setActionDelegation(delegation);
		addAction(taskNode, Event.EVENTTYPE_NODE_ENTER, action);
		return processDefinition;
	}

	private void print(ProcessDefinition processDefinition, OutputStream outputStream) {
		print(processDefinition, new OutputStreamWriter(outputStream));
	}

	private void print(ProcessDefinition processDefinition, Writer writer) {
		new JpdlXmlWriter(writer).write(processDefinition, true);
	}

	private ProcessDefinition validate(ProcessDefinition processDefinition) {
		try {
			StringWriter writer = new StringWriter();
			print(processDefinition, writer);
			String xml = writer.toString();
			JpdlXmlReader reader = new JpdlXmlReader(new StringReader(xml));
			return reader.readProcessDefinition();
		} catch (JpdlException e) {
			StringBuilder sb = new StringBuilder();
			for (Problem p : (List<Problem>) e.getProblems()) {
				sb.append(p.getDescription()).append(System.lineSeparator());
			}
			Assert.fail(sb.toString());
		}
		return null;
	}

	@Test
	public void validateGenerateDocumento() {
		validate(criarComGenerateDocumento());
	}

	@Test
	public void validateStatusProcesso() {
		validate(criarComStatusProcesso());
	}

	private List<LoopConfigurationStandard> possibleStdLoopConfigurations() {
		List<LoopConfigurationStandard> list = new ArrayList<>();
		list.add(createStdLoopConfiguration("#{true}", 2L, true));
		list.add(createStdLoopConfiguration("#{true}", null, null));
		list.add(createStdLoopConfiguration(null, 2L, false));
		list.add(createStdLoopConfiguration(null, null, null));
		return list;
	}

	private List<LoopConfiguration> possibleMultiInstLoopConfigurations(){
		List<LoopConfiguration> list = new ArrayList<>();
		for (LoopConfigurationMultiInstanceFlowCondition behavior : LoopConfigurationMultiInstanceFlowCondition.values()) {
			list.add(createMultiLoopConfiguration(behavior, "#{true}", "entrada", Boolean.TRUE, "", "", "", "", "", ""));
			list.add(createMultiLoopConfiguration(behavior, "#{true}", "entrada", Boolean.FALSE, "", "", "", "", "", ""));
		}
		
		return list;
	}
	
	@Test
	public void validateMultiInstanceLoop(){
		for (LoopConfiguration loopConfiguration : possibleMultiInstLoopConfigurations()) {
			ProcessDefinition pd = criarComLoop(loopConfiguration);
			ProcessDefinition pd2 = validate(pd);
			TaskNode node = (TaskNode) pd.getNode("loopNode");
			TaskNode node2 = (TaskNode) pd2.getNode("loopNode");
			
			String expected=elementToString(node.getLoopConfiguration());
			String actual=elementToString(node2.getLoopConfiguration());
			Assert.assertEquals(expected, actual);
		}
	}
	
	private String elementToString(LoopConfiguration loopConfiguration){
		Element element = DocumentHelper.createElement("element");
		loopConfiguration.write(element);
		StringWriter writer = new StringWriter();
		XMLWriter xmlWriter = new XMLWriter(writer);
		try {
			xmlWriter.write(element);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return writer.toString();
	}
	
	
	@Test
	public void validateStandardLoop() {
		for (LoopConfigurationStandard loopConfiguration : possibleStdLoopConfigurations()) {
			ProcessDefinition pd = criarComLoop(loopConfiguration);
			ProcessDefinition pd2 = validate(pd);
			TaskNode node = (TaskNode) pd.getNode("loopNode");
			TaskNode node2 = (TaskNode) pd2.getNode("loopNode");
			
			String expected=elementToString(node.getLoopConfiguration());
			String actual=elementToString(node2.getLoopConfiguration());
			Assert.assertEquals(expected, actual);
		}
	}

	public static LoopConfigurationMultiInstance createMultiLoopConfiguration(LoopConfigurationMultiInstanceFlowCondition behavior, String completionCondition , String inputDataItem , Boolean isSequential , String loopCardinality , String loopDataInput , String loopDataOutput , String noneBehaviorEventRef , String oneBehaviorEventRef , String outputDataItem){
		LoopConfigurationMultiInstance lc = new LoopConfigurationMultiInstance();
		lc.setBehavior(behavior);
		lc.setCompletionCondition(completionCondition);
		lc.setInputDataItem(inputDataItem);
		lc.setIsSequential(isSequential);
		lc.setLoopCardinality(loopCardinality);
		lc.setLoopDataInput(loopDataInput);
		if (LoopConfigurationMultiInstanceFlowCondition.N.equals(behavior)){
			Event event = new Event();
			Action action = new Action();
			action.setActionExpression("#{expression}");
			event.addAction(action);
			lc.setNoneBehaviorEvent(event);
		} else if (LoopConfigurationMultiInstanceFlowCondition.N.equals(behavior)){
			Event event = new Event();
			Action action = new Action();
			action.setActionExpression("#{expression}");
			event.addAction(action);
			lc.setOneBehaviorEvent(event);
		}
//		lc.setLoopDataOutput(loopDataOutput);
//		lc.setNoneBehaviorEventRef(noneBehaviorEventRef);
//		lc.setOneBehaviorEventRef(oneBehaviorEventRef);
//		lc.setOutputDataItem(outputDataItem);
		return lc;
	}
	
	public static LoopConfigurationStandard createStdLoopConfiguration(String loopCondition, Long loopMaximum,
			Boolean testBefore) {
		LoopConfigurationStandard config = new LoopConfigurationStandard();
		if (loopCondition != null)
			config.setLoopCondition(loopCondition);
		if (loopMaximum != null)
			config.setLoopMaximum(loopMaximum);
		if (testBefore != null)
			config.setTestBefore(testBefore);
		return config;
	}

}
