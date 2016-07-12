package br.com.infox.epp.fluxo.crud;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.jbpm.context.def.VariableAccess;

import br.com.infox.core.list.Pageable;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.documento.ClassificacaoDocumentoSearch;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.type.TipoDocumentoEnum;
import br.com.infox.ibpm.process.definition.variable.VariableType;
import br.com.infox.ibpm.variable.VariableEditorModeloHandler;
import br.com.infox.ibpm.variable.VariableEditorModeloHandler.FileConfig;

@Named
@ViewScoped
public class VariavelClassificacaoDocumentoAction implements Serializable, Pageable {
	
    private static final long serialVersionUID = 1L;
    private static final int MAX_RESULTS = 10;
    
    @Inject
    private ClassificacaoDocumentoSearch classificacaoDocumentoSearch;
    
    private List<ClassificacaoDocumento> classificacoesDisponiveis;
    private List<ClassificacaoDocumento> classificacoesDaVariavel;
    private int page = 1;
    private int pageCount;
    private Long total;
    private String nomeClassificacaoDocumento;
    private VariableAccess currentVariable;
    
    
    private TipoDocumentoEnum getTipoDocumento() {
        if (isEditor()) {
            return TipoDocumentoEnum.P;
        } else {
            return TipoDocumentoEnum.D;
        }
    }
    
	private void updateConfiguracoesClassificacoes() {
		if (isEditor()) {
    		updateListaClassificacoesEditor();
    	} else {
    		updateListaClassificacoesUpload();
    	}
	}
    
    private void updateListaClassificacoesUpload() {
    	if (!getClassificacoesDaVariavel().isEmpty()) {
    		FileConfig configuration = new FileConfig();
    		configuration.setCodigosClassificacaoDocumento(new ArrayList<String>());
    		for (ClassificacaoDocumento classificacao : getClassificacoesDaVariavel()) {
				configuration.getCodigosClassificacaoDocumento().add(classificacao.getCodigoDocumento());
			}
    		getCurrentVariable().setConfiguration(VariableEditorModeloHandler.toJson(configuration));
    	} else {
    		getCurrentVariable().setConfiguration(null);
    	}
	}
    
    private void updateListaClassificacoesEditor() {
		FileConfig configuration = null;
		if (!StringUtil.isEmpty(getCurrentVariable().getConfiguration())) {
			configuration = VariableEditorModeloHandler.fromJson(getCurrentVariable().getConfiguration());
		} 
		if (!getClassificacoesDaVariavel().isEmpty()) {
			if (configuration ==  null) {
				configuration = new FileConfig();
			}
			configuration.setCodigosClassificacaoDocumento(new ArrayList<String>());
			for (ClassificacaoDocumento classificacao : getClassificacoesDaVariavel()) {
				configuration.getCodigosClassificacaoDocumento().add(classificacao.getCodigoDocumento());
			}
			getCurrentVariable().setConfiguration(VariableEditorModeloHandler.toJson(configuration));
		} else {
			if (configuration != null) {
				if (configuration.getCodigosModeloDocumento() != null && !configuration.getCodigosModeloDocumento().isEmpty()) {
					configuration.setCodigosClassificacaoDocumento(null);
					getCurrentVariable().setConfiguration(VariableEditorModeloHandler.toJson(configuration));
				} else {
					getCurrentVariable().setConfiguration(null);
				}
			}
		}
    }
    
    private List<String> getCodigosClassificacoesVariavel() {
    	if (!StringUtil.isEmpty(getCurrentVariable().getConfiguration())) {
			return VariableEditorModeloHandler.fromJson(getCurrentVariable().getConfiguration()).getCodigosClassificacaoDocumento();
		}
    	return null;
    }
    
    private boolean isEditor() {
    	return VariableType.EDITOR.name().equals(getCurrentVariable().getType());
    }
    
    public void adicionarClassificacao(ClassificacaoDocumento classificacaoDocumento) {
    	getClassificacoesDaVariavel().add(classificacaoDocumento);
    	updateConfiguracoesClassificacoes();
    	this.classificacoesDisponiveis = null;
    }

	public void removerClassificacao(ClassificacaoDocumento classificacaoDocumento) {
    	getClassificacoesDaVariavel().remove(classificacaoDocumento);
    	updateConfiguracoesClassificacoes();
    	this.classificacoesDisponiveis = null;
    }
    
    public List<ClassificacaoDocumento> getClassificacoesDisponiveis() {
        if (classificacoesDisponiveis == null) {
        	List<String> codigosAdicionadas = getCodigosClassificacoesVariavel();
            this.total = classificacaoDocumentoSearch.countClassificacoesDocumentoDisponiveisVariavelFluxo(codigosAdicionadas, getTipoDocumento(), 
            		getNomeClassificacaoDocumento()); 
            this.pageCount = Long.valueOf(total / MAX_RESULTS + (total % MAX_RESULTS != 0 ? 1 : 0)).intValue();
            int start = (this.page - 1) * MAX_RESULTS;
            classificacoesDisponiveis = classificacaoDocumentoSearch.listClassificacoesDocumentoDisponiveisVariavelFluxo(codigosAdicionadas, 
            		getTipoDocumento(), getNomeClassificacaoDocumento(), start, MAX_RESULTS);
        }
        return classificacoesDisponiveis;
    }
    
    public List<ClassificacaoDocumento> getClassificacoesDaVariavel() {
        if (classificacoesDaVariavel == null) {
        	if (getCodigosClassificacoesVariavel() != null) {
        		this.classificacoesDaVariavel = classificacaoDocumentoSearch.findByListCodigos(getCodigosClassificacoesVariavel());
        	} else {
        		this.classificacoesDaVariavel = new ArrayList<ClassificacaoDocumento>();
        	}
        }
        return this.classificacoesDaVariavel;
    }
    
    public void clearSearch() {
        resetSearch();
        this.nomeClassificacaoDocumento = null;
        this.classificacoesDaVariavel = null;
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

	public VariableAccess getCurrentVariable() {
		return currentVariable;
	}

	public void setCurrentVariable(VariableAccess currentVariable) {
		this.currentVariable = currentVariable;
		this.classificacoesDaVariavel = null;
		this.classificacoesDisponiveis = null;
	}

}
