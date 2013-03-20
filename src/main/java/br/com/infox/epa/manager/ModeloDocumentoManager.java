package br.com.infox.epa.manager;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epa.dao.ModeloDocumentoDAO;
import br.com.infox.ibpm.entity.ModeloDocumento;

/**
 * Classe Manager para a entidade ModeloDocumento 
 * @author erikliberal
 */
@Name(ModeloDocumentoManager.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class ModeloDocumentoManager extends GenericManager{
	private static final long serialVersionUID = 4455754174682600299L;
	public static final String NAME = "modeloDocumentoManager";
	
	private ModeloDocumento modeloDocumento;
	private String modeloDocumentoRO;

	@In
	private ModeloDocumentoDAO modeloDocumentoDAO;
	
	public void limpar(){
		modeloDocumento = null;
	}
	
	/**
	 * Retorna todos os Modelos de Documento ativos
	 * @return lista de modelos de documento ativos
	 */
	public List<ModeloDocumento> getModeloDocumentoList() {
		return modeloDocumentoDAO.getModeloDocumentoList();
	}
	
	public void setModeloDocumentoCombo(ModeloDocumento modeloDocumentoCombo) {
		this.modeloDocumento = modeloDocumentoCombo;
	}
	
	public ModeloDocumento getModeloDocumentoCombo() {
		return modeloDocumento;
	}

}