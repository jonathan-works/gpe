package br.com.infox.epp.fluxo.manager;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.fluxo.dao.NaturezaDAO;
import br.com.infox.epp.fluxo.entity.Natureza;

@Stateless
@AutoCreate
@Name(NaturezaManager.NAME)
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class NaturezaManager extends Manager<NaturezaDAO, Natureza> {

    private static final long serialVersionUID = 2649821908249070536L;

    public static final String NAME = "naturezaManager";
    
    public List<Natureza> getNaturezasPrimarias(){
    	return getDao().findNaturezasPrimarias();
    }

    public void lockNatureza(Natureza natureza) throws DAOException {
        if (!natureza.getLocked()) {
            natureza.setLocked(true);
            update(natureza);
        }
    }
}
