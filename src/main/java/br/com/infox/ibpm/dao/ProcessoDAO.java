package br.com.infox.ibpm.dao;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;
import br.com.itx.util.HibernateUtil;

@Name(ProcessoDAO.NAME)
@AutoCreate
public class ProcessoDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "processoDAO";
	
	public void anulaActorId(String actorId) {
		String query = "update public.tb_processo set nm_actor_id = null where nm_actor_id = :actorId";
		HibernateUtil.getSession().createSQLQuery(query).setParameter("actorId", actorId).executeUpdate();
	}
	
	public void anularTodosActorId() {
		String query = "update public.tb_processo set nm_actor_id = null ";
		HibernateUtil.getSession().createSQLQuery(query).executeUpdate();
	}

}
