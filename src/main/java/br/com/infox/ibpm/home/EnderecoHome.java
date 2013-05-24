/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informa��o Ltda.

 Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; vers�o 2 da Licen�a.
 Este programa � distribu�do na expectativa de que seja �til, por�m, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU 
 ADEQUA��O A UMA FINALIDADE ESPEC�FICA.
 
 Consulte a GNU GPL para mais detalhes.
 Voc� deve ter recebido uma c�pia da GNU GPL junto com este programa; se n�o, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.ibpm.home;

import java.util.Date;

import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.util.Strings;

import br.com.infox.ibpm.component.suggest.CepSuggestBean;
import br.com.infox.ibpm.entity.Cep;
import br.com.infox.ibpm.entity.Endereco;
import br.com.infox.access.entity.UsuarioLogin;
import br.com.itx.util.ComponentUtil;


@Name("enderecoHome")
@BypassInterceptors
@Install(precedence=Install.FRAMEWORK)
public class EnderecoHome extends AbstractEnderecoHome<Endereco> {

	private static final long serialVersionUID = 1L;

	private CepSuggestBean getCepSuggestBean() {
		return getComponent("cepSuggest");
	}
	
	public void setCep(Cep cep) {
		getCepSuggestBean().setInstance(cep);
	}
	
	public Cep getCep(){
		return getCepSuggestBean().getInstance();
	}
	
	/**
	 * M�todo que traz os dados de endere�o do CEP informado (se houver em base)
	 */
	@Observer("cepChangedEvent")
	public void setEndereco(Cep cep) {
		if (cep == null)
		{
			Contexts.removeFromAllContexts("cepSuggest");
			this.setInstance(new Endereco());
		}else{		
			this.getInstance().setCep(cep);
			this.getInstance().setNomeEstado(cep.getMunicipio().getEstado().getEstado());
			this.getInstance().setNomeCidade(cep.getMunicipio().getMunicipio());
			this.getInstance().setNomeLogradouro(cep.getNomeLogradouro());
			this.getInstance().setNomeBairro(cep.getNomeBairro());
		}
	}
	
	/**
	 * Fun��o para checar se o cep � null
	 * @return
	 */
	public boolean checkCep() {
		if (this.getInstance().getCep() != null){
			return true;
		}
		return false;
	}
	
	/**
	 * Fun��o que checa se existe algum campo de endere�o preenchido.
	 * (Se algum campo for diferente de vazio o campo cep passa a ser obrigat�rio)
	 * Caso todos os campos estiverem vazios ou nulls retorna true e o cep n�o ser� obrigat�rio
	 * Se os campos de endere�o forem iguais a null retorna false
	 * @return
	 */
	public boolean checkEndereco() {
		return checkEndereco(getInstance());
	}
	
	public boolean checkEndereco(Endereco endereco) {
		if ( (!Strings.isEmpty(endereco.getNomeLogradouro())) 
				|| (!Strings.isEmpty(endereco.getNomeBairro()))
				|| (!Strings.isEmpty(endereco.getComplemento()))){
				return true;
			} 
		return false;
	}
	
	@Override
	public void setId(Object id) {
		super.setId(id);
		if(isManaged()){
			if (getInstance().getCep() != null){
				getCepSuggestBean().setInstance(getInstance().getCep());
			}
		}
	}

	@Override
	protected Endereco createInstance() {
		setInstance(super.createInstance());
		getInstance().setCep(new Cep());
		return instance;
	}
	
	@Override
	public String persist() {
		UsuarioLogin pessoaLogada = (UsuarioLogin) 
					Contexts.getSessionContext().get("usuarioLogado");

		//setando data e hora da altera��o
		getInstance().setDataAlteracao(new Date());
		getInstance().setUsuarioCadastrador( pessoaLogada );
		String persist = "";
		if (getInstance().getCorrespondencia() == null){
			getInstance().setCorrespondencia(Boolean.FALSE);
		}
		if (checkCep()){
//			getInstance().setCodUf(getInstance().getCep().getMunicipio().getEstado().getCodEstado());
			persist = super.persist();
            refreshGrid("processoParteVinculoPessoaEnderecoGrid");
			Contexts.removeFromAllContexts("cepSuggest");
		}
		return persist;
	}
	
	@Override
	public String update() {
		String update = "";
		if (checkCep()){
//			getInstance().setCodUf(getInstance().getCep().getMunicipio().getEstado().getCodEstado());
			//setando data e hora da altera��o
			getInstance().setDataAlteracao(new Date());
			UsuarioLogin pessoaLogada = (UsuarioLogin) 
					Contexts.getSessionContext().get("usuarioLogado");

			getInstance().setUsuarioCadastrador( pessoaLogada );

			update = super.update();
		}	
		return update;
	}	
	
	public static EnderecoHome instance() {
		return ComponentUtil.getComponent("enderecoHome");
	}

	@Override
	 public void newInstance() {
	  Contexts.removeFromAllContexts("cepSuggest");
	  super.newInstance();
	 }
	
}