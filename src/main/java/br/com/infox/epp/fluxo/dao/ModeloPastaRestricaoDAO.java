package br.com.infox.epp.fluxo.dao;

import static br.com.infox.epp.fluxo.query.ModeloPastaRestricaoQuery.DELETE_BY_MODELO_PASTA;
import static br.com.infox.epp.fluxo.query.ModeloPastaRestricaoQuery.GET_BY_MODELO_PASTA;
import static br.com.infox.epp.fluxo.query.ModeloPastaRestricaoQuery.PARAM_MODELO_PASTA;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.fluxo.entity.ModeloPasta;
import br.com.infox.epp.fluxo.entity.ModeloPastaRestricao;

@Stateless
@AutoCreate
@Name(ModeloPastaRestricaoDAO.NAME)
public class ModeloPastaRestricaoDAO extends DAO<ModeloPastaRestricao>{
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "modeloPastaRestricaoDAO";
	
	public List<ModeloPastaRestricao> getByModeloPasta(ModeloPasta modelo){
		Map<String, Object> params = new HashMap<>();
        params.put(PARAM_MODELO_PASTA, modelo);
        return getNamedResultList(GET_BY_MODELO_PASTA, params);
	}
	
	public void deleteByModeloPasta(ModeloPasta modelo) throws DAOException{
		Map<String, Object> params = new HashMap<>();
        params.put(PARAM_MODELO_PASTA, modelo);
        executeNamedQueryUpdate(DELETE_BY_MODELO_PASTA, params);
	}

}
