package br.com.infox.epp.processo.situacao.manager;

import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.processo.situacao.dao.SituacaoProcessoDAO;
import br.com.infox.epp.processo.situacao.entity.SituacaoProcesso;
import br.com.infox.epp.processo.type.TipoProcesso;

@AutoCreate
@Name(SituacaoProcessoManager.NAME)
public class SituacaoProcessoManager extends Manager<SituacaoProcessoDAO, SituacaoProcesso> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "situacaoProcessoManager";

    public boolean existemTarefasEmAberto(long taskId) {
        return getDao().getQuantidadeTarefasAtivasByTaskId(taskId) > 0;
    }

    public List<Integer> getProcessosAbertosByIdTarefa(Integer idTarefa, Map<String, Object> selected, TipoProcesso tipoProcesso) {
    	if (tipoProcesso == TipoProcesso.COMUNICACAO || tipoProcesso == TipoProcesso.DOCUMENTO){
    		return getDao().getProcessosAbertosByIdTarefaAndTipoProcesso(idTarefa, selected, tipoProcesso);
    	} else {
    		return getDao().getQueryProcessoAbertosByIdTarefa(idTarefa, selected);
    	}
    }

    public boolean canOpenTask(long currentTaskId) {
        return getDao().canOpenTask(currentTaskId);
    }
    
    public <E> List<E> getChildrenTarefas(TipoProcesso tipoProcesso, Integer idFluxo) {
    	if (tipoProcesso == TipoProcesso.COMUNICACAO || tipoProcesso == TipoProcesso.DOCUMENTO) {
    		return getDao().getChildrenComunicacaoDocumentoList(idFluxo);
    	} else {
    		return getDao().getChildrenList(idFluxo);
    	}
    }
    
	public <E> List<E> getRootsFluxos(TipoProcesso tipoProcesso) {
		if (tipoProcesso == TipoProcesso.COMUNICACAO || tipoProcesso == TipoProcesso.DOCUMENTO) {
			return getDao().getRootList(tipoProcesso);
		} else {
			return getDao().getRootList();
		}
    }
	
	public Query createQueryCaixas(TipoProcesso tipoProcesso) {
		if (tipoProcesso == TipoProcesso.DOCUMENTO || tipoProcesso == TipoProcesso.COMUNICACAO) {
			return getDao().createQueryCaixas(tipoProcesso);
		} else {
			return getDao().createQueryCaixas();
		}
	}

}
