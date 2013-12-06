package br.com.infox.epp.painel.caixa;

import java.text.MessageFormat;
import java.util.List;

import javax.faces.model.SelectItem;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.persistence.PostgreSQLErrorCode;
import br.com.infox.epp.tarefa.component.tree.TarefasTreeHandler;
import br.com.infox.epp.tarefa.entity.Tarefa;
import br.com.infox.epp.tarefa.manager.TarefaManager;
import br.com.infox.ibpm.event.JbpmEventsHandler;
import br.com.itx.component.AbstractHome;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.EntityUtil;

@Name(CaixaHome.NAME)
public class CaixaHome extends AbstractHome<Caixa> {
    private static final long serialVersionUID = 1L;
    private static final LogProvider LOG = Logging.getLogProvider(CaixaHome.class);
    
	public static final String NAME = "caixaHome";
	public static final String ADD_CAIXA_EVENT = "addCaixaEvent";
	
	@In private TarefaManager tarefaManager;
	@In private CaixaManager caixaManager;
	
	public List<SelectItem> getPreviousNodes() {
		return getPreviousNodes(instance.getTarefa());
	}
	
	public List<SelectItem> getPreviousNodes(Integer idTarefa) {
		return tarefaManager.getPreviousNodes(EntityUtil.find(Tarefa.class, idTarefa));
	}
	
    public List<SelectItem> getPreviousNodes(Tarefa tarefa) {
		return tarefaManager.getPreviousNodes(tarefa);
	}

	@Override
	protected boolean beforePersistOrUpdate() {
	    if (instance.getTarefa() == null) {
	        return false;
	    }
	    return true;
	}
	
	public void addCaixa(int idTarefa) {
		instance.setTarefa(EntityUtil.find(Tarefa.class, idTarefa));
		instance.setNomeIndice(MessageFormat.format("{0}-{1}", instance.getNomeCaixa(), idTarefa));
		persist();
		
		newInstance();
	}
	
	@Override
	protected String afterPersistOrUpdate(String ret) {
	    if (AbstractHome.PERSISTED.equals(ret)) {
	        JbpmEventsHandler.updatePostDeploy();
	        TarefasTreeHandler.clearActiveTree();
	    }
	    return super.afterPersistOrUpdate(ret);
	}
	
	@Override
	public String update() {
		String ret = super.update();
		try {
			if (PostgreSQLErrorCode.valueOf(ret) == PostgreSQLErrorCode.UNIQUE_VIOLATION) {
		    	FacesMessages.instance().clear();
		    	FacesMessages.instance().add(Severity.ERROR, "Já existe uma caixa na mesma tarefa com o nó anterior especificado.");
		    }
		} catch (IllegalArgumentException e) {
			LOG.warn(".update()", e);
			// Retorno do update não pertence ao enum, nada a fazer
		}
		return ret;
	}
	
	public static CaixaHome instance() {
		return ComponentUtil.getComponent(NAME);
	}
	
    @Override
    protected Caixa createInstance() {
        return new Caixa();
    }
    
    @Override
    public String remove() {
        caixaManager.removeCaixaByIdCaixa(instance.getIdCaixa());
    	String ret = super.remove();
    	TarefasTreeHandler.clearActiveTree();
    	return ret;
    }
    
    public void setCaixaIdCaixa(Integer id) {
        setId(id);
    }

    public Integer getCaixaIdCaixa() {
        return (Integer) getId();
    }
    
    public void removeCaixa(int idCaixa) {
        if(idCaixa == 0) {
            return;
        }
        instance = EntityUtil.find(Caixa.class, idCaixa);
        if(instance != null){
            remove();
        }else{
            FacesMessages.instance().add(Severity.ERROR, "Por favor, selecione a caixa que deseja excluir!");
        }
        TarefasTreeHandler tree = ComponentUtil.getComponent("tarefasTree");
        tree.clearTree();   
    }
}