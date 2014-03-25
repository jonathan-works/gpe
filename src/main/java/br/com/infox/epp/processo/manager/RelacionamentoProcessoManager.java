package br.com.infox.epp.processo.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.dao.RelacionamentoProcessoDAO;
import br.com.infox.epp.processo.entity.RelacionamentoProcesso;

@AutoCreate
@Name(RelacionamentoProcessoManager.NAME)
public class RelacionamentoProcessoManager extends Manager<RelacionamentoProcessoDAO, RelacionamentoProcesso> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "relacionamentoProcessoManager";

    @In
    private ProcessoEpaManager processoEpaManager;

    public boolean existeRelacionamento(String processo1, String processo2) {
        return getDao().existeRelacionamento(processo1, processo2);
    }
    
    @Override
    public RelacionamentoProcesso persist(final RelacionamentoProcesso instance) throws DAOException {
        if (instance.getProcesso() == null) {
            instance.setProcesso(processoEpaManager.getProcessoEpaByNumeroProcesso(instance.getNumeroProcesso()));
        }
        return super.persist(instance);
    }

}
