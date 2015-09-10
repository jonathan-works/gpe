package br.com.infox.ibpm.util;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.UsuarioLoginManager;

@Name(UserHandler.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class UserHandler {
    public static final String NAME = "userHandler";

    private TaskInstance taskInstance;
    private String usuarioTarefa;
    private Integer idTarefa;

    @In
    private UsuarioLoginManager usuarioLoginManager;

    public String getUsuarioByIdTarefa(Integer idTarefa, Integer idProcesso) {
    	if (!idTarefa.equals(this.idTarefa)) {
    		this.idTarefa = idTarefa;
    		this.usuarioTarefa = usuarioLoginManager.getNomeUsuarioByIdTarefa(idTarefa, idProcesso);
    	}
    	return this.usuarioTarefa;
    }
    
    public String getUsuarioByTaskInstance(TaskInstance taskInstance) {
        if (this.taskInstance == null || !this.taskInstance.equals(taskInstance)) {
            this.taskInstance = taskInstance;
            this.usuarioTarefa = usuarioLoginManager.getNomeUsuarioByTaskInstance(taskInstance);
        }
        return this.usuarioTarefa;
    }
    
    public UsuarioLogin getUsuario(String login) {
        if (login == null || "".equals(login)) {
            return null;
        }
        return usuarioLoginManager.getUsuarioLoginByLogin(login);
	}
    
    public void clear() {
    	this.usuarioTarefa = null;
    	this.idTarefa = null;
    }
}
