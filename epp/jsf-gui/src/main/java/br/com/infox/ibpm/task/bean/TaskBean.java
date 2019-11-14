package br.com.infox.ibpm.task.bean;

import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.ibpm.task.entity.UsuarioTaskInstance;

public class TaskBean {

	private TaskInstance taskInstance;
	private UsuarioTaskInstance usuarioTaskInstance;

	public TaskBean(TaskInstance taskInstance, UsuarioTaskInstance usuarioTaskInstance) {
		this.taskInstance = taskInstance;
		this.usuarioTaskInstance = usuarioTaskInstance;
	}

	public TaskInstance getTaskInstance() {
		return taskInstance;
	}

	public void setTaskInstance(TaskInstance taskInstance) {
		this.taskInstance = taskInstance;
	}

	public UsuarioTaskInstance getUsuarioTaskInstance() {
		return usuarioTaskInstance;
	}

	public void setUsuarioTaskInstance(UsuarioTaskInstance usuarioTaskInstance) {
		this.usuarioTaskInstance = usuarioTaskInstance;
	}

}
