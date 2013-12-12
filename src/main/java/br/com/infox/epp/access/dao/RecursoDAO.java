package br.com.infox.epp.access.dao;

import javax.persistence.Query;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;

@Name(RecursoDAO.NAME)
@AutoCreate
public class RecursoDAO extends GenericDAO {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "recursoDAO";
    
    public boolean existsRecurso(String identificador){
        String hql = "select count(o) from Recurso o where o.identificador = :identificador";
        Query query = getEntityManager().createQuery(hql).setParameter("identificador", identificador);
        return ((Long) query.getSingleResult()) > 0;
    }

}
