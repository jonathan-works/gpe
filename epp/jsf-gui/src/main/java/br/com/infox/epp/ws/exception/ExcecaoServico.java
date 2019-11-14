package br.com.infox.epp.ws.exception;

/**
 * Interface utilizada para definir exceções utilizadas nos serviços
 * @author paulo
 *
 */
public interface ExcecaoServico extends ErroServico {
			
	/**
	 * Retorna o erro retornado pelo serviço
	 * @return
	 */
	public ErroServico getErro();
	
	/**
	 * Status HTTP que deve ser retornado ao ocorrer essa exeção
	 * @return
	 */
	public int getStatus();
}
