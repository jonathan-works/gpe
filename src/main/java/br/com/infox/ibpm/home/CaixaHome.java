package br.com.infox.ibpm.home;

import java.text.MessageFormat;
import java.util.List;

import javax.faces.model.SelectItem;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import br.com.infox.ibpm.component.tree.TarefasTreeHandler;
import br.com.infox.ibpm.entity.Caixa;
import br.com.infox.ibpm.entity.Tarefa;
import br.com.infox.ibpm.jbpm.actions.JbpmEventsHandler;
import br.com.infox.ibpm.manager.CaixaManager;
import br.com.infox.ibpm.manager.TarefaManager;
import br.com.infox.util.PostgreSQLErrorCode;
import br.com.itx.component.AbstractHome;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.EntityUtil;

@Name(CaixaHome.NAME)
public class CaixaHome extends AbstractHome<Caixa> {
    private static final long serialVersionUID = 1L;
    
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
    public String remove(Caixa obj) {
        String ret = super.remove(obj);
        newInstance();
        return ret;
    }
    
    @Override
    public String remove() {
    	String ret = super.remove();
    	TarefasTreeHandler.clearActiveTree();
    	return ret;
    }
}