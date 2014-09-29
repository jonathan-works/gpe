package br.com.infox.epp.processo.documento.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.documento.dao.HistoricoStatusDocumentoDAO;
import br.com.infox.epp.processo.documento.entity.HistoricoStatusDocumento;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.documento.type.TipoAlteracaoDocumento;

@AutoCreate
@Name(HistoricoStatusDocumentoManager.NAME)
public class HistoricoStatusDocumentoManager extends Manager<HistoricoStatusDocumentoDAO, HistoricoStatusDocumento>{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "historicoStatusDocumentoManager";
	
	public void gravarHistoricoDocumento(String motivo, TipoAlteracaoDocumento tipoAlteracaoDocumento, 
			ProcessoDocumento processoDocumento) throws DAOException {
		HistoricoStatusDocumento historicoStatusDocumento = new HistoricoStatusDocumento();
    	historicoStatusDocumento.setMotivo(motivo);
    	historicoStatusDocumento.setProcessoDocumento(processoDocumento);
    	historicoStatusDocumento.setTipoAlteracaoDocumento(tipoAlteracaoDocumento);
    	persist(historicoStatusDocumento);
	}

}
