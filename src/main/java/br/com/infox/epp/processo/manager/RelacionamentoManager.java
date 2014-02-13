package br.com.infox.epp.processo.manager;

import java.lang.reflect.InvocationTargetException;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.dao.RelacionamentoDAO;
import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.infox.epp.processo.entity.Relacionamento;
import br.com.itx.util.EntityUtil;

@Name(RelacionamentoManager.NAME)
public class RelacionamentoManager extends Manager<RelacionamentoDAO, Relacionamento> {

    public static final String NAME = "relacionamentoManager";
    private static final long serialVersionUID = 1L;
    
    public Relacionamento getRelacionamentoByProcesso(final ProcessoEpa processo) {
        Relacionamento relacionamento = null;
        if (processo != null) {
            relacionamento = getDao().getRelacionamentoByProcesso(processo);
        }
        return relacionamento;
    }
    
    @Override
    public Relacionamento persist(final Relacionamento o) throws DAOException {
        final RelacionamentoDAO relacionamentoDAO = getDao();
        relacionamentoDAO.persist(o);
        try {
            return relacionamentoDAO.find(EntityUtil.getId(o).getReadMethod().invoke(o));
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new DAOException(e);
        }
    }
}
