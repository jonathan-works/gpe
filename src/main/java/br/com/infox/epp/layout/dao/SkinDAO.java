package br.com.infox.epp.layout.dao;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.epp.layout.entity.Skin;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class SkinDAO extends Dao<Skin, Integer> {

	public SkinDAO() {
		super(Skin.class);
	}
	
    public Skin getSkinPadrao() {
        String query = "from Skin where padrao = true";
        return getSingleResult(getEntityManager().createQuery(query, Skin.class));
    }
	

}
