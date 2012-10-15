package br.com.infox.access;

import java.lang.reflect.Field;
import java.util.List;

import javax.persistence.Query;

import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;
import org.jboss.seam.security.Identity;
import org.jboss.seam.util.Reflections;

import br.com.itx.component.grid.GridQuery;

/**
 * Classe que estende GridQuery, verificando as permissões de leitura de cada registro
 * 
 * @author luizruiz
 *
 */
public class SecurityGrid extends GridQuery {

	private static final long serialVersionUID = 1L;
	
	private Log log = Logging.getLog(SecurityGrid.class);

	/**
	 * Sobrescreve o método de GridQuery, buscando por todos os registros
	 * e depois filtrando-os pela permissão de leitura.
	 * Faz cache do resultado e atualiza o contador de registros.
	 * 
	 * IMPORTANTE: Trata apenas 50.000 registros, 
	 * 	mais que isso lança RuntimeException
	 * 
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public List getResultList() {
		List resultList = null;
		Field resultListField = null;
		try {
			resultListField = EntityQuery.class.getDeclaredField("resultList");
			resultList = (List) Reflections.get(resultListField, this);
			if (resultList != null) {
				return resultList;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		Query query = createCountQuery();
		resultCount = query==null ? null : (Long) query.getSingleResult();
		
		String msg = "Query retornou " + resultCount + " registros (limite = 50.000): " + getEjbql();
		if (resultCount > 50000) {
			log.error(msg);
			throw new RuntimeException(msg);
		} else if (resultCount > 40000) {
			log.warn(msg);
		} else {
			log.info(msg);
		}
		Integer maxResults = getMaxResults();
		if (maxResults == null) {
			maxResults = 16;
		}
		Integer firstResult = getFirstResult();
		setFirstResult(0);
		
		setMaxResults(null);
		resultList = super.getResultList();
		Identity.instance().filterByPermission(resultList, "read");
		resultCount = Long.valueOf(resultList.size());
		setMaxResults(maxResults);
		setFirstResult(firstResult);
		try {
			Reflections.setAndWrap(resultListField, this, resultList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return truncList(resultList);
	}

	@Override
	public Long getResultCount() {
		return resultCount;
	}
	
	/**
	 * Trunca a lista de acordo com a página a ser mostrada.
	 * Diferentemente do método sobrescrito, esse recebe todos os registros
	 * que passaram na validação.
	 */
	@SuppressWarnings("rawtypes")
	protected List truncList(List results) {
		Integer mr = getMaxResults();
		if (mr != null && results.size() > mr) {
			int from = getFirstResult();
			int to = from + mr;
			if (to >= results.size()) {
				to = results.size();
			}
			return results.subList(from, to);
		} 
		return results;
	}

}