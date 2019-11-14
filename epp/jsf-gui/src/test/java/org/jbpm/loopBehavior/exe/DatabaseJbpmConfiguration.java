package org.jbpm.loopBehavior.exe;

import java.util.UUID;

import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.db.GraphSession;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.persistence.PersistenceService;
import org.jbpm.persistence.db.DbPersistenceService;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.InputSource;

import br.com.infox.ibpm.jpdl.InfoxJpdlXmlReader;

public class DatabaseJbpmConfiguration {

	public void createProcessInstance(String name,JbpmConfiguration jbpmConfiguration) {
		jbpmConfiguration.createSchema();
		ProcessDefinition processDefinition = getGraphSession(jbpmConfiguration).findLatestProcessDefinition("teste");

		ProcessInstance processInstance = new ProcessInstance(processDefinition);

		Token token = processInstance.getRootToken();

		while (!processInstance.hasEnded()) {
			if (token.getNode() instanceof TaskNode) {
				TaskNode taskNode = (TaskNode) token.getNode();
				for (TaskInstance taskInstance : processInstance.getTaskMgmtInstance().getUnfinishedTasks(token)) {
					taskInstance.end(taskNode.getLeavingTransitions().iterator().next().getName());
				}
			} else {
				token.signal();
			}
		}

	}
	
	@Test
	public void deployFluxoLoop(){
		try {
			JbpmConfiguration jbpmConfiguration = JbpmConfiguration.parseInputStream(ClassLoader.getSystemResourceAsStream("mock-jbpm/jbpm.cfg.xml"));
			InputSource is = new InputSource(ClassLoader.getSystemResourceAsStream("mock-jbpm/fluxo-loop.xml"));
			InfoxJpdlXmlReader reader = new InfoxJpdlXmlReader(is);
			ProcessDefinition processDefinition = reader.readProcessDefinition();
			processDefinition.setName("loop-config-"+UUID.randomUUID().toString());
			JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
			PersistenceService persistenceService = jbpmContext.getServices().getPersistenceService();
			GraphSession graphSession = null;
			if (persistenceService instanceof DbPersistenceService) {
				DbPersistenceService dbPersistenceService = (DbPersistenceService) persistenceService;
				graphSession = dbPersistenceService.getGraphSession();
				graphSession.deployProcessDefinition(processDefinition);
				dbPersistenceService.getSession().flush();
				dbPersistenceService.close();
			}
		} catch (Exception e){
			e.printStackTrace(System.out);
			Assert.fail(e.getMessage());
		}
		
	}
	
	private GraphSession getGraphSession(JbpmConfiguration jbpmConfiguration) {
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		PersistenceService persistenceService = jbpmContext.getServices().getPersistenceService();
		GraphSession graphSession = null;
		if (persistenceService instanceof DbPersistenceService) {
			DbPersistenceService dbPersistenceService = (DbPersistenceService) persistenceService;
			graphSession = dbPersistenceService.getGraphSession();
		}
		return graphSession;
	}

}
