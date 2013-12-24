package br.com.infox.epp.ajuda.dao;

import static br.com.infox.epp.ajuda.query.AjudaQuery.AJUDA_BY_URL;
import static br.com.infox.epp.ajuda.query.AjudaQuery.PARAM_URL;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.ajuda.entity.Ajuda;

@Name(AjudaDAO.NAME)
@AutoCreate
public class AjudaDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "ajudaDAO";
	
	public Ajuda getAjudaByPaginaUrl(String url){
	    Map<String, Object> parameters = new HashMap<>();
	    parameters.put(PARAM_URL, url);
	    return getNamedSingleResult(AJUDA_BY_URL, parameters);
	}

}
