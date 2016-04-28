package br.com.infox.epp.ws.exception;
/**
 * Implementaçãop simples de {@link ErroServico}
 * @author paulo
 *
 */
public class ErroServicoImpl implements ErroServico {

	private String code;
	private String message;
	
	public ErroServicoImpl(String codigo, String mensagem) {
		super();
		this.code = codigo;
		this.message = mensagem;
	}

	
	@Override
	public String getCode() {
		return this.code;
	}

	@Override
	public String getMessage() {
		return this.message;
	}
}
