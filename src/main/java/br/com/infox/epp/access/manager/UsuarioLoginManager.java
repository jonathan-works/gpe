package br.com.infox.epp.access.manager;

import java.util.Date;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.access.dao.UsuarioLoginDAO;
import br.com.infox.epp.access.entity.UsuarioLogin;

@Name(UsuarioLoginManager.NAME)
@AutoCreate
public class UsuarioLoginManager extends Manager<UsuarioLoginDAO, UsuarioLogin> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "usuarioLoginManager";

    public boolean usuarioExpirou(final UsuarioLogin usuarioLogin) {
        boolean result = Boolean.FALSE;
        if (usuarioLogin != null) {
            final Date dataExpiracao = usuarioLogin.getDataExpiracao();
            result = usuarioLogin.getProvisorio() && dataExpiracao != null
                    && dataExpiracao.before(new Date());
        }
        return result;
    }

    public void inativarUsuario(final UsuarioLogin usuario) {
        getDao().inativarUsuario(usuario);
    }

    public UsuarioLogin getUsuarioLoginByEmail(final String email) {
        return getDao().getUsuarioLoginByEmail(email);
    }

    public UsuarioLogin getUsuarioLoginByLogin(final String login) {
        return getDao().getUsuarioLoginByLogin(login);
    }
    
    public String getActorIdTarefaAtual(Integer idProcesso){
        return getDao().getActorIdTarefaAtual(idProcesso);
    }
    
    public String getUsuarioByTarefa(TaskInstance taskInstance) {
        return getDao().getUsuarioByTarefa(taskInstance);
    }

}
