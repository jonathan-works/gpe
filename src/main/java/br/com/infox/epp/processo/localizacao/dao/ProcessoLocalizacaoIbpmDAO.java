package br.com.infox.epp.processo.localizacao.dao;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.UsuarioLocalizacao;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.home.ProcessoHome;
import br.com.infox.epp.processo.localizacao.query.ProcessoLocalizacaoIbpmQuery;
import br.com.infox.ibpm.component.ControleFiltros;
import br.com.infox.ibpm.home.Authenticator;
import br.com.itx.util.EntityUtil;

@Name(ProcessoLocalizacaoIbpmDAO.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class ProcessoLocalizacaoIbpmDAO extends GenericDAO {
	private static final long serialVersionUID = 1L;
	public static final String NAME = "processoLocalizacaoIbpmDAO";

	public Localizacao listByTaskInstance(Long idTaskInstance) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(ProcessoLocalizacaoIbpmQuery.QUERY_PARAM_ID_TASK_INSTANCE, 
				idTaskInstance);
		return getNamedSingleResult(ProcessoLocalizacaoIbpmQuery.LIST_BY_TASK_INSTANCE, parameters);
	}
	
	public boolean possuiPermissao() {
		ControleFiltros.instance().iniciarFiltro();
		String hql = "select count(o) from ProcessoLocalizacaoIbpm o " +
						"where o.processo.idProcesso = :id" +
						" and o.localizacao = :localizacao" +
						" and o.papel = :papel";
		Query query = entityManager.createQuery(hql);
		query.setParameter("id", ProcessoHome.instance().getInstance().getIdProcesso());
		query.setParameter("localizacao", Authenticator.getLocalizacaoAtual());
		query.setParameter("papel", Authenticator.getPapelAtual());
		return (Long) EntityUtil.getSingleResult(query) > 0;
	}
	
	public Long getTaskInstanceId(UsuarioLocalizacao usrLoc, Processo processo, Long idTarefa) {
        Map<String,Object> parameters = new HashMap<String, Object>();
        parameters.put(ProcessoLocalizacaoIbpmQuery.QUERY_PARAM_PROCESSO,processo);
        parameters.put(ProcessoLocalizacaoIbpmQuery.QUERY_PARAM_LOCALIZACAO, usrLoc.getLocalizacao());
        parameters.put(ProcessoLocalizacaoIbpmQuery.QUERY_PARAM_PAPEL, usrLoc.getPapel());
        parameters.put(ProcessoLocalizacaoIbpmQuery.QUERY_PARAM_ID_TASK, idTarefa.intValue());
        return getNamedSingleResult(ProcessoLocalizacaoIbpmQuery.LIST_ID_TASK_INSTANCE_BY_ID_TAREFA, parameters);
    }
	
	public Long getTaskInstanceId(UsuarioLocalizacao usrLoc, Processo processo) {
		Map<String,Object> parameters = new HashMap<String, Object>();
        parameters.put(ProcessoLocalizacaoIbpmQuery.QUERY_PARAM_PROCESSO,processo);
        parameters.put(ProcessoLocalizacaoIbpmQuery.QUERY_PARAM_LOCALIZACAO, usrLoc.getLocalizacao());
        parameters.put(ProcessoLocalizacaoIbpmQuery.QUERY_PARAM_PAPEL, usrLoc.getPapel());
        
        return getNamedSingleResult(ProcessoLocalizacaoIbpmQuery.LIST_ID_TASK_INSTANCE_BY_LOCALIZACAO_PAPEL, parameters);
    }
	
	public void deleteProcessoLocalizacaoIbpmByTaskIdAndProcessId(Long taskId, Long processId){
        String hql = "delete from ProcessoLocalizacaoIbpm o " +
        		"where o.idProcessInstanceJbpm = :processId and o.idTaskJbpm = :taskId";
        entityManager.createQuery(hql)
                .setParameter("processId", processId)
                .setParameter("taskId", taskId).executeUpdate();
	}
	
}