package br.com.infox.epp.processo.comunicacao.service.api;

import javax.ejb.Local;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.entity.Processo;

@Local
public interface AnalisarPedidoProrrogacaoPrazoService {
	Documento getSolicitacaoProrrogacaoPrazo(Processo processoAnaliseDocumento);
	void atualizarPrazo(Processo comunicacao, Integer diasProrrogacao) throws DAOException;
}
