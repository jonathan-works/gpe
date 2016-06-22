package br.com.infox.ibpm.variable;

import static br.com.infox.constants.WarningConstants.RAWTYPES;
import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.context.def.VariableAccess;

import com.google.gson.Gson;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.entity.VariavelTipoModelo;
import br.com.infox.epp.documento.list.associated.AssociatedTipoModeloVariavelList;
import br.com.infox.epp.documento.list.associative.AssociativeModeloDocumentoList;
import br.com.infox.epp.documento.modelo.ModeloDocumentoSearch;
import br.com.infox.seam.util.ComponentUtil;

public class VariableEditorModeloHandler {

	private VariableAccess variableAccess;
	private List<ModeloDocumento> modeloDocumentoList;
	
	public void init(VariableAccess variableAccess) {
		this.variableAccess = variableAccess;
		carregarModeloList();
	}
	
    private void carregarModeloList() {
    	modeloDocumentoList = new ArrayList<>();
    	if (!StringUtil.isEmpty(this.variableAccess.getConfiguration())) {
    		EditorConfig configuracoes = fromJson(this.variableAccess.getConfiguration());
    		if (configuracoes.getCodigosModeloDocumento() != null && !configuracoes.getCodigosModeloDocumento().isEmpty()) {
	    		ModeloDocumentoSearch modeloDocumentoSearch = BeanManager.INSTANCE.getReference(ModeloDocumentoSearch.class);
	    		for (String codigoModelo : configuracoes.getCodigosModeloDocumento()) {
	    			modeloDocumentoList.add(modeloDocumentoSearch.getModeloDocumentoByCodigo(codigoModelo));
	    		}
    		}
    	}
    }
    
    public void updateModelo() {
		EditorConfig configuration = null;
		if (!StringUtil.isEmpty(this.variableAccess.getConfiguration())) {
			configuration = fromJson(this.variableAccess.getConfiguration());
		} 
		
		if (!getModeloDocumentoList().isEmpty()) {
			if (configuration ==  null) {
				configuration = new EditorConfig();
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
        EntityList modeloDocumentoList = ComponentUtil.getComponent(AssociatedTipoModeloVariavelList.NAME);
        modeloDocumentoList.getResultList().add(modelo);
        refreshModelosAssociados();
        updateModelo();
    }

    private void refreshModelosAssociados() {
        AssociativeModeloDocumentoList associativeModeloDocumentoList = ComponentUtil.getComponent(AssociativeModeloDocumentoList.NAME);
        associativeModeloDocumentoList.refreshModelosAssociados();
    }

    public void removeModelo(ModeloDocumento modelo) {
        modeloDocumentoList.remove(modelo);
        EntityList<VariavelTipoModelo> modeloDocumentoList = ComponentUtil.getComponent(AssociatedTipoModeloVariavelList.NAME);
        modeloDocumentoList.getResultList().remove(modelo);
        refreshModelosAssociados();
        updateModelo();
    }

    public List<ModeloDocumento> getModeloDocumentoList() {
        if (modeloDocumentoList == null) {
        	carregarModeloList();
        }
        return modeloDocumentoList;
    }
	
	public static EditorConfig fromJson(String configuration) {
		return new Gson().fromJson(configuration, EditorConfig.class);
	}
	
	public static String toJson(EditorConfig configuration) {
		return new Gson().toJson(configuration, EditorConfig.class);
	}
	
	public static class EditorConfig {
		private List<String> codigosModeloDocumento;
		private List<String> codigosClassificacaoDocumento;

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
	}
}
