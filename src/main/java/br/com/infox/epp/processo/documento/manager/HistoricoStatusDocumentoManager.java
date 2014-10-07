package br.com.infox.epp.processo.documento.manager;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.documento.dao.HistoricoStatusDocumentoDAO;
import br.com.infox.epp.processo.documento.entity.HistoricoStatusDocumento;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.type.TipoAlteracaoDocumento;

@AutoCreate
@Name(HistoricoStatusDocumentoManager.NAME)
public class HistoricoStatusDocumentoManager extends Manager<HistoricoStatusDocumentoDAO, HistoricoStatusDocumento>{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "historicoStatusDocumentoManager";
	
	public void gravarHistoricoDocumento(String motivo, TipoAlteracaoDocumento tipoAlteracaoDocumento, 
			Documento documento) throws DAOException {
		HistoricoStatusDocumento historicoStatusDocumento = new HistoricoStatusDocumento();
    	historicoStatusDocumento.setMotivo(motivo);
    	historicoStatusDocumento.setDocumento(documento);
    	historicoStatusDocumento.setTipoAlteracaoDocumento(tipoAlteracaoDocumento);
    	persist(historicoStatusDocumento);
	}
	
	public boolean existeAlgumHistoricoDoDocumento(Integer idDocumento){
		return getDao().existeAlgumHistoricoDoDocumento(idDocumento);
	}
	
	public List<HistoricoStatusDocumento> getListHistoricoByDocumento(Documento documento){
		return getDao().getListHistoricoByDocumento(documento);
	}

}
