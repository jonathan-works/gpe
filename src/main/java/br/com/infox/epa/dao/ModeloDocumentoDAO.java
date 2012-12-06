package br.com.infox.epa.dao;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epa.query.ModeloDocumentoQuery;
import br.com.infox.ibpm.entity.ModeloDocumento;

/**
 * Classe DAO para a entidade ModeloDocumento
 * @author erikliberal
 *
 */
@Name(ModeloDocumentoDAO.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class ModeloDocumentoDAO extends GenericDAO {
	private static final long serialVersionUID = -39703831180567768L;
	public static final String NAME = "modeloDocumentoDAO";
	
	/**
	 * Retorna todos os Modelos de Documento ativos
	 * @return lista de modelos de documento ativos
	 */
	public List<ModeloDocumento> getModeloDocumentoList() {
		return getNamedResultList(ModeloDocumentoQuery.LIST_ATIVOS, null);
	}
	
}