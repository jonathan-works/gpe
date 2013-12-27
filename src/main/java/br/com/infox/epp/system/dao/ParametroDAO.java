package br.com.infox.epp.system.dao;

import java.util.HashMap;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.system.entity.Parametro;

@Name(ParametroDAO.NAME)
@AutoCreate
public class ParametroDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "parametroDAO";
	
	public Parametro getParametroByNomeVariavel(String nomeVariavel){
		final String hql = "select p from Parametro p where nomeVariavel = :nomeVariavel";
		final HashMap<String, Object> parameters = new HashMap<>();
		parameters.put("nomeVariavel", nomeVariavel);
		return getSingleResult(hql, parameters);
	}

}
