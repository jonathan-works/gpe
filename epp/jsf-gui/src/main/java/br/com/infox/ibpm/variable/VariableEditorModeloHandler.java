package br.com.infox.ibpm.variable;

import static br.com.infox.constants.WarningConstants.RAWTYPES;
import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.context.def.VariableAccess;

import com.google.gson.Gson;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.cdi.util.Beans;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.list.associated.AssociatedTipoModeloVariavelList;
import br.com.infox.epp.documento.list.associative.AssociativeModeloDocumentoList;
import br.com.infox.epp.documento.modelo.ModeloDocumentoSearch;

public class VariableEditorModeloHandler {

	private VariableAccess variableAccess;
	private List<ModeloDocumento> modeloDocumentoList;
	private String pasta;
	
	public void init(VariableAccess variableAccess) {
		this.variableAccess = variableAccess;
		carregarModeloList();
	}
	
    private void carregarModeloList() {
    	modeloDocumentoList = new ArrayList<>();
    	if (!StringUtil.isEmpty(this.variableAccess.getConfiguration())) {
    		FileConfig configuracoes = fromJson(this.variableAccess.getConfiguration());
    		pasta = configuracoes.pasta;
    		if (configuracoes.getCodigosModeloDocumento() != null && !configuracoes.getCodigosModeloDocumento().isEmpty()) {
	    		ModeloDocumentoSearch modeloDocumentoSearch = Beans.getReference(ModeloDocumentoSearch.class);
	    		modeloDocumentoList.addAll(modeloDocumentoSearch.getModeloDocumentoListByListCodigos(configuracoes.getCodigosModeloDocumento()));
    		}
    	}
    }
    
    public void updateModelo() {
		FileConfig configuration = null;
		if (!StringUtil.isEmpty(this.variableAccess.getConfiguration())) {
			configuration = fromJson(this.variableAccess.getConfiguration());
		} 
		
		if (!getModeloDocumentoList().isEmpty()) {
			if (configuration ==  null) {
				configuration = new FileConfig();
			}
			configuration.setCodigosModeloDocumento(new ArrayList<String>());
			for (ModeloDocumento modelo : getModeloDocumentoList()) {
				configuration.getCodigosModeloDocumento().add(modelo.getCodigo());
			}
			this.variableAccess.setConfiguration(toJson(configuration));
		} else {
			if (configuration != null) {
				if (configuration.getCodigosClassificacaoDocumento() != null && !configuration.getCodigosClassificacaoDocumento().isEmpty()) {
					configuration.setCodigosModeloDocumento(null);
					this.variableAccess.setConfiguration(toJson(configuration));
				} else {
					this.variableAccess.setConfiguration(null);
				}
			}
		}
    }
    
    // TODO: Esse entityList está bizarro, é a causa dos 2 warnings abaixo
    @SuppressWarnings({ UNCHECKED, RAWTYPES })
    public void addModelo(ModeloDocumento modelo) {
        if (modeloDocumentoList == null) {
            modeloDocumentoList = new ArrayList<>();
        }
        modeloDocumentoList.add(modelo);
        EntityList entityList = getAssociatedTipoModeloVariavelList();
        entityList.getResultList().add(modelo);
        refreshModelosAssociados();
        updateModelo();
    }

    private void refreshModelosAssociados() {
        getAssociativeModeloDocumentoList().refreshModelosAssociados();
    }

    public void removeModelo(ModeloDocumento modelo) {
        modeloDocumentoList.remove(modelo);
        getAssociatedTipoModeloVariavelList().getResultList().remove(modelo);
        refreshModelosAssociados();
        updateModelo();
    }

    public List<ModeloDocumento> getModeloDocumentoList() {
        if (modeloDocumentoList == null) {
        	carregarModeloList();
        }
        return modeloDocumentoList;
    }
	
    public void setPasta(String pasta) {
    	this.pasta = pasta;
    	FileConfig configuration = null;
		if (!StringUtil.isEmpty(this.variableAccess.getConfiguration())) {
			configuration = fromJson(this.variableAccess.getConfiguration());
		}
		if (configuration ==  null) {
			configuration = new FileConfig();
		}
		configuration.setPasta(pasta);
		this.variableAccess.setConfiguration(toJson(configuration));
    }
    
    public String getPasta() {
    	return pasta;
    }
    
	public static FileConfig fromJson(String configuration) {
		return new Gson().fromJson(configuration, FileConfig.class);
	}
	
	public static String toJson(FileConfig configuration) {
		return new Gson().toJson(configuration, FileConfig.class);
	}
	
	public static class FileConfig {
		private List<String> codigosModeloDocumento;
		private List<String> codigosClassificacaoDocumento;
		private String pasta;

		public List<String> getCodigosModeloDocumento() {
			return codigosModeloDocumento;
		}

		public void setCodigosModeloDocumento(List<String> codigosModeloDocumento) {
			this.codigosModeloDocumento = codigosModeloDocumento;
		}

		public List<String> getCodigosClassificacaoDocumento() {
			return codigosClassificacaoDocumento;
		}

		public void setCodigosClassificacaoDocumento(List<String> codigosClassificacaoDocumento) {
			this.codigosClassificacaoDocumento = codigosClassificacaoDocumento;
		}

		public String getPasta() {
			return pasta;
		}

		public void setPasta(String pasta) {
			this.pasta = pasta;
		}
	}
	
	private AssociatedTipoModeloVariavelList getAssociatedTipoModeloVariavelList() {
	    return Beans.getReference(AssociatedTipoModeloVariavelList.class);
	}
	
	private AssociativeModeloDocumentoList getAssociativeModeloDocumentoList() {
	    return Beans.getReference(AssociativeModeloDocumentoList.class);
	}
}
