package br.com.infox.epp.ws.exception;

/**
 * Interface utilizada para definir exceções utilizadas nos serviços
 * @author paulo
 *
 */
public interface ExcecaoServico {
	
	/**
	 * Define um erro ocorrido no serviço
	 * @author paulo
	 *
	 */
	public static interface ErroServico
	{
		public String getCodigo();
		public String getMensagem();		
	}
	
	/**
	 * Implementaçãop simples de {@link ErroServico}
	 * @author paulo
	 *
	 */
	public static class ErroServicoImpl implements ErroServico {

		private String codigo;
		private String mensagem;
		
		public ErroServicoImpl(String codigo, String mensagem) {
			super();
			this.codigo = codigo;
			this.mensagem = mensagem;
		}

		
		@Override
		public String getCodigo() {
			return this.codigo;
		}

		@Override
		public String getMensagem() {
			return this.mensagem;
		}
	}
		
	public ErroServico getErro();
}
