package br.com.infox.ibpm.task.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.processo.entity.Processo;
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
    
    public List<Localizacao> getLocalizacoes(Processo processo) {
        Map<String, Object> params = new HashMap<>(1);
        params.put(UsuarioTaskInstanceQuery.PARAM_PROCESSO, processo.getIdProcesso());
        return getNamedResultList(UsuarioTaskInstanceQuery.LOCALIZACOES_DO_PROCESSO, params);
    }
    
    public List<UsuarioTaskInstance> getByLocalizacaoExterna(Integer idLocalizacao, Integer idProcesso) {
        Map<String, Object> params = new HashMap<>(2);
        params.put(UsuarioTaskInstanceQuery.PARAM_PROCESSO, idProcesso);
        params.put(UsuarioTaskInstanceQuery.PARAM_LOCALIZACAO, idLocalizacao);
        return getNamedResultList(UsuarioTaskInstanceQuery.USUARIO_DA_LOCALIZACAO_DO_PROCESSO, params);
    }
}
