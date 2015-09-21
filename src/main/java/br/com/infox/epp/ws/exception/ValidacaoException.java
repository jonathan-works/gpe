package br.com.infox.epp.ws.exception;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.validation.ValidationException;

import br.com.infox.epp.ws.messages.WSMessages;
import br.com.infox.epp.ws.services.MensagensErroService;

/**
 * Implementa erros de validação contendo códigos de erro, além de poder representar vários erros
 * @author paulo
 *
 */
public class ValidacaoException extends ValidationException implements ExcecaoMultiplaServico {
	
	private static final long serialVersionUID = 1L;

	private List<ErroServico> erros = new ArrayList<>();
	
	public ValidacaoException() {
		super();
	}
	public ValidacaoException(String codigo, String mensagem) {
		this();
		adicionar(codigo, mensagem);
	}
	
	public ValidacaoException(String codigo, String mensagem, Throwable causa) {
		this(codigo, mensagem);
		super.initCause(causa);
	}
	
	public ValidacaoException(WSMessages mensagem) {
		this(mensagem.codigo(), mensagem.label());
	}

	public ValidacaoException(WSMessages mensagem, Throwable causa) {
		this(mensagem.codigo(), mensagem.label(), causa);
	}
	
	public void adicionar(String codigo, String mensagem) {
		erros.add(new ErroServicoImpl(codigo, mensagem));
	}
	
	@Override
	public Collection<ErroServico> getErros() {
		return erros;
	}
	
	@Override
	public ErroServico getErro() {
		if(erros.isEmpty()) {
			throw new ValidacaoException(MensagensErroService.CODIGO_ERRO_INDEFINIDO, "Erro indefinido");
		}
		return erros.get(0);
	}
	

}
