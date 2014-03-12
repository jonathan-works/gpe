package br.com.infox.epp.documento.manager;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.documento.dao.TipoProcessoDocumentoDAO;
import br.com.infox.epp.documento.entity.TipoProcessoDocumento;

@Name(TipoProcessoDocumentoManager.NAME)
@AutoCreate
public class TipoProcessoDocumentoManager extends Manager<TipoProcessoDocumentoDAO, TipoProcessoDocumento> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "tipoProcessoDocumentoManager";
	
	private TipoProcessoDocumento tipoProcessoDocumento;
	private TipoProcessoDocumento tipoProcessoDocumentoRO;
	
	public void limpar(){
		tipoProcessoDocumento = null;
	}
	
	public TipoProcessoDocumento getTipoProcessoDocumento() {
		return tipoProcessoDocumento;
	}
	
	public void setTipoProcessoDocumento(
			TipoProcessoDocumento tipoProcessoDocumento) {	
			this.tipoProcessoDocumento = tipoProcessoDocumento;		
	}
	
	public void setTipoProcessoDocumentoRO(TipoProcessoDocumento tipoProcessoDocumentoRO) {
		this.tipoProcessoDocumentoRO = tipoProcessoDocumentoRO;
	}

	public TipoProcessoDocumento getTipoProcessoDocumentoRO() {
		return tipoProcessoDocumentoRO;
	}
	
	public List<TipoProcessoDocumento> getUseableTipoProcessoDocumento(boolean isModelo, Papel papel) {
	    return getDao().getUseableTipoProcessoDocumento(isModelo, papel);
	}
}
