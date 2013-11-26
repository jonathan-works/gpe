package br.com.infox.epp.fluxo.crud;

import java.util.Collections;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

import br.com.infox.core.action.AbstractAction;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.VariavelProcesso;
import br.com.infox.epp.fluxo.manager.VariavelProcessoManager;
import br.com.itx.util.EntityUtil;

@Name(VariavelProcessoAction.NAME)
@Scope(ScopeType.PAGE)
public class VariavelProcessoAction extends AbstractAction {
    
    public static final String NAME = "variavelProcessoAction";
    
    private Fluxo fluxo;
    private VariavelProcesso variavelProcesso;
    private List<VariavelProcesso> variaveisProcesso;
    private int page = 1;
    private int maxPages;
    
    @In
    private VariavelProcessoManager variavelProcessoManager;
    
    public void setFluxo(Fluxo fluxo) {
		this.fluxo = fluxo;
	}
    
    public void adicionarVariavelProcesso() {
    	this.variavelProcesso = new VariavelProcesso();
    }
    
    public void save() {
    	String ret;
    	variavelProcesso.setFluxo(fluxo);
    	variavelProcesso = EntityUtil.getEntityManager().merge(variavelProcesso);
    	if (isPersisted()) {
    		ret = update(variavelProcesso);
    	} else {
    		ret = persist(variavelProcesso);
    	}
    	if (AbstractAction.PERSISTED.equals(ret)) {
    		this.variaveisProcesso = null;
    		this.variavelProcesso = null;
    		FacesMessages.instance().add("#{messages['VariavelProcesso_created']}");
    	} else if (AbstractAction.UPDATED.equals(ret)) {
    		this.variaveisProcesso = null;
    		this.variavelProcesso = null;
    		FacesMessages.instance().add("#{messages['VariavelProcesso_updated']}");
    	}
	}
    
    private boolean isPersisted() {
    	return variavelProcesso != null && variavelProcesso.getIdVariavelProcesso() != null && getGenericManager().contains(variavelProcesso);
    }
    
    @Override
    public String remove(Object obj) {
    	String ret = super.remove(EntityUtil.getEntityManager().merge(obj));
    	if (AbstractAction.REMOVED.equals(ret)) {
    		this.variaveisProcesso = null;
    		FacesMessages.instance().add("#{messages['VariavelProcesso_deleted']}");
    		if (obj.equals(this.variavelProcesso)) {
    			this.variavelProcesso = null;
    		}
    	}
    	return ret;
    }
    
    public List<VariavelProcesso> listVariaveisProcesso(int maxResults) {
    	if (this.variaveisProcesso == null) {
    		int total = variavelProcessoManager.getTotalVariaveisProcessoByFluxo(fluxo).intValue();
    		maxPages = total / maxResults;
    		if (total % maxResults != 0) {
    			maxPages++;
    		}
    		int start = (page - 1) * maxResults;
    		this.variaveisProcesso = variavelProcessoManager.listVariaveisProcessoByFluxo(fluxo, start, maxResults);
    	}
    	return Collections.unmodifiableList(this.variaveisProcesso);
	}
    
    public VariavelProcesso getVariavelProcesso() {
		return variavelProcesso;
	}
    
    public void setVariavelProcesso(VariavelProcesso variavelProcesso) {
		this.variavelProcesso = variavelProcesso;
	}
    
    public int getPage() {
		return page;
	}
    
    public void setPage(int page) {
    	if (page > maxPages) {
			page = maxPages;
		} else {
			this.page = page;
		}
		this.variaveisProcesso = null;
	}
    
    public int getMaxPages() {
		return maxPages;
	}
    
    public void setMaxPages(int maxPages) {
		this.maxPages = maxPages;
	}
}
