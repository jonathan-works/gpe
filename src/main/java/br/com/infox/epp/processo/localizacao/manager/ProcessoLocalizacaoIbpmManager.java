package br.com.infox.epp.processo.localizacao.manager;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.processo.localizacao.dao.ProcessoLocalizacaoIbpmDAO;

@Name(ProcessoLocalizacaoIbpmManager.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class ProcessoLocalizacaoIbpmManager implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String NAME = "processoLocalizacaoIbpmManager";

    @In
    private ProcessoLocalizacaoIbpmDAO processoLocalizacaoIbpmDAO;

    public Localizacao listByTaskInstance(Long idTaskInstance) {
        return processoLocalizacaoIbpmDAO.listByTaskInstance(idTaskInstance);
    }

    public void deleteByTaskIdAndProcessId(Long taskId, Long processId) throws DAOException {
        processoLocalizacaoIbpmDAO.deleteProcessoLocalizacaoIbpmByTaskIdAndProcessId(taskId, processId);
    }

}
