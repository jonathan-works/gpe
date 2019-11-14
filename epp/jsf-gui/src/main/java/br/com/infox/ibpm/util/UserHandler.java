package br.com.infox.ibpm.util;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.UsuarioLoginManager;

@Named
@RequestScoped
public class UserHandler {

    private TaskInstance taskInstance;
    private String usuarioTarefa;

    @Inject
    private UsuarioLoginManager usuarioLoginManager;

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
    
}
