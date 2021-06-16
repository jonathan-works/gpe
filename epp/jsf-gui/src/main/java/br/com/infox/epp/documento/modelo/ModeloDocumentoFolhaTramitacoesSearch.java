package br.com.infox.epp.documento.modelo;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.processo.entity.Processo;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ModeloDocumentoFolhaTramitacoesSearch extends PersistenceController {
	
	public String gerarTextoModeloDocumento(Processo processo) {
		return processo.getNumeroProcesso();
	}

}
