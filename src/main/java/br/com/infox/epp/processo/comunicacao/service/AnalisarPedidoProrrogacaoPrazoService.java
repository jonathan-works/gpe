package br.com.infox.epp.processo.comunicacao.service;

import javax.ejb.Local;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.entity.Processo;

@Local
public interface AnalisarPedidoProrrogacaoPrazoService {//TODO ver a classe do TCE que extende isso
	Documento getSolicitacaoProrrogacaoPrazo(Processo processoAnaliseDocumento);
	void atualizarPrazo(Processo comunicacao, Integer diasProrrogacao) throws DAOException;
}
