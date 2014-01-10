package br.com.infox.epp.fluxo.crud;

import java.util.Collections;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

import br.com.infox.core.action.AbstractAction;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.fluxo.entity.DefinicaoVariavelProcesso;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.manager.DefinicaoVariavelProcessoManager;

@Name(DefinicaoVariavelProcessoAction.NAME)
@Scope(ScopeType.PAGE)
public class DefinicaoVariavelProcessoAction extends AbstractAction<DefinicaoVariavelProcesso> {
    
    public static final String NAME = "definicaoVariavelProcessoAction";
    private static final Log LOG = Logging.getLog(DefinicaoVariavelProcessoAction.class);
    
    private Fluxo fluxo;
    private DefinicaoVariavelProcesso variavel;
    private List<DefinicaoVariavelProcesso> variaveis;
    private int page = 1;
    private int maxPages;
    
    @In
    private DefinicaoVariavelProcessoManager definicaoVariavelProcessoManager;
    
    public void setFluxo(Fluxo fluxo) {
		this.fluxo = fluxo;
	}
    
    public void adicionarVariavel() {
    	this.variavel = new DefinicaoVariavelProcesso();
    }
    
    public void save() {
    	String ret;
    	variavel.setFluxo(fluxo);
    	if (isPersisted()) {
    		ret = update(variavel);
    	} else {
    		ret = persist(variavel);
    	}
    	if (AbstractAction.PERSISTED.equals(ret)) {
    		this.variaveis = null;
    		this.variavel = null;
    		FacesMessages.instance().add("#{messages['DefinicaoVariavelProcesso_created']}");
    	} else if (AbstractAction.UPDATED.equals(ret)) {
    		this.variaveis = null;
    		this.variavel = null;
    		FacesMessages.instance().add("#{messages['DefinicaoVariavelProcesso_updated']}");
    	}
	}
    
    public boolean isPersisted() {
    	if (variavel != null && variavel.getId() != null) {
    		if (!getGenericManager().contains(variavel)) {
    			try {
					getGenericManager().merge(variavel);
				} catch (DAOException e) {
					LOG.error(".isPersisted()", e);
				}
    		}
    		return getGenericManager().contains(variavel);
    	}
    	return false;
    }
    
    @Override
    public String remove(DefinicaoVariavelProcesso obj) {
    	String ret;
        try {
            ret = super.remove(getGenericManager().merge(obj));
        } catch (DAOException e) {
            LOG.error("Não foi possível remover a DefinicaoVariavelProcesso " + obj.getNome(), e);
            ret = "UNMERGED";
        }
    	if (AbstractAction.REMOVED.equals(ret)) {
    		this.variaveis = null;
    		FacesMessages.instance().add("#{messages['DefinicaoVariavelProcesso_deleted']}");
    		if (obj.equals(this.variavel)) {
    			this.variavel = null;
    		}
    	}
    	return ret;
    }
    
    public List<DefinicaoVariavelProcesso> listVariaveis(int maxResults) {
    	if (this.variaveis == null) {
    		int total = definicaoVariavelProcessoManager.getTotalVariaveisByFluxo(fluxo).intValue();
    		calcMaxPages(maxResults, total);
    		int start = calcStart(maxResults);
    		this.variaveis = definicaoVariavelProcessoManager.listVariaveisByFluxo(fluxo, start, maxResults);
    	}
    	return Collections.unmodifiableList(this.variaveis);
	}

	private int calcStart(int maxResults) {
		return (page - 1) * maxResults;
	}

	private void calcMaxPages(int maxResults, int total) {
		maxPages = total / maxResults;
		if (total % maxResults != 0) {
			maxPages++;
		}
	}
    
    public DefinicaoVariavelProcesso getVariavel() {
		return variavel;
	}
    
    public void setVariavel(DefinicaoVariavelProcesso variavel) {
		this.variavel = variavel;
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
		this.variaveis = null;
	}
    
    public int getMaxPages() {
		return maxPages;
	}
    
    public void setMaxPages(int maxPages) {
		this.maxPages = maxPages;
	}
    
    public String getNomeAmigavel(DefinicaoVariavelProcesso variavel) {
    	return definicaoVariavelProcessoManager.getNomeAmigavel(variavel);
    }
    
    public String getNomeAmigavel() {
    	return getNomeAmigavel(variavel);
    }
    
    public void setNomeAmigavel(String nomeAmigavel) {
    	definicaoVariavelProcessoManager.setNome(variavel, nomeAmigavel);
    }
}
