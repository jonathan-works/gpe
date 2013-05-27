/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.

 Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; versão 2 da Licença.
 Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 
 Consulte a GNU GPL para mais detalhes.
 Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
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
	 * Método que traz os dados de endereço do CEP informado (se houver em base)
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
	 * Função para checar se o cep é null
	 * @return
	 */
	public boolean checkCep() {
		if (this.getInstance().getCep() != null){
			return true;
		}
		return false;
	}
	
	/**
	 * Função que checa se existe algum campo de endereço preenchido.
	 * (Se algum campo for diferente de vazio o campo cep passa a ser obrigatório)
	 * Caso todos os campos estiverem vazios ou nulls retorna true e o cep não será obrigatório
	 * Se os campos de endereço forem iguais a null retorna false
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

		//setando data e hora da alteração
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
			//setando data e hora da alteração
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