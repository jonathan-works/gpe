package br.com.infox.epp.processo.documento.numeration;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.entity.Processo;

@AutoCreate
@Stateless
@Name(NumeracaoDocumentoSequencialManager.NAME)
public class NumeracaoDocumentoSequencialManager extends Manager<NumeracaoDocumentoSequencialDAO, NumeracaoDocumentoSequencial> {
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "numeracaoDocumentoSequencialManager";
	
	public Integer getNextNumeracaoDocumentoSequencial(Processo processo) throws DAOException {
		return getDao().getNextNumeracaoDocumentoSequencial(processo);
	}
	
	public NumeracaoDocumentoSequencial removeByProcesso(Processo processo) throws DAOException {
	    return getDao().removeByProcesso(processo);
	}
}
