package br.com.infox.ibpm.manager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;
import br.com.infox.ibpm.dao.TipoModeloDocumentoDAO;

@Name(TipoModeloDocumentoManager.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class TipoModeloDocumentoManager extends GenericManager{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "tipoModeloDocumentoManager";
	
	@In private TipoModeloDocumentoDAO tipoModeloDocumentoDAO; 
	
	public boolean violaUnicidadeDeAbreviacao(TipoModeloDocumento tipoModeloDocumento){
		return tipoModeloDocumentoDAO.existeOutroTipoModeloDocumentoComMesmaAbreviacao(tipoModeloDocumento);
	}
	
	public boolean violaUnicidadeDeDescricao(TipoModeloDocumento tipoModeloDocumento){
		return tipoModeloDocumentoDAO.existeOutroTipoModeloDocumentoComMesmaDescricao(tipoModeloDocumento);
	}
	
}
