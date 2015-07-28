package br.com.infox.epp.fluxo.crud;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.faces.FacesMessages;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.dao.DefinicaoVariavelProcessoDAO;
import br.com.infox.epp.fluxo.entity.DefinicaoVariavelProcesso;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.manager.DefinicaoVariavelProcessoManager;
import br.com.infox.log.Log;
import br.com.infox.log.Logging;
import br.com.infox.seam.util.ComponentUtil;

@Named
@ViewScoped
public class DefinicaoVariavelProcessoAction implements Serializable {

	private static final long serialVersionUID = 1L;
    private static final Log LOG = Logging.getLog(DefinicaoVariavelProcessoAction.class);

    @Inject
    private DefinicaoVariavelProcessoDAO definicaoVariavelProcessoDAO;
    @Inject
    private DefinicaoVariavelProcessoManager definicaoVariavelProcessoManager;
    
    private Fluxo fluxo;
    private DefinicaoVariavelProcesso variavel;
    private List<DefinicaoVariavelProcesso> variaveis;
    private int page = 1;
    private int maxPages;
    private Integer maiorOrdem;

    public void setFluxo(Fluxo fluxo) {
        this.fluxo = fluxo;
    }

    public void adicionarVariavel() {
        variavel = new DefinicaoVariavelProcesso();
        variavel.setOrdem(getMaiorOrdem() + 1);
        variavel.setFluxo(fluxo);
    }

    public void persist() {
    	try {
        	definicaoVariavelProcessoManager.persist(variavel);
        	FacesMessages.instance().add("#{infoxMessages['DefinicaoVariavelProcesso_created']}");
	        clear();
    	} catch (DAOException e) {
    		LOG.error("", e);
    		ComponentUtil.<ActionMessagesService>getComponent(ActionMessagesService.NAME).handleDAOException(e);
    		variavel.setId(null);
    	}
    }
    
    public void update() {
    	try {
        	definicaoVariavelProcessoManager.update(variavel);
        	FacesMessages.instance().add("#{infoxMessages['DefinicaoVariavelProcesso_updated']}");
	        clear();
    	} catch (DAOException e) {
    		LOG.error("", e);
    		ComponentUtil.<ActionMessagesService>getComponent(ActionMessagesService.NAME).handleDAOException(e);
    	}
    }

	private void clear() {
		this.variaveis = null;
		this.variavel = null;
		this.maiorOrdem = null;
	}

    public boolean isPersisted() {
        return variavel != null && variavel.getId() != null;
    }

    public void remove(DefinicaoVariavelProcesso obj) {
        try {
            definicaoVariavelProcessoManager.remove(obj);
            this.variaveis = null;
            this.maiorOrdem = null;
            FacesMessages.instance().add("#{infoxMessages['DefinicaoVariavelProcesso_deleted']}");
            if (obj.equals(this.variavel)) {
                this.variavel = null;
            }
        } catch (DAOException e) {
            LOG.error("Não foi possível remover a DefinicaoVariavelProcesso " + obj.getNome(), e);
            ComponentUtil.<ActionMessagesService>getComponent(ActionMessagesService.NAME).handleDAOException(e);
        }
    }

    public List<DefinicaoVariavelProcesso> listVariaveis(int maxResults) {
        if (variaveis == null) {
            int total = definicaoVariavelProcessoManager.getTotalVariaveisByFluxo(fluxo).intValue();
            calcMaxPages(maxResults, total);
            int start = calcStart(maxResults);
            variaveis = definicaoVariavelProcessoManager.listVariaveisByFluxo(fluxo, start, maxResults);
        }
        return variaveis;
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
    
    public Integer getMaiorOrdem() {
    	if (maiorOrdem == null) {
    		maiorOrdem = definicaoVariavelProcessoDAO.getMaiorOrdem(fluxo);
    	}
		return maiorOrdem;
	}
    
    public void moveUp(DefinicaoVariavelProcesso definicaoVariavelProcesso) {
    	try {
    		definicaoVariavelProcessoManager.moveUp(definicaoVariavelProcesso);
    		this.variaveis = null;
    	} catch (DAOException e) {
    		ComponentUtil.<ActionMessagesService>getComponent(ActionMessagesService.NAME).handleDAOException(e);
    		LOG.error("", e);
    	}
    }
    
    public void moveDown(DefinicaoVariavelProcesso definicaoVariavelProcesso) {
    	try {
    		definicaoVariavelProcessoManager.moveDown(definicaoVariavelProcesso);
    		this.variaveis = null;
    	} catch (DAOException e) {
    		ComponentUtil.<ActionMessagesService>getComponent(ActionMessagesService.NAME).handleDAOException(e);
    		LOG.error("", e);
    	}
    }
}
