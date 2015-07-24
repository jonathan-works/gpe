package br.com.infox.epp.fluxo.crud;

import java.util.Collections;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import br.com.infox.log.Log;
import br.com.infox.log.Logging;

import br.com.infox.core.action.AbstractAction;
import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.fluxo.dao.DefinicaoVariavelProcessoDAO;
import br.com.infox.epp.fluxo.entity.DefinicaoVariavelProcesso;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.manager.DefinicaoVariavelProcessoManager;

@Scope(ScopeType.PAGE)
@Name(DefinicaoVariavelProcessoAction.NAME)
public class DefinicaoVariavelProcessoAction extends AbstractAction<DefinicaoVariavelProcesso, DefinicaoVariavelProcessoManager> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "definicaoVariavelProcessoAction";
    private static final Log LOG = Logging.getLog(DefinicaoVariavelProcessoAction.class);

    @In
    private ActionMessagesService actionMessagesService;
    @In
    private DefinicaoVariavelProcessoDAO definicaoVariavelProcessoDAO;
    
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
        this.variavel = new DefinicaoVariavelProcesso();
        this.variavel.setOrdem(getMaiorOrdem() + 1);
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
            this.maiorOrdem = null;
            FacesMessages.instance().add("#{infoxMessages['DefinicaoVariavelProcesso_created']}");
        } else if (AbstractAction.UPDATED.equals(ret)) {
            this.variaveis = null;
            this.variavel = null;
            FacesMessages.instance().add("#{infoxMessages['DefinicaoVariavelProcesso_updated']}");
        }
    }

    public boolean isPersisted() {
        if (variavel != null && variavel.getId() != null) {
            if (!getManager().contains(variavel)) {
                try {
                    getManager().merge(variavel);
                } catch (DAOException e) {
                    LOG.error(".isPersisted()", e);
                }
            }
            return getManager().contains(variavel);
        }
        return false;
    }

    @Override
    public String remove(DefinicaoVariavelProcesso obj) {
        String ret;
        try {
            ret = super.remove(getManager().merge(obj));
        } catch (DAOException e) {
            LOG.error("Não foi possível remover a DefinicaoVariavelProcesso "
                    + obj.getNome(), e);
            ret = "UNMERGED";
        }
        if (AbstractAction.REMOVED.equals(ret)) {
            this.variaveis = null;
            this.maiorOrdem = null;
            FacesMessages.instance().add("#{infoxMessages['DefinicaoVariavelProcesso_deleted']}");
            if (obj.equals(this.variavel)) {
                this.variavel = null;
            }
        }
        return ret;
    }

    public List<DefinicaoVariavelProcesso> listVariaveis(int maxResults) {
        if (this.variaveis == null) {
            int total = getManager().getTotalVariaveisByFluxo(fluxo).intValue();
            calcMaxPages(maxResults, total);
            int start = calcStart(maxResults);
            this.variaveis = getManager().listVariaveisByFluxo(fluxo, start, maxResults);
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
        return getManager().getNomeAmigavel(variavel);
    }

    public String getNomeAmigavel() {
        return getNomeAmigavel(variavel);
    }

    public void setNomeAmigavel(String nomeAmigavel) {
        getManager().setNome(variavel, nomeAmigavel);
    }
    
    public Integer getMaiorOrdem() {
    	if (maiorOrdem == null) {
    		maiorOrdem = definicaoVariavelProcessoDAO.getMaiorOrdem(fluxo);
    	}
		return maiorOrdem;
	}
    
    public void moveUp(DefinicaoVariavelProcesso definicaoVariavelProcesso) {
    	try {
    		for (DefinicaoVariavelProcesso definicao : this.variaveis) {
    			if (definicao.getOrdem().equals(definicaoVariavelProcesso.getOrdem() - 1)) {
    				definicao.setOrdem(definicao.getOrdem() + 1);
    				getManager().update(definicao);
    				break;
    			}
    		}
    		definicaoVariavelProcesso.setOrdem(definicaoVariavelProcesso.getOrdem() - 1);
    		getManager().update(definicaoVariavelProcesso);
    		this.variaveis = null;
    	} catch (DAOException e) {
    		actionMessagesService.handleDAOException(e);
    		LOG.error("", e);
    	}
    }
    
    public void moveDown(DefinicaoVariavelProcesso definicaoVariavelProcesso) {
    	try {
    		for (DefinicaoVariavelProcesso definicao : this.variaveis) {
    			if (definicao.getOrdem().equals(definicaoVariavelProcesso.getOrdem() + 1)) {
    				definicao.setOrdem(definicao.getOrdem() - 1);
    				getManager().update(definicao);
    				break;
    			}
    		}
    		definicaoVariavelProcesso.setOrdem(definicaoVariavelProcesso.getOrdem() + 1);
    		getManager().update(definicaoVariavelProcesso);
    		this.variaveis = null;
    	} catch (DAOException e) {
    		actionMessagesService.handleDAOException(e);
    		LOG.error("", e);
    	}
    }
}
