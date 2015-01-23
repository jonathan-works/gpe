package br.com.infox.epp.processo.documento.numeration;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.entity.Processo;

@AutoCreate
@Name(NumeracaoDocumentoSequencialManager.NAME)
@Scope(ScopeType.EVENT)
public class NumeracaoDocumentoSequencialManager extends Manager<NumeracaoDocumentoSequencialDAO, NumeracaoDocumentoSequencial> {
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "numeracaoDocumentoSequencialManager";
	
	public Integer getNextNumeracaoDocumentoSequencial(Processo processo) throws DAOException {
		return getDao().getNextNumeracaoDocumentoSequencial(processo);
	}
	
}
