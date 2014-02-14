package br.com.infox.epp.processo.sigilo.dao;

import static br.com.infox.epp.processo.sigilo.query.SigiloProcessoQuery.NAMED_QUERY_SIGILO_PROCESSO_ATIVO;
import static br.com.infox.epp.processo.sigilo.query.SigiloProcessoQuery.QUERY_PARAM_PROCESSO;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.infox.epp.processo.sigilo.entity.SigiloProcesso;

@Name(SigiloProcessoDAO.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class SigiloProcessoDAO extends DAO<SigiloProcesso> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "sigiloProcessoDAO";
	
	public SigiloProcesso getSigiloProcessoAtivo(ProcessoEpa processoEpa) {
		Map<String, Object> params = new HashMap<>();
		params.put(QUERY_PARAM_PROCESSO, processoEpa);
		return getNamedSingleResult(NAMED_QUERY_SIGILO_PROCESSO_ATIVO, params);
	}
}
