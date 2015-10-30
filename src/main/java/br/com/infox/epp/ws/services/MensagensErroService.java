package br.com.infox.epp.ws.services;

import java.util.Arrays;
import java.util.Collection;

import javax.ejb.EJBException;

import br.com.infox.epp.ws.exception.ExcecaoMultiplaServico;
import br.com.infox.epp.ws.exception.ExcecaoServico;
import br.com.infox.epp.ws.exception.ExcecaoServico.ErroServico;
import br.com.infox.epp.ws.exception.ExcecaoServico.ErroServicoImpl;

/**
 * Serviço responsável por gerenciar mensagens e códigos de erros
 * @author paulo
 *
 */
public class MensagensErroService {
	
	public static String CODIGO_ERRO_INDEFINIDO = "ME0000";   
	
	private Collection<ErroServico> getErrosExcecao(ExcecaoMultiplaServico excecao) {
		return excecao.getErros();
	}
	
	private Collection<ErroServico> getErrosExcecao(ExcecaoServico excecao) {
		return Arrays.asList(excecao.getErro());
	}
	
	private Collection<ErroServico> getErrosExcecao(Throwable excecao) {
		ErroServico erro = new ErroServicoImpl(CODIGO_ERRO_INDEFINIDO, excecao.getMessage());
		return Arrays.asList(erro);
	}
	
	/**
	 * Retorna os erros associados a uma exceção
	 */
	public Collection<ErroServico> getErros(Throwable excecao) {
		if(excecao instanceof EJBException && excecao.getCause() != null) {
			excecao = excecao.getCause();
		}
		if(ExcecaoMultiplaServico .class.isAssignableFrom(excecao.getClass())) {
			return getErrosExcecao((ExcecaoMultiplaServico) excecao);
		}
		else if(ExcecaoServico.class.isAssignableFrom(excecao.getClass())) {
			return getErrosExcecao((ExcecaoServico) excecao);
		}
		else
		{
			return getErrosExcecao(excecao);
		}
	}

	/**
	 * Retorna um erro único que identifica uma exceção
	 */
	public ErroServico getErro(Throwable excecao) {
		Collection<ErroServico> erros = getErros(excecao);
		if(erros == null || erros.isEmpty()) {
			return new ErroServicoImpl(CODIGO_ERRO_INDEFINIDO, excecao.getMessage());
		}
		return erros.iterator().next();
	}
}
