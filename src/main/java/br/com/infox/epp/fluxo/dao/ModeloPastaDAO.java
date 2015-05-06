package br.com.infox.epp.fluxo.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.ModeloPasta;

@AutoCreate
@Name(ModeloPastaDAO.NAME)
public class ModeloPastaDAO extends DAO<ModeloPasta>{
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "modeloPastaDao";

	public List<ModeloPasta> getByFluxo(Fluxo fluxo) {
		Map<String, Object> parameters = new HashMap<>();
		// TODO Criar DAO
		return null;
	}

}
