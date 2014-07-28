package br.com.infox.ibpm.task.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.ibpm.task.dao.UsuarioTaskInstanceDAO;
import br.com.infox.ibpm.task.entity.UsuarioTaskInstance;

@Name(UsuarioTaskInstanceManager.NAME)
@AutoCreate
public class UsuarioTaskInstanceManager extends Manager<UsuarioTaskInstanceDAO, UsuarioTaskInstance> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "usuarioTaskInstanceManager";

    public Localizacao getLocalizacaoTarefa(long idTaskInstance) {
        return getDao().getLocalizacaoTarefa(idTaskInstance);
    }
}
