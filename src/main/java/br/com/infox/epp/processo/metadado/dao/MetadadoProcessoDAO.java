package br.com.infox.epp.processo.metadado.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.query.MetadadoProcessoQuery;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoDefinition;

@AutoCreate
@Name(MetadadoProcessoDAO.NAME)
public class MetadadoProcessoDAO extends DAO<MetadadoProcesso> {
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "metadadoProcessoDAO";
	
	public List<MetadadoProcesso> getListMetadadoVisivelByProcesso(Processo processo) {
		Map<String, Object> params = new HashMap<>(1);
		params.put(MetadadoProcessoQuery.PARAM_PROCESSO, processo);
		return getNamedResultList(MetadadoProcessoQuery.LIST_METADADO_PROCESSO_VISIVEL_BY_PROCESSO, params);
	}

	public MetadadoProcesso getMetadado(MetadadoProcessoDefinition definition, Processo processo) {
		Map<String, Object> params = new HashMap<>();
		params.put(MetadadoProcessoQuery.PARAM_PROCESSO, processo);
		params.put(MetadadoProcessoQuery.PARAM_METADADO_TYPE, definition.getMetadadoType());
		return getNamedSingleResult(MetadadoProcessoQuery.GET_METADADO, params);
	}

	public void removerMetadado(MetadadoProcessoDefinition definition, Processo processo) throws DAOException {
		Map<String, Object> params = new HashMap<>();
		params.put(MetadadoProcessoQuery.PARAM_METADADO_TYPE, definition.getMetadadoType());
		params.put(MetadadoProcessoQuery.PARAM_PROCESSO, processo);
		executeNamedQueryUpdate(MetadadoProcessoQuery.REMOVER_METADADO, params);
	}
}
