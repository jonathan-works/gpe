package br.com.infox.ibpm.jbpm.importe;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.activity.InvalidActivityException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Redirect;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;
import org.jbpm.graph.action.Script;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.node.EndState;
import org.jbpm.graph.node.Fork;
import org.jbpm.graph.node.Join;
import org.jbpm.graph.node.NodeTypes;
import org.jbpm.graph.node.ProcessState;
import org.jbpm.graph.node.StartState;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.taskmgmt.def.Swimlane;
import org.jbpm.taskmgmt.def.Task;
import org.jdom.Document;
import org.jdom.Element;

import br.com.infox.ibpm.jbpm.JpdlXmlWriter;
import br.com.infox.ibpm.jbpm.handler.TaskHandler;
import br.com.infox.ibpm.jbpm.node.DecisionNode;
import br.com.itx.util.XmlUtil;
import br.com.infox.ibpm.jbpm.ProcessBuilder;

@Name(ImportarXPDL.NAME)
@Scope(ScopeType.CONVERSATION)
@BypassInterceptors
public class ImportarXPDL implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "importarXPDL";
	public static final String POOLS = "Pools";
	public static final String ASSOCIATIONS = "Associations";
	public static final String ARTIFACTS = "Artifacts";
	public static final String WORKFLOW_PROCESSES = "WorkflowProcesses";
	public static final String EXTENDED_ATTRIBUTES= "ExtendedAttributes";
	public static final String ACTIVITES = "Activities";
	public static final String TRANSITIONS = "Transitions";
	public static final String NO_NAME = "Indefinido ";
	private Log log = Logging.getLog(this.getClass());
	private int index = 1;
	
	public void importarXPDL(byte[] bytes, String cdFluxo) {
		ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
		Document doc = XmlUtil.readDocument(stream);
		Element root = doc.getRootElement();
		List<Element> pools = obterElements(root, POOLS);
		List<Element> workFlow = obterElements(root, WORKFLOW_PROCESSES);
		gereFluxo(pools, workFlow, cdFluxo);
	}
	
	@SuppressWarnings("rawtypes")
	public List<Element> obterElements(Element ele, String filter) {
		List<Element> lista = null;
		if(ele != null && filter != null) {
			lista = new ArrayList<Element>();
			List l = ele.getContent();
			Iterator it = l.iterator();
			Element e = null;
			while(it.hasNext()) {
				Object ob = it.next();
				if(ob instanceof Element) {
					e = (Element)ob;
					if(e.getName().equals(filter))
						lista.add(e);
				}
			}
		}
		return lista;
	}
	
	private void gereFluxo(List<Element> poolsList, List<Element> workflowList, String cdFluxo) {
		if(poolsList != null && poolsList.size() > 1) {
			mensagemErro("XPDL inválido. " + (poolsList == null ? "Não há nenhuma piscina definida.": "Não mais de uma piscina definida."));
			return;
		}
		Element pools = (Element)poolsList.get(0);
		List<Element> pool = obterElements(pools, "Pool");
		String nome = pool.get(1).getAttributeValue("Name");
		List<Element> raias = obterElements(pool.get(1), "Lanes");
		List<Element> raia = obterElements(raias.get(0), "Lane");
		List<Element> workflows = obterElements(workflowList.get(0), "WorkflowProcess");
		List<Element> activities = obterElements(workflows.get(1), ACTIVITES);
		List<Element> activity = obterElements(activities.get(0), "Activity");
		List<Element> transicoes = obterElements(workflows.get(1), TRANSITIONS);
		List<Element> transicao = obterElements(transicoes.get(0), "Transition");
		gereXML(nome, raia, activity, transicao);
	}
	
	@SuppressWarnings("rawtypes")
	private void gereXML(String nome, List raias, List nos, List transicoes) {
		
		try {
			index = 1;
		ProcessDefinition definition = ProcessDefinition.createNewProcessDefinition();
		definition.setName(nome);
		addSwinlanes(raias, definition);
		addNodes(nome, nos, transicoes, definition);
		addEvents(definition);
		String xml = JpdlXmlWriter.toString(definition);
		//System.out.println(xml);
		ProcessBuilder process = ProcessBuilder.instance();
		process.setXml(xml);
		Redirect redirect = Redirect.instance();
		redirect.setParameter("id", nome);
		redirect.setViewId("/Fluxo/definicao/processDefinition.xhtml");
		redirect.execute();
		//TODO:Redirecionar
		
		} catch (Exception e) {
			System.err.println("Classe Java: ImportarXPDL. Erro msg: " + e.getMessage());
			mensagemErro(e.getMessage());
		}
		
	}
	
	@SuppressWarnings("rawtypes")
	private void addSwinlanes(List raias, ProcessDefinition definition) {
		Swimlane s = null; Element raia=null;
		for(int i = 0; i < raias.size(); i++) {
			raia = (Element)raias.get(i);
			s = new Swimlane(raia.getAttributeValue("Name"));
			definition.getTaskMgmtDefinition().addSwimlane(s);
		}
	}
	
	@SuppressWarnings("rawtypes")
	private void addNodes(String name, List nos, List transicoes, ProcessDefinition definition) throws InvalidActivityException, ParallelNodeException {
		Map<String, Node> map = new HashMap<String, Node>();
		ArrayList<Node> l = new ArrayList<Node>();
		boolean parallel = false; int end =0; //begin = 0;
		Node no = null; Element ele = null;
		for(int i = 0; i < nos.size(); i++) {
			ele = (Element)nos.get(i);
			String nome = ele.getAttributeValue("Name");
			String id = ele.getAttributeValue("Id");
			//Verificar o tipo
			String type = verificarTypenode(ele);
			if(type == null) {
				throw new InvalidActivityException("O Sistema e-PA não aceita o Nó do tipo: " + ele.getName());
			}
			if(type.equals("Parallel"))
				parallel = true;
			else if(type.equals("EndState"))
				end++;
//			else if(type.equals("StartEvent"))
//				begin++;
			
			//Gerar o Nó
			no = gereNode(type, nome, definition);
			if(no != null) {
				map.put(id, no);
				l.add(no);
			}
		}
		//Gerando as transições
		for(int i =0; i < transicoes.size(); i++) {
			ele = (Element)transicoes.get(i);
			String from = ele.getAttributeValue("From");
			String to = ele.getAttributeValue("To");
			String nome = ele.getAttributeValue("Name");
			Node de = map.get(from);
			Node para = map.get(to);
			setTransiction(de, para, nome, definition);
		}
		//gerando os os Nodes Join e Fork
		if(parallel) 
			transformeParallelNode(l);
		//Mais de um Nó de finalização
		if(end > 1)
			removeExcessEndStat(l);
		//Ordenação simples
		ordeneNodes(l);
		Iterator<Node> it = l.iterator();
		while(it.hasNext()) {
			Node n = it.next();
			definition.addNode(n);
		}
		definition.setDescription(name);
	}
	
	/**
	 * Método responsável por garantir apenas um End Stat
	 */
	private void removeExcessEndStat(ArrayList<Node> lista) {
		if(lista != null) {
			Map<Node, Integer> ends = getEndNodes(lista);
			Set<Node> keys = ends.keySet();
			Iterator<Node> it = keys.iterator();
			Node end = it.next();
			for(Node n:lista) {
				checkTransitionToEndStatAndChangeIt(n, keys, end);
			}
			keys.remove(end);
			Iterator<Node> it2 = keys.iterator();
			while(it2.hasNext())
				lista.remove(it2.next());
		}
	}
	
	/**
	 * Método responsável por verificar se o nó possui uma transição para o estado final.
	 * Caso positivo, então ele muda a transição para o nó fim.
	 * @param node
	 * @param keys
	 * @return
	 */
	private boolean checkTransitionToEndStatAndChangeIt(Node node, Set<Node> keys, Node fim) {
		boolean find = false;
		if(keys != null && !keys.isEmpty() && node != null) {
			Iterator<Node> it = keys.iterator();
			while(it.hasNext() && !find) {
				Node end = it.next();
				List<Transition> leaves = node.getLeavingTransitions();
				int i = 0; Transition t = null;
				while(leaves != null && i < leaves.size() && !find){
					t = leaves.get(i++);
					if(end == t.getTo() || end == t.getFrom()) {
						find = true;
						if(end == t.getTo() && t.getTo() != fim) {
							t.setTo(fim);
							//t.setName(fim.getName());
						}
						else if(t.getFrom() != fim){
							t.setFrom(fim);
						}
					}
				}
			}
		}
		return find;
	}
	
	/**
	 * Transforma os Nós Parallel (Notação XPDML) em Fork ou Join
	 * @param lista
	 * @throws ParallelNodeException 
	 */
	private void transformeParallelNode(ArrayList<Node> lista) throws ParallelNodeException {
		if(lista != null) {
			Map<Node, Integer> parallel = getParallelNodes(lista);
			Set<Node> para = parallel.keySet();
			Iterator<Node> iterator = para.iterator();
			Node temp = null; Set<Transition> arrives = null;
			List<Transition> leaves = null;
			while(iterator.hasNext()) {
				temp = iterator.next();
				arrives = temp.getArrivingTransitions();
				leaves = temp.getLeavingTransitions();
				if(arrives == null || arrives.isEmpty() || leaves == null || leaves.isEmpty() ||
					(arrives.size() > 1 && leaves.size() > 1)) {
					String name = temp.getName();
					throw new ParallelNodeException("Impossível verificar se o Nó (" + 
					(name != null? name:"Paralelo") + ") é do tipo Join ou Fork.");
				}
				else if(arrives.size() == 1 && leaves.size() > 1) {
					Fork node = new Fork();
					preencherNo(lista, parallel, temp, arrives, leaves, node);
				}
				else if(leaves.size() == 1 && arrives.size() > 1) {
					Join node = new Join();
					preencherNo(lista, parallel, temp, arrives, leaves, node);
				}
				else 
					throw new ParallelNodeException("Impossível verificar se o Nó (" + 
							(temp.getName() != null? temp.getName():"Paralelo") + ") é do tipo Join ou Fork.");
			}
		}
	}

	private void preencherNo(ArrayList<Node> lista, Map<Node, Integer> parallel, 
			Node temp, Set<Transition> arrives, List<Transition> leaves, Node node) {
		node.setName(temp.getName());
		node.setProcessDefinition(node.getProcessDefinition());
		Iterator<Transition> it2 = arrives.iterator();
		while(it2.hasNext())
			node.addArrivingTransition(it2.next());
		Iterator<Transition> it3 = leaves.iterator();
		while(it3.hasNext())
			node.addLeavingTransition(it3.next());
		lista.set(parallel.get(temp), node);
	}
	
	private Map<Node, Integer> getParallelNodes(ArrayList<Node> l) {
		Map<Node, Integer> lista = null;
		if(l != null) {
			lista = new HashMap<Node, Integer>();
			int i = 0;
			for(Node n : l) {
				if(n instanceof ParallelNode)
					lista.put(n, i);
				i++;
			}
		}
		return lista;
	}
	
	private Map<Node, Integer> getEndNodes(ArrayList<Node> l) {
		HashMap<Node, Integer> lista = null;
		if(l != null) {
			lista = new HashMap<Node, Integer>();
			int i = 0;
			for(Node n : l) {
				if(n instanceof EndState)
					lista.put(n, i);
				i++;
			}
		}
		return lista;
	}
	
	/**
	 * Garantir que o Nó inicial fique no início e o Nó final no fim
	 * @param l
	 */
	private void ordeneNodes(ArrayList<Node> l) {
		Node start = null; int ini = -1;
		Node end = null; int fim = -1; int i = 0;
		for(Node n : l) {
			if(n instanceof StartState) {
				start = n;
				ini = i;
			}
			else if(n instanceof EndState) {
				end = n;
				fim = i;
			}
			i++;
		}
		if(ini != 0) {
			Node temp = l.get(0);
			l.set(0, start);
			l.set(ini, temp);
		}
		if(fim !=  l.size()-1) {
			Node temp = l.get(l.size()-1);
			l.set(l.size()-1, end);
			l.set(fim, temp);
		}
	}

	@SuppressWarnings("rawtypes")
	/**
	 * Transforma o tipo do XPDL para o tipo do sistema
	 * @param ele
	 * @param type
	 * @return
	 */
	public String verificarTypenode(Element ele) {
		String tipo = null;
		List event = ele.getChildren("Event", ele.getNamespace());
		List impl = ele.getChildren("Implementation", ele.getNamespace());
		List route = ele.getChildren("Route", ele.getNamespace());
		if(event != null && !event.isEmpty()) {
			Element temp = (Element)event.get(0);
			List start = temp.getChildren("StartEvent", temp.getNamespace());
			List end = temp.getChildren("EndEvent", temp.getNamespace());
			List inter = temp.getChildren("IntermediateEvent", temp.getNamespace());
			if(start != null && !start.isEmpty()) {
				tipo = "StartState";
			}
			else if(end != null && !end.isEmpty())
				tipo = "EndState";
			else if(inter != null && !inter.isEmpty()) {
				Element temp2 = (Element)event.get(0);
				String value = temp2.getAttributeValue("Trigger", temp2.getNamespace());
				if(value != null && "Message".equalsIgnoreCase(value))
					tipo = "MailNode";
				else
					tipo = "Node";
			}
		}
		else if(impl != null && !impl.isEmpty()) {
			Element temp = (Element)impl.get(0);
			List task = temp.getChildren("Task", temp.getNamespace());
			List sub = temp.getChildren("SubFlow", temp.getNamespace());
			if(task != null && !task.isEmpty()) {
				tipo = "Task";
			}
			else if(sub != null && !sub.isEmpty())
				tipo = null; //"ProcessState"; SUB-PROCESSO
		}
		else if(route != null && !route.isEmpty()) {
			Element temp = (Element)route.get(0);
			String gateway = temp.getAttributeValue("GatewayType");
			if(gateway != null && !gateway.isEmpty() && "Parallel".equalsIgnoreCase(gateway))
				tipo = "Parallel";
			else
				tipo = "Decision";
		}
		return tipo;
	}
	
	private Node gereNode(String type, String nome, ProcessDefinition definition) {
		Node node = null;
		Class<?> nodeType = null;
		if("Parallel".equalsIgnoreCase(type))
			nodeType = ParallelNode.class;
		else
			nodeType = NodeTypes.getNodeType(getNodeType(type));
		try {
			if(nodeType != null) {
				if("Parallel".equalsIgnoreCase(type))
					node = new ParallelNode();
				else
					node = (Node)nodeType.newInstance();
				if(nome != null && !nome.isEmpty())
					node.setName(nome);
				else {
					if(node instanceof StartState || node instanceof EndState)
						node.setName((node instanceof StartState ? "Início" : "Fim"));
					else
						node.setName(NO_NAME + index++);
				}
				if (node instanceof TaskNode || node instanceof StartState || node instanceof ProcessState) {
					addTask(node, definition);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return node;
	}
	
	public void addTask(Node node, ProcessDefinition definition) {
		if (node instanceof TaskNode) {
			TaskNode tn = (TaskNode) node;
			Task t = new Task();
			t.setProcessDefinition(definition);
			t.setTaskMgmtDefinition(definition.getTaskMgmtDefinition());
			t.setName(node.getName());
			t.setSwimlane(definition.getTaskMgmtDefinition().getSwimlanes().values().iterator().next());
			tn.setEndTasks(true);
			tn.addTask(t);
		}
		else if (node instanceof StartState) {
			Task startTask = new Task("Tarefa inicial");
			startTask.setSwimlane(definition.getTaskMgmtDefinition().getSwimlanes().values().iterator().next());
			TaskHandler startTaskHandler = new TaskHandler(startTask);
			definition.getTaskMgmtDefinition().setStartTask(startTaskHandler.getTask());
		}
		else if(node instanceof ProcessState) {
			ProcessState sub = (ProcessState)node;
			ProcessDefinition subProc = ProcessDefinition.createNewProcessDefinition();
			subProc.setDescription(NO_NAME + index++);
			subProc.setName(NO_NAME + index++);
			sub.setSubProcessDefinition(subProc);
			
		}
	}
	
	
	private void setTransiction(Node atual, Node next, String nome, ProcessDefinition definition) {
		Transition t = new Transition();
		t.setProcessDefinition(definition);
		next.addArrivingTransition(t);
		atual.addLeavingTransition(t);
		if(nome != null && !nome.isEmpty())
			t.setName(nome);
		else
			t.setName(next.getName());
	}
	
	public String getNodeType(String nodeType) {
		if (nodeType.equals("Task")) {
			return "task-node";
		}
		if (nodeType.equals("MailNode")) {
			return "mail-node";
		}
		if (nodeType.equals("StartState")) {
			return "start-state";
		}
		if (nodeType.equals("EndState")) {
			return "end-state";
		}
		if (nodeType.equals("ProcessState")) {
			return "process-state";
		}
		return nodeType.substring(0, 1).toLowerCase() + nodeType.substring(1);
	}
	
	/* 
	* Metodo que adiciona o tratamento de eventos 
	 */
	private void addEvents(ProcessDefinition definition) {
		for (String e : ProcessDefinition.supportedEventTypes) {
			addEvent(e, "br.com.infox.ibpm.util.JbpmEvents.raiseEvent(executionContext)", new Script(), definition);
		}
	}
	
	private void addEvent(String eventType, String expression, Action action, ProcessDefinition definition) {
		Event event = definition.getEvent(eventType);
		if (event == null) {
			event = new Event(eventType);
			definition.addEvent(event);
		}
		action.setAsync(false);
		if (action instanceof Script) {
			Script script = (Script) action;
			script.setExpression(expression);
		} else {
			action.setActionExpression(expression);		
		}
		event.addAction(action);
	}
	
	
	private void mensagemErro(String msg) {
		FacesMessages.instance().add(Severity.INFO, msg);
		log.error(msg);
	}
	
	private class ParallelNode extends DecisionNode {

		private static final long serialVersionUID = 1L;
		
	}
	
}
