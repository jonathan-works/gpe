package br.com.infox.epp.ajuda.dao;

import javax.persistence.Query;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.ajuda.entity.Pagina;
import br.com.itx.util.EntityUtil;

@Name(PaginaDAO.NAME)
@AutoCreate
public class PaginaDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "paginaDAO";
	
	public Pagina getPaginaByUrl(String url){
		String hql = "select o from Pagina o where o.url = :url";
		Query query = EntityUtil.createQuery(hql).setParameter("url", url);
		return EntityUtil.getSingleResult(query);
	}

}
