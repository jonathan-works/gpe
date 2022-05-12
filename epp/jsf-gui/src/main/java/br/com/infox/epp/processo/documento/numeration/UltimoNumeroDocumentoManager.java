package br.com.infox.epp.processo.documento.numeration;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;

@AutoCreate
@Stateless
@Name(UltimoNumeroDocumentoManager.NAME)
public class UltimoNumeroDocumentoManager extends Manager<UltimoNumeroDocumentoDAO, UltimoNumeroDocumento> {
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "ultimoNumeroDocumentoManager";
	
	public Long getNextNumeroDocumento(TipoModeloDocumento tipoModeloDocumento, Integer ano) throws DAOException {
		return getDao().getNextNumeroDocumento(tipoModeloDocumento, ano);
	}
	
	public void createUltimoNumero(TipoModeloDocumento tipoModeloDocumento, Integer ano) {
		getDao().createUltimoNumero(tipoModeloDocumento, ano);
	}
}
