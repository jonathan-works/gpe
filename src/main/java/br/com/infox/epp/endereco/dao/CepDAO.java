package br.com.infox.epp.endereco.dao;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.hibernate.NonUniqueResultException;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.endereco.entity.Cep;

@Name(CepDAO.NAME)
@AutoCreate
public class CepDAO extends GenericDAO {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "cepDAO";
    
    public Cep findCep(String numeroCep){
        String hql = "select o from Cep o where o.numeroCep =:searchCep";
        Query query = getEntityManager().createQuery(hql).setParameter("searchCep", numeroCep).setMaxResults(1);
        try {
            return (Cep) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException | IllegalArgumentException e)  {
            System.err.println(e.getMessage());
            return null;
        }
    }

}
