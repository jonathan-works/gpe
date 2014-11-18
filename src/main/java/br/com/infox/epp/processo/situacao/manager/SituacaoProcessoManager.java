package br.com.infox.epp.processo.situacao.manager;

import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.processo.situacao.dao.SituacaoProcessoDAO;
import br.com.infox.epp.processo.situacao.entity.SituacaoProcesso;

@AutoCreate
@Name(SituacaoProcessoManager.NAME)
public class SituacaoProcessoManager extends Manager<SituacaoProcessoDAO, SituacaoProcesso> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "situacaoProcessoManager";

    public boolean existemTarefasEmAberto(long taskId) {
        return getDao().getQuantidadeTarefasAtivasByTaskId(taskId) > 0;
    }

    public List<Integer> getProcessosAbertosByIdTarefa(Integer idTarefa, Map<String, Object> selected) {
        return getDao().getProcessosAbertosByIdTarefa(idTarefa, selected);
    }

    public boolean canOpenTask(long currentTaskId) {
        return getDao().canOpenTask(currentTaskId);
    }
    
    public <E> List<E> getChildrenTarefas(String tipoProcesso, Integer idFluxo) {
    	if (tipoProcesso.equals("processo")) {
    		return getDao().getChildrenList(idFluxo);
    	} 
//    	else if (tipoProcesso.equals("processoComunicacao")) {
//    		return 
//    	} else if (tipoProcesso.equals("processoDocumento")) {
//    		return 
//    	} else {
//    		return null;
//    	}
    	return null;
    }
    
    public <E> List<E> getRootsFluxos(String tipoProcesso) {
    	if (tipoProcesso.equals("processo")) {
    		return getDao().getRootList();
    	} 
//    	else if (tipoProcesso.equals("processoComunicacao")) {
//    		return 
//    	} else if (tipoProcesso.equals("processoDocumento")) {
//    		return 
//    	} else {
//    		return null;
//    	}
    	return null;
    }

}
