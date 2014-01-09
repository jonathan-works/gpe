package br.com.infox.epp.system.dao;

import static br.com.infox.epp.system.query.ParametroQuery.LIST_PARAMETROS_ATIVOS;
import static br.com.infox.epp.system.query.ParametroQuery.MAP_PARAMETRO_TRIGGERS;
import static br.com.infox.epp.system.query.ParametroQuery.TRIGGER_NAMES_PARAM;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public List<Parametro> listParametrosAtivos() {
        return getNamedResultList(LIST_PARAMETROS_ATIVOS);
    }
    
    public List<Map<String, Object>> getMapParametroTriggers(List<String> triggersNames){
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(TRIGGER_NAMES_PARAM, triggersNames);
        return getResultList(MAP_PARAMETRO_TRIGGERS, parameters);
    }

}
