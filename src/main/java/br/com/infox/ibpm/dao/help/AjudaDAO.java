package br.com.infox.ibpm.dao.help;

import javax.persistence.Query;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.ajuda.entity.Ajuda;
import br.com.itx.util.EntityUtil;

@Name(AjudaDAO.NAME)
@AutoCreate
public class AjudaDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "ajudaDAO";
	
	public Ajuda getAjudaByPaginaUrl(String url){
		String hql = "select a from Ajuda a where a.pagina.url = :url order by a.dataRegistro desc";
		Query query = EntityUtil.createQuery(hql).setParameter("url", url);
		return EntityUtil.getSingleResult(query);
	}

}
