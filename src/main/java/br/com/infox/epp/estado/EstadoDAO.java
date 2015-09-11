package br.com.infox.epp.estado;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;

@AutoCreate
@Name(EstadoDAO.NAME)
public class EstadoDAO extends DAO<Estado>{
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "estadoDAO";
	
	@Factory(scope=ScopeType.APPLICATION)
	public List<Estado> getEstados(){
		return findAll();
	}
	
	public Estado getBySigla(String sigla) {
		Map<String, Object> params = new HashMap<>();
		params.put(EstadoQuery.PARAM_SIGLA, sigla);
    	return getNamedSingleResult(EstadoQuery.ESTADO_BY_SIGLA, params);
	}
	
	@Override
	public List<Estado> findAll() {
		String hql = "select o from Estado o order by o.nome";
		return getResultList(hql, null);
	}
	 
}
