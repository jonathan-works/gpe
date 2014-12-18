package br.com.infox.epp.processo.localizacao.dao;

import static br.com.infox.epp.processo.localizacao.query.ProcessoLocalizacaoIbpmQuery.COUNT_PROCESSO_LOCALIZACAO_IBPM_BY_ATTRIBUTES;
import static br.com.infox.epp.processo.localizacao.query.ProcessoLocalizacaoIbpmQuery.DELETE_BY_PROCESS_ID_AND_TASK_ID;
import static br.com.infox.epp.processo.localizacao.query.ProcessoLocalizacaoIbpmQuery.LIST_BY_TASK_INSTANCE;
import static br.com.infox.epp.processo.localizacao.query.ProcessoLocalizacaoIbpmQuery.LIST_ID_TASK_INSTANCE_BY_ID_TAREFA;
import static br.com.infox.epp.processo.localizacao.query.ProcessoLocalizacaoIbpmQuery.LIST_ID_TASK_INSTANCE_BY_LOCALIZACAO_PAPEL;
import static br.com.infox.epp.processo.localizacao.query.ProcessoLocalizacaoIbpmQuery.PARAM_ID_TASK;
import static br.com.infox.epp.processo.localizacao.query.ProcessoLocalizacaoIbpmQuery.PARAM_ID_TASK_INSTANCE;
import static br.com.infox.epp.processo.localizacao.query.ProcessoLocalizacaoIbpmQuery.PARAM_LOCALIZACAO;
import static br.com.infox.epp.processo.localizacao.query.ProcessoLocalizacaoIbpmQuery.PARAM_PAPEL;
import static br.com.infox.epp.processo.localizacao.query.ProcessoLocalizacaoIbpmQuery.PARAM_PROCESSO;
import static br.com.infox.epp.processo.localizacao.query.ProcessoLocalizacaoIbpmQuery.PARAM_PROCESS_ID;
import static br.com.infox.epp.processo.localizacao.query.ProcessoLocalizacaoIbpmQuery.PARAM_TASK_ID;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.filter.ControleFiltros;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.localizacao.entity.ProcessoLocalizacaoIbpm;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraColegiada;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraMonocratica;
import br.com.infox.seam.util.ComponentUtil;

@Name(ProcessoLocalizacaoIbpmDAO.NAME)
@AutoCreate
public class ProcessoLocalizacaoIbpmDAO extends DAO<ProcessoLocalizacaoIbpm> {
    private static final long serialVersionUID = 1L;
    public static final String NAME = "processoLocalizacaoIbpmDAO";
    
    public Localizacao listByTaskInstance(Long idTaskInstance) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(PARAM_ID_TASK_INSTANCE, idTaskInstance);
        return getNamedSingleResult(LIST_BY_TASK_INSTANCE, parameters);
    }

    public boolean possuiPermissao(Processo processo) {
        if (Authenticator.getUsuarioPerfilAtual().getPerfilTemplate().getLocalizacao() == null) {
            return false;
        }
        if (!isUsuarioLogadoEmUnidadesDecisorasDoProcesso(processo)) {
            return false;
        }
        ControleFiltros.instance().iniciarFiltro();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_PROCESSO, processo);
        parameters.put(PARAM_LOCALIZACAO, Authenticator.getUsuarioPerfilAtual().getPerfilTemplate().getLocalizacao());
        parameters.put(PARAM_PAPEL, Authenticator.getPapelAtual());
        Long count = getNamedSingleResult(COUNT_PROCESSO_LOCALIZACAO_IBPM_BY_ATTRIBUTES, parameters);
        return count != null && count > 0;
    }
    
    private boolean isUsuarioLogadoEmUnidadesDecisorasDoProcesso(Processo processo) {
        UnidadeDecisoraMonocratica monocraticaLogada = getAuthenticator().getMonocraticaLogada();
        UnidadeDecisoraColegiada colegiadaLogada = getAuthenticator().getColegiadaLogada();
        MetadadoProcesso metadado = processo.getMetadado(EppMetadadoProvider.UNIDADE_DECISORA_MONOCRATICA);
        UnidadeDecisoraMonocratica monocraticaDoProcesso = null;
        if (metadado != null) {
        	monocraticaDoProcesso = metadado.getValue();
        }
        UnidadeDecisoraColegiada colegiadaDoProcesso = null;
        metadado = processo.getMetadado(EppMetadadoProvider.UNIDADE_DECISORA_COLEGIADA);
        if (metadado != null) {
        	colegiadaDoProcesso = metadado.getValue();
        }
        return (monocraticaLogada == null && monocraticaDoProcesso == null && colegiadaLogada == null && colegiadaDoProcesso == null) 
                || (monocraticaLogada != null && colegiadaLogada == null && monocraticaLogada.equals(monocraticaDoProcesso)) 
                || (colegiadaLogada != null && monocraticaLogada == null && colegiadaLogada.equals(colegiadaDoProcesso))
                || (monocraticaLogada != null && colegiadaLogada != null && monocraticaLogada.equals(monocraticaDoProcesso)) && colegiadaLogada.equals(colegiadaDoProcesso);
    }
    
    public Long getTaskInstanceId(UsuarioPerfil usuarioPerfil, Processo processo,
            Long idTarefa) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(PARAM_PROCESSO, processo);
        parameters.put(PARAM_LOCALIZACAO, usuarioPerfil.getPerfilTemplate().getLocalizacao());
        parameters.put(PARAM_PAPEL, usuarioPerfil.getPerfilTemplate().getPapel());
        parameters.put(PARAM_ID_TASK, idTarefa.intValue());
        return getNamedSingleResult(LIST_ID_TASK_INSTANCE_BY_ID_TAREFA, parameters);
    }

    public Long getTaskInstanceId(UsuarioPerfil usuarioPerfil, Processo processo) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(PARAM_PROCESSO, processo);
        parameters.put(PARAM_LOCALIZACAO, usuarioPerfil.getPerfilTemplate().getLocalizacao());
        parameters.put(PARAM_PAPEL, usuarioPerfil.getPerfilTemplate().getPapel());

        return getNamedSingleResult(LIST_ID_TASK_INSTANCE_BY_LOCALIZACAO_PAPEL, parameters);
    }

    public void deleteProcessoLocalizacaoIbpmByTaskIdAndProcessId(Long taskId,
            Long processId) throws DAOException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_PROCESS_ID, processId);
        parameters.put(PARAM_TASK_ID, taskId);
        executeNamedQueryUpdate(DELETE_BY_PROCESS_ID_AND_TASK_ID, parameters);
    }
    
    private Authenticator getAuthenticator() {
        return ComponentUtil.getComponent(Authenticator.NAME);
    }

}
