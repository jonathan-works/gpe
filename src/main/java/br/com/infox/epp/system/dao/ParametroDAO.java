package br.com.infox.epp.system.dao;

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
		String hql = "select p from Parametro p where nomeVariavel = :nomeVariavel";
		return (Parametro) getEntityManager().createQuery(hql).setParameter("nomeVariavel", nomeVariavel).getSingleResult();
	}

}
