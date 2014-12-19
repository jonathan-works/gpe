package br.com.infox.epp.processo.metadado.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.query.MetadadoProcessoQuery;

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
	
}
