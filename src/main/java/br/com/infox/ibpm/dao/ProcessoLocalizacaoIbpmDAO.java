package br.com.infox.ibpm.dao;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.access.entity.Papel;
import br.com.infox.core.dao.GenericDAO;
import br.com.infox.ibpm.component.ControleFiltros;
import br.com.infox.ibpm.entity.Localizacao;
import br.com.infox.ibpm.home.Authenticator;
import br.com.infox.ibpm.home.ProcessoHome;
import br.com.infox.ibpm.query.ProcessoLocalizacaoIbpmQuery;
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
		String hql = "select 1 from ProcessoLocalizacaoIbpm o " +
						"where o.processo.idProcesso = :id" +
						" and o.localizacao = :localizacao" +
						" and o.papel = :papel";
		Query query = entityManager.createQuery(hql);
		query.setParameter("id", ProcessoHome.instance().getInstance().getIdProcesso());
		query.setParameter("localizacao", Authenticator.getLocalizacaoAtual());
		query.setParameter("papel", Authenticator.getPapelAtual());
		Object result = EntityUtil.getSingleResult(query);
		return result != null;
	}
}