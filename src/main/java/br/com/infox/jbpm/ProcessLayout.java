package br.com.infox.jbpm;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jbpm.db.GraphSession;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.ibpm.jbpm.JbpmUtil;
import br.com.infox.jbpm.layout.JbpmLayout;
import br.com.infox.util.constants.WarningConstants;

@Name(ProcessLayout.NAME)
@Scope(ScopeType.CONVERSATION)
@BypassInterceptors
public class ProcessLayout implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String NAME = "processLayout";
	private ProcessDefinition processDefinition;
	private JbpmLayout layout;
	private Long idProcessInstance;
	
	private synchronized JbpmLayout getLayout() {
		if (layout == null) {
			layout = new JbpmLayout(processDefinition, getTaskMap());
		}
		return layout;
	}
	
	@SuppressWarnings(WarningConstants.UNCHECKED)
	private Map<Node, TaskInstance> getTaskMap() {
		Map<Node, TaskInstance> map = null;
		if (idProcessInstance != null) {
			map = new LinkedHashMap<Node, TaskInstance>();
			List<TaskInstance> list = JbpmUtil.getJbpmSession()
				.getNamedQuery("GraphSession.findTaskInstancesForProcessInstance")
				.setLong("processInstance", (long) idProcessInstance)
				.list();
			for (TaskInstance ti : list) {
				map.put(ti.getTask().getTaskNode(), ti);
			}
		}
		return map;
	}

	public void paintGraph(OutputStream out, Object data) throws IOException {
		getLayout().paint(out);
	}

	public String getMap() {
		return getLayout().getMap();
	}

	public void setProcessDefinition(ProcessDefinition processDefinition) {
		this.processDefinition = processDefinition;
	}

	public ProcessDefinition getProcessDefinition() {
		return processDefinition;
	}

	public void setIdProcessInstance(Long idProcessInstance) {
		this.idProcessInstance = idProcessInstance;
		ProcessInstance pi = new GraphSession(JbpmUtil.getJbpmSession())
			.getProcessInstance(idProcessInstance);
		processDefinition = pi.getProcessDefinition();
	}

	public Long getIdProcessInstance() {
		return idProcessInstance;
	}
	
}