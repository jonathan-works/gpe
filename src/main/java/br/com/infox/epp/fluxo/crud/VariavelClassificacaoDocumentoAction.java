package br.com.infox.epp.fluxo.crud;

import java.io.Serializable;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.list.Pageable;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.type.TipoDocumentoEnum;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.VariavelClassificacaoDocumento;
import br.com.infox.epp.fluxo.manager.VariavelClassificacaoDocumentoManager;
import br.com.infox.ibpm.process.definition.variable.VariableType;
import br.com.infox.log.Log;
import br.com.infox.log.Logging;

@Name(VariavelClassificacaoDocumentoAction.NAME)
@Scope(ScopeType.PAGE)
@Transactional
public class VariavelClassificacaoDocumentoAction implements Serializable, Pageable {
	
    public static final String NAME = "variavelClassificacaoDocumentoAction";
    private static final long serialVersionUID = 1L;
    private static final Log LOG = Logging.getLog(VariavelClassificacaoDocumentoAction.class);
    private static final int MAX_RESULTS = 10;
    
    @In
    private VariavelClassificacaoDocumentoManager variavelClassificacaoDocumentoManager;
    @In
    private ActionMessagesService actionMessagesService;
    
    private VariavelClassificacaoDocumento variavelClassificacaoDocumento = new VariavelClassificacaoDocumento();
    private String currentVariable;
    private TipoDocumentoEnum tipoDocumento;
    private Fluxo fluxo;
    private List<ClassificacaoDocumento> classificacoesDisponiveis;
    private List<VariavelClassificacaoDocumento> classificacoesDaVariavel;
    private int page = 1;
    private int pageCount;
    private Long total;
    private String nomeClassificacaoDocumento;
    
    public VariavelClassificacaoDocumento getVariavelClassificacaoDocumento() {
        return variavelClassificacaoDocumento;
    }
    
    public void setCurrentVariable(String currentVariable) {
        this.currentVariable = currentVariable;
        clearSearch();
    }
    
    public String getCurrentVariable() {
        return currentVariable;
    }
    
    public void setCurrentVariableType(VariableType currentVariableType) {
        if (currentVariableType == VariableType.EDITOR) {
            tipoDocumento = TipoDocumentoEnum.P;
        } else if (currentVariableType == VariableType.FILE) {
            tipoDocumento = TipoDocumentoEnum.D;
        }
    }
    
    public void adicionarClassificacao(ClassificacaoDocumento classificacao) {
        variavelClassificacaoDocumento.setVariavel(currentVariable);
        variavelClassificacaoDocumento.setClassificacaoDocumento(classificacao);
        variavelClassificacaoDocumento.setFluxo(fluxo);
        variavelClassificacaoDocumento.setPublicado(false);
        variavelClassificacaoDocumento.setRemoverNaPublicacao(false);
        try {
            variavelClassificacaoDocumentoManager.persist(variavelClassificacaoDocumento);
            clearSearch();
        } catch (DAOException e) {
            LOG.error("", e);
            actionMessagesService.handleDAOException(e);
        }
        variavelClassificacaoDocumento = new VariavelClassificacaoDocumento();
    }
    
    public void removerClassificacao(VariavelClassificacaoDocumento variavelClassificacaoDocumento) {
        try {
            // Refresh para atualizar o status de publicado da variável, por causa do cache do Hibernate
            variavelClassificacaoDocumentoManager.refresh(variavelClassificacaoDocumento);
            variavelClassificacaoDocumentoManager.remove(variavelClassificacaoDocumento);
            clearSearch();
        } catch (DAOException e) {
            LOG.error("", e);
            actionMessagesService.handleDAOException(e);
        }
    }
    
    public void setFluxo(Fluxo fluxo) {
        this.fluxo = fluxo;
        clearSearch();
    }
    
    public List<ClassificacaoDocumento> getClassificacoesDisponiveis() {
        if (classificacoesDisponiveis == null) {
            Integer idFluxo = fluxo.getIdFluxo();
            this.total = variavelClassificacaoDocumentoManager.totalClassificacoesDisponiveisParaVariavel(idFluxo, currentVariable, tipoDocumento, nomeClassificacaoDocumento);
            this.pageCount = Long.valueOf(total / MAX_RESULTS + (total % MAX_RESULTS != 0 ? 1 : 0)).intValue();
            int start = (this.page - 1) * MAX_RESULTS;
            classificacoesDisponiveis = variavelClassificacaoDocumentoManager.listClassificacoesDisponiveisParaVariavel(idFluxo, currentVariable, tipoDocumento, nomeClassificacaoDocumento, start, MAX_RESULTS);
        }
        return classificacoesDisponiveis;
    }
    
    public List<VariavelClassificacaoDocumento> getClassificacoesDaVariavel() {
        if (classificacoesDaVariavel == null) {
            classificacoesDaVariavel = variavelClassificacaoDocumentoManager.listVariavelClassificacao(currentVariable, fluxo.getIdFluxo());
        }
        return classificacoesDaVariavel;
    }
    
    public void clearSearch() {
        resetSearch();
        nomeClassificacaoDocumento = null;
        classificacoesDaVariavel = null;
    }
    
    public void resetSearch() {
        classificacoesDisponiveis = null;
        page = 1;
        pageCount = 0;
    }
    
    @Override
    public Integer getPage() {
        return page;
    }

    @Override
    public void setPage(Integer page) {
        this.page = page;
        classificacoesDisponiveis = null;
    }

    @Override
    public Integer getPageCount() {
        return pageCount;
    }

    @Override
    public boolean isPreviousExists() {
        return page > 1;
    }

    @Override
    public boolean isNextExists() {
        return page < pageCount && pageCount > 1;
    }
    
    public Long getResultCount() {
        return total;
    }
    
    public String getNomeClassificacaoDocumento() {
        return nomeClassificacaoDocumento;
    }
    
    public void setNomeClassificacaoDocumento(String nomeClassificacaoDocumento) {
        this.nomeClassificacaoDocumento = nomeClassificacaoDocumento;
    }
}
