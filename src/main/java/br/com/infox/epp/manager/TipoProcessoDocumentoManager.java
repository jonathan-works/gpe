package br.com.infox.epp.manager;

import java.util.List;

import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.ibpm.dao.TipoProcessoDocumentoDAO;
import br.com.infox.ibpm.entity.TipoProcessoDocumento;
import br.com.itx.util.EntityUtil;

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
		String sql = "select o from TipoProcessoDocumento o ";
		Query q = EntityUtil.getEntityManager().createQuery(sql);
		return (TipoProcessoDocumento) q.getResultList().get(0);
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
