package br.com.infox.epp.ws.exception;

public class ExcecaoServicoImpl implements ExcecaoServico {

	private ErroServico erro;
	private int status;
	
	public ExcecaoServicoImpl(int status, String codigo, String mensagem) {
		this.erro = new ErroServicoImpl(codigo, mensagem);
		this.status = status;
	}
	
	@Override
	public ErroServico getErro() {
		return erro;
	}

	@Override
	public int getStatus() {
		return status;
	}

	@Override
	public String getCode() {
		return getErro().getCode();
	}

	@Override
	public String getMessage() {
		return getErro().getMessage();
	}

}
