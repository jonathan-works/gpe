package br.com.infox.epp.documento.dao;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.constants.WarningConstants;
import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.documento.entity.GrupoModeloDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;
import br.com.infox.epp.documento.query.ModeloDocumentoQuery;

/**
 * Classe DAO para a entidade ModeloDocumento
 * @author erikliberal
 *
 */
@Name(ModeloDocumentoDAO.NAME)
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
	
	public ModeloDocumento getModeloDocumentoByTitulo(String titulo){
		String hql = "select o from ModeloDocumento o where o.tituloModeloDocumento = :titulo";
		return (ModeloDocumento) entityManager.createQuery(hql)
				.setParameter("titulo", titulo).getSingleResult();
	}
	
	@SuppressWarnings(WarningConstants.UNCHECKED)
	public List<ModeloDocumento> getModeloDocumentoByGrupoAndTipo(GrupoModeloDocumento grupo, TipoModeloDocumento tipo){
		String hql = "select distinct o from ModeloDocumento o where " +
				"o.tipoModeloDocumento.grupoModeloDocumento = :grupo and " +
				"o.tipoModeloDocumento = :tipo " +
				"order by o.tituloModeloDocumento";
		return entityManager.createQuery(hql)
				.setParameter("grupo", grupo)
				.setParameter("tipo", tipo).getResultList();
	}
	
	@SuppressWarnings(WarningConstants.UNCHECKED)
	public List<ModeloDocumento> getModelosDocumentoInListaModelos(String listaModelos){
		String hql = "select o from ModeloDocumento o " +
						"where o.idModeloDocumento in (" +
						listaModelos + ") order by modeloDocumento";
		return (List<ModeloDocumento>) entityManager.createQuery(hql).getResultList();
	}
	
}