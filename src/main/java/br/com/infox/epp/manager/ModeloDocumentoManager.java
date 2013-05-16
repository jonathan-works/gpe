package br.com.infox.epp.manager;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.dao.ModeloDocumentoDAO;
import br.com.infox.ibpm.entity.GrupoModeloDocumento;
import br.com.infox.ibpm.entity.ModeloDocumento;
import br.com.infox.ibpm.entity.TipoModeloDocumento;

/**
 * Classe Manager para a entidade ModeloDocumento 
 * @author erikliberal
 */
@Name(ModeloDocumentoManager.NAME)
@AutoCreate
public class ModeloDocumentoManager extends GenericManager{
	private static final long serialVersionUID = 4455754174682600299L;

	public static final String NAME = "modeloDocumentoManager";

	@In
	private ModeloDocumentoDAO modeloDocumentoDAO;
	
	/**
	 * Retorna todos os Modelos de Documento ativos
	 * @return lista de modelos de documento ativos
	 */
	public List<ModeloDocumento> getModeloDocumentoList() {
		return modeloDocumentoDAO.getModeloDocumentoList();
	}
	
	public List<ModeloDocumento> getModeloDocumentoByGrupoAndTipo(GrupoModeloDocumento grupo, TipoModeloDocumento tipo){
		return modeloDocumentoDAO.getModeloDocumentoByGrupoAndTipo(grupo, tipo);
	}

}