package br.com.infox.epp.processo.manager;

import java.lang.reflect.InvocationTargetException;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.EntityUtil;
import br.com.infox.epp.processo.dao.RelacionamentoDAO;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.Relacionamento;

@AutoCreate
@Name(RelacionamentoManager.NAME)
@Stateless
public class RelacionamentoManager extends Manager<RelacionamentoDAO, Relacionamento> {

    public static final String NAME = "relacionamentoManager";
    private static final long serialVersionUID = 1L;
    
    @Inject
    private RelacionamentoDAO relacionamentoDAO;
    
    @Inject
    private ProcessoManager processoManager;

    public Relacionamento getRelacionamentoByProcesso(Processo processo) {
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
    
	public void remove(Integer idProcesso, Integer idProcessoInternoRelacionado) {
		Processo processo = processoManager.find(idProcesso);
		Processo processoInternoRelacionado = processoManager.find(idProcessoInternoRelacionado);
		relacionamentoDAO.remove(processo, processoInternoRelacionado);
	}

	public void remove(Integer idProcesso, String numeroProcessoExterno) {
		Processo processo = processoManager.find(idProcesso);
		relacionamentoDAO.remove(processo, numeroProcessoExterno);
	}
    
}
