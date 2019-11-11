package br.com.infox.epp.ws.exception;

import java.util.Collection;

/**
 * Interface utilizada para representar uma exceção contendo múltplos erros de serviço
 * @author paulo
 *
 */
public interface ExcecaoMultiplaServico extends ExcecaoServico {
	/**
	 * Coleção contendo os erros representados por essa exceção
	 * @return
	 */
	public Collection<ErroServico> getErros();
}
