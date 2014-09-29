package br.com.infox.epp.processo.documento.dao;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.processo.documento.entity.HistoricoStatusDocumento;

@AutoCreate
@Name(HistoricoStatusDocumentoDAO.NAME)
public class HistoricoStatusDocumentoDAO extends DAO<HistoricoStatusDocumento> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "historicoStatusDocumentoDAO";

}
