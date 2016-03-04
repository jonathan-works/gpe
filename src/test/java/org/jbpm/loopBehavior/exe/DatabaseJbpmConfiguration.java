package org.jbpm.loopBehavior.exe;

import java.io.InputStream;
import java.sql.SQLException;

import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.db.GraphSession;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.loopBehavior.exe.Consumer;
import org.jbpm.persistence.PersistenceService;
import org.jbpm.persistence.db.DbPersistenceService;
import org.jbpm.taskmgmt.exe.TaskInstance;

public class DatabaseJbpmConfiguration {

	public void beforeInit() {
		jbpmConfiguration.createSchema();
	}

	public void afterCompletion() {
		jbpmConfiguration.dropSchema();
	}

	static JbpmConfiguration jbpmConfiguration = null;

	public void createProcessInstance(String name) {
		ProcessDefinition processDefinition = getGraphSession().findLatestProcessDefinition("teste");

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

	// @BeforeClass
	public static void configuration()
			throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
		InputStream inputStream = ClassLoader.getSystemResourceAsStream("mock-jbpm/jbpm.cfg.xml");
		if (inputStream != null) {
			jbpmConfiguration = JbpmConfiguration.parseInputStream(inputStream);
		}
	}

	// @Before
	public void initDatabase() {
	}

	// @After
	public void closeDatabase() {
	}

	private void deploy(final ProcessDefinition processDefinition) {
		execute(new Consumer<GraphSession>() {
			@Override
			public void consume(GraphSession obj) {
				obj.deployProcessDefinition(processDefinition);
			}
		});
	}

	private void execute(Consumer<GraphSession> runnable) {
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		PersistenceService persistenceService = jbpmContext.getServices().getPersistenceService();
		GraphSession graphSession = null;
		if (persistenceService instanceof DbPersistenceService) {
			DbPersistenceService dbPersistenceService = (DbPersistenceService) persistenceService;
			graphSession = dbPersistenceService.getGraphSession();
			runnable.consume(graphSession);
			dbPersistenceService.getSession().flush();
			dbPersistenceService.close();
		}
	}

	private GraphSession getGraphSession() {
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
