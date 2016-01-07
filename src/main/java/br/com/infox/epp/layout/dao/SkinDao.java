package br.com.infox.epp.layout.dao;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.TypedQuery;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.epp.layout.entity.Skin;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class SkinDao extends Dao<Skin, Integer> {

	public SkinDao() {
		super(Skin.class);
	}
	
    public Skin getSkinPadrao() {
        String query = "from Skin where padrao = true";
        return getSingleResult(getEntityManager().createQuery(query, Skin.class));
    }
    
    public Skin findByCodigo(String codigo) {
        String jpql = "from Skin where codigo = :codigo";
        TypedQuery<Skin> query = getEntityManager().createQuery(jpql, Skin.class);
        query.setParameter("codigo", codigo);
        return getSingleResult(query);
    }
	

}
