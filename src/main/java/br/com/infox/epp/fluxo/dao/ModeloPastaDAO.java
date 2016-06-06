package br.com.infox.epp.fluxo.dao;

import static br.com.infox.epp.fluxo.query.ModeloPastaQuery.GET_BY_FLUXO;
import static br.com.infox.epp.fluxo.query.ModeloPastaQuery.PARAM_FLUXO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.ModeloPasta;

@Stateless
@AutoCreate
@Name(ModeloPastaDAO.NAME)
public class ModeloPastaDAO extends DAO<ModeloPasta>{
	
    private static final long serialVersionUID = 1L;
	public static final String NAME = "modeloPastaDao";

	public List<ModeloPasta> getByFluxo(Fluxo fluxo) {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put(PARAM_FLUXO, fluxo);
		return getNamedResultList(GET_BY_FLUXO, parameters);
	}
}
