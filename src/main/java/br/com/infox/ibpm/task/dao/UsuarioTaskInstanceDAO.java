package br.com.infox.ibpm.task.dao;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.ibpm.task.entity.UsuarioTaskInstance;
import br.com.infox.ibpm.task.query.UsuarioTaskInstanceQuery;

@Name(UsuarioTaskInstanceDAO.NAME)
@AutoCreate
public class UsuarioTaskInstanceDAO extends DAO<UsuarioTaskInstance> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "usuarioTaskInstanceDAO";

    public Localizacao getLocalizacaoTarefa(long idTaskInstance) {
        Map<String, Object> params = new HashMap<>();
        params.put(UsuarioTaskInstanceQuery.ID_TASKINSTANCE_PARAM, idTaskInstance);
        return getNamedSingleResult(UsuarioTaskInstanceQuery.LOCALIZACAO_DA_TAREFA, params);
    }
}
