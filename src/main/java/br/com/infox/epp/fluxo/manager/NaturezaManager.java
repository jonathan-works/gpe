package br.com.infox.epp.fluxo.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.fluxo.dao.NaturezaDAO;
import br.com.infox.epp.fluxo.entity.Natureza;

@Name(NaturezaManager.NAME)
@AutoCreate
public class NaturezaManager extends Manager<NaturezaDAO, Natureza> {

    private static final long serialVersionUID = 2649821908249070536L;

    public static final String NAME = "naturezaManager";
    
    public void lockNatureza(Natureza natureza) throws DAOException{
        if (!natureza.getLocked()) {
            natureza.setLocked(true);
            update(natureza);
        }
    }
}
