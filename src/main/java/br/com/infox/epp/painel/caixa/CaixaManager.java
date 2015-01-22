package br.com.infox.epp.painel.caixa;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;

@AutoCreate
@Name(CaixaManager.NAME)
public class CaixaManager extends Manager<CaixaDAO, Caixa> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "caixaManager";
    
    @In
    private ProcessoManager processoManager;

    @Override
    public Caixa remove(Caixa caixa) throws DAOException {
    	List<Processo> processoList = processoManager.getProcessosByIdCaixa(caixa.getIdCaixa());
    	for(Processo processo : processoList) {
    		processo.setCaixa(null);
    	}
    	return super.remove(caixa);
    }
    
    public void moverProcessoParaCaixa(Processo processo, Caixa caixa) throws DAOException {
    	processo.setCaixa(caixa);
    	processoManager.update(processo);
    }
    
    public void moverProcessosParaCaixa(List<Processo> processos, Caixa caixa) throws DAOException {
    	for (Processo processo : processos) {
    		processo.setCaixa(caixa);
    		processoManager.merge(processo);
    	}
    	flush();
    }
    
    public Caixa getCaixaByIdTarefaAndIdNodeAnterior(Integer idTarefa, Integer idNodeAnterior) {
    	return getDao().getCaixaByIdTarefaAndIdNodeAnterior(idTarefa, idNodeAnterior);
    }

}
