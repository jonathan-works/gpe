package br.com.infox.epp.processo.partes.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.processo.partes.entity.TipoParte;
import br.com.infox.epp.processo.partes.query.TipoParteQuery;

@Stateless
@Name(TipoParteDAO.NAME)
public class TipoParteDAO extends DAO<TipoParte> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "tipoParteDAO";
	
	@Override
	public List<TipoParte> findAll() {
		String hql = "from TipoParte order by descricao";
		return getResultList(hql, null);
	}
	
	public TipoParte getTipoParteByIdentificador(String identificador){
		Map<String, Object> params = new HashMap<>();
		params.put(TipoParteQuery.PARAM_IDENTIFICADOR, identificador);
		return getNamedSingleResult(TipoParteQuery.TIPO_PARTE_BY_IDENTIFICADOR, params);
	}

}
