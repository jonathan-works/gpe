package br.com.infox.ibpm.dao;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.ibpm.entity.Parametro;

@Name(ParametroDAO.NAME)
@AutoCreate
public class ParametroDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "parametroDAO";
	
	public Parametro getParametroByNomeVariavel(String nomeVariavel){
		String hql = "select p from Parametro p where nomeVariavel = :nome";
		return (Parametro) entityManager.createQuery(hql).setParameter("nomeVariavel", nomeVariavel).getSingleResult();
	}

}
