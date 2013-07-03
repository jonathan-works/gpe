package br.com.infox.epp.manager;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.ibpm.dao.TipoProcessoDocumentoDAO;
import br.com.infox.ibpm.entity.TipoProcessoDocumento;

@Name(TipoProcessoDocumentoManager.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class TipoProcessoDocumentoManager extends GenericManager {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "tipoProcessoDocumentoManager";
	
	private TipoProcessoDocumento tipoProcessoDocumento;
	private TipoProcessoDocumento tipoProcessoDocumentoRO;
	@In private TipoProcessoDocumentoDAO tipoProcessoDocumentoDAO;
	
	/**
	 * Se o tipoProcessoDocumento for nulo (caso o componente utilizado seja
	 * o editor sem assinatura digital, o tipoProcessoDOcumento será setado
	 * automaticamente com um valor aleatorio
	 **/
	public TipoProcessoDocumento getTipoProcessoDocumentoFluxo(){
		return tipoProcessoDocumentoDAO.getTipoProcessoDocumentoFluxo();
	}
	
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
	
	public List<TipoProcessoDocumento> getTipoProcessoDocumentoInterno(boolean isModelo){
		return tipoProcessoDocumentoDAO.getTipoProcessoDocumentoInterno(isModelo);
	}
}
