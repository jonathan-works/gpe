package br.com.infox.ibpm.manager;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.ibpm.dao.TipoProcessoDocumentoDAO;
import br.com.infox.ibpm.entity.TipoProcessoDocumento;

@Name(TipoProcessoDocumentoManager.NAME)
@AutoCreate
public class TipoProcessoDocumentoManager extends GenericManager {
	
	public static final String NAME = "tipoProcessoDocumentoManager";
	private static final long serialVersionUID = 1L;
	
	@In private TipoProcessoDocumentoDAO tipoProcessoDocumentoDAO;

	public List<TipoProcessoDocumento> getTipoProcessoDocumentoInterno(boolean isModelo){
		return tipoProcessoDocumentoDAO.getTipoProcessoDocumentoInterno(isModelo);
	}

}
