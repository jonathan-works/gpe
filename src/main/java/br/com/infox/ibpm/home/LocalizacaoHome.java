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

import java.util.List;

import javax.faces.event.AbortProcessingException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.util.Strings;
import org.richfaces.event.ItemChangeEvent;

import br.com.infox.component.tree.EntityNode;
import br.com.infox.component.tree.SearchTree2GridList;
import br.com.infox.ibpm.component.tree.LocalizacaoEstruturaSearchTreeHandler;
import br.com.infox.ibpm.component.tree.LocalizacaoTreeHandler;
import br.com.infox.ibpm.entity.Cep;
import br.com.infox.ibpm.entity.Endereco;
import br.com.infox.ibpm.entity.Estado;
import br.com.infox.ibpm.entity.Localizacao;
import br.com.infox.ibpm.entity.Municipio;
import br.com.infox.ibpm.manager.LocalizacaoManager;
import br.com.itx.util.ComponentUtil;


@Name(LocalizacaoHome.NAME)
public class LocalizacaoHome
		extends AbstractLocalizacaoHome<Localizacao>{

	public static final String NAME = "localizacaoHome";
	
	private static final long serialVersionUID = 1L;
	private Localizacao localizacaoPai;
	private SearchTree2GridList<Localizacao> searchTree2GridList;
	private boolean mostrarSomenteEstrutura = false;
	private boolean mostrarSomenteEstruturaAnterior = false;
	private String cnpj = "0";
	
	@In private LocalizacaoManager localizacaoManager;
	
	
	public Localizacao getLocalizacaoPai() {
		return localizacaoPai;
	}

	public void setLocalizacaoPai(Localizacao localizacaoPai) {
		this.localizacaoPai = localizacaoPai;
	}

	@Override
	public void newInstance() {
		getInstance().setAtivo(Boolean.TRUE);
		limparTrees();
		Contexts.removeFromAllContexts("cepSuggest");	
		super.newInstance();
	}
	
	public static LocalizacaoHome instance() {
		return ComponentUtil.getComponent("localizacaoHome");
	}
	
	private void limparTrees(){
		LocalizacaoTreeHandler ret1 = getComponent("localizacaoSearchTree");
		LocalizacaoTreeHandler ret2 = getComponent("localizacaoTree");
		ret1.clearTree();
		ret2.clearTree();
		if(getLockedFields().contains("localizacaoPai")) ret2.clearTree();
		if(searchTree2GridList != null) {
			searchTree2GridList.refreshTreeList();
			searchTree2GridList = null;
		}
		localizacaoPai = null;
	}
	
	@Override
	protected Localizacao createInstance() {
		instance = super.createInstance();
		instance.setLocalizacaoPai(new Localizacao());
		getEnderecoHome().newInstance();
		return instance;
	}
	
	@Override
	public String persist() {
		String ret = null;
		try{
			ret = super.persist();
			if (ret != null) {
				limparTrees();
			}
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		} 
		return ret;	
	}
	
	private void verificaListas(){
		/*
		 * Verifica se o pai atual e o pai selecionado são diferentes de nulo e
		 * se os dois são diferentes um do outro e remove o registro da lista 
		 * do pai atual e insere na lista do pai selecionado.
		 */
		if ((getInstance().getLocalizacaoPai() != null) && 
			(localizacaoPai != null) &&
			(!getInstance().getLocalizacaoPai().getLocalizacao().equals(localizacaoPai.getLocalizacao()))){
			
			getInstance().getLocalizacaoPai().getLocalizacaoList().remove(getInstance());
			localizacaoPai.getLocalizacaoList().add(getInstance());
			localizacaoPai.getLocalizacaoList();
		}
		/*
		 * Se o pai atual não for nulo e o pai selecionado for, o registro é
		 * excluido da lista do pai atual.
		 */
		if ((getInstance().getLocalizacaoPai() != null) && (localizacaoPai == null)){
			getInstance().getLocalizacaoPai().getLocalizacaoList().remove(getInstance());
		}
		/*
		 * Se o pai atual for nulo e o pai selecionado não for, o registro é 
		 * adicionado à lista do pai atual.
		 */
		if ((getInstance().getLocalizacaoPai() == null) && (localizacaoPai != null)) {
			localizacaoPai.getLocalizacaoList().add(getInstance());
			getInstance().setLocalizacaoPai(localizacaoPai);
		}
	}
	
	@Override
	public String update() {
		verificaListas();
		/*
		 * Se o registro estiver como inativo na hora do update, todos os seus
		 * filhos serão inativados
		 */
		if (!getInstance().getAtivo()) {
			inactiveRecursive(getInstance());
			return "updated";
		}
		String ret = null;
		try {
			ret = super.update();
			if (ret != null) {
				limparTrees();
			}
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return ret;
	}
	
	@Override
	protected boolean beforePersistOrUpdate() {
		getInstance().setLocalizacaoPai(localizacaoPai);
		if (getInstance().getEstrutura()) {
			instance.setEstruturaFilho(null);
			instance.setLocalizacaoPai(null);
			localizacaoPai = null;
			limparTrees();
		}
		if ((getEnderecoHome().checkEndereco()) &&
			(getEnderecoHome().getCep() != null)) {
			Endereco endereco = getEnderecoHome().getInstance();
			endereco = getEntityManager().merge(endereco);
			Cep cep = endereco.getCep();
			cep = getEntityManager().merge(cep);
			Municipio municipio = cep.getMunicipio();
			municipio = getEntityManager().merge(municipio);
			Estado estado = municipio.getEstado();
			getEntityManager().merge(estado);
			getInstance().setEndereco(endereco);
		} else {
			getInstance().setEndereco(null);
			if (getEnderecoHome().checkEndereco()) {
				FacesMessages.instance().add(StatusMessage.Severity.ERROR,
						"Cep obrigatório");
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Verifica se a localização está vinculada a algum ItemTIpoDocumento. Se
	 * não estiver, realiza a inativação em cascata
	 */
	public String inactiveRecursive(Localizacao localizacao) {
		if (localizacao.getItemTipoDocumentoList().size() <= 0) {
			if (localizacao.getLocalizacaoList().size() > 0) {
				inativarFilhos(localizacao);
			}
			localizacao.setAtivo(Boolean.FALSE);
			String ret = super.update();
			limparTrees();
			return ret;
		}
		FacesMessages.instance().add(StatusMessage.Severity.ERROR,
				"Registro está em uso não poderá ser excluido!");
		return "False";
	}

	private void inativarFilhos(Localizacao localizacao){
		localizacao.setAtivo(Boolean.FALSE);
		
		Integer quantidadeFilhos = localizacao.getLocalizacaoList().size();
		for (int i = 0; i < quantidadeFilhos; i++) {
			inativarFilhos(localizacao.getLocalizacaoList().get(i));
		}
	}

	@Override
	public void onClickFormTab() {
		if (isManaged()) {
			localizacaoPai = getInstance().getLocalizacaoPai();
		} else {
			localizacaoPai = null;
		}
	}
	
	public void instanciarLocalizacaoPai(){
		if (isManaged()) {
			localizacaoPai = getInstance().getLocalizacaoPai();
		} else {
			localizacaoPai = null;
		}
	}
	
	private EnderecoHome getEnderecoHome() {
		return getComponent("enderecoHome");
	}

	@Override
	public void setId(Object id) {
		boolean changed = id != null && !id.equals(getId());
		super.setId(id);
		if(isManaged() && getInstance().getEndereco() != null){
			getEnderecoHome().setId(getInstance().getEndereco().getIdEndereco());
		} else if ((changed && getInstance().getEndereco() == null) || id == null) {
			getEnderecoHome().newInstance();
		}
		if (isManaged() && changed) {
			localizacaoPai = getInstance().getLocalizacaoPai();
		}
		if (id == null) {
			localizacaoPai = null;
		}
	}
	
	/**
	 * Grava o registro atual e seta o localizacaoPai do próximo com o valor
	 * da localização do último registro inserido.
	 */
	public String persistAndNext() {
		String outcome = null;
		try{
			outcome = persist();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		} 
		if (outcome != null){
			if (!outcome.equals("")) {
				Localizacao me = getInstance();
				newInstance();
				getInstance().setLocalizacaoPai(me);
				getEntityManager().flush();
				localizacaoPai = getInstance().getLocalizacaoPai();
				getInstance().setLocalizacaoPai(localizacaoPai);
			}
		}
		return outcome;
	}

	private LocalizacaoEstruturaSearchTreeHandler getLocalizacaoEstruturaSearch() {
		return getComponent("localizacaoEstruturaSearchTreeHandler", ScopeType.CONVERSATION);
	}

	public void setCnpj(String cnpj) {
		this.cnpj = Strings.isEmpty(cnpj) ? "0" : cnpj;
	}

	public String getCnpj() {
		return cnpj;
	}

	public void setMostrarSomenteEstrutura(boolean mostrarSomenteEstrutura) {
		this.mostrarSomenteEstrutura = mostrarSomenteEstrutura;
		if (mostrarSomenteEstruturaAnterior != mostrarSomenteEstrutura) {
			mostrarSomenteEstruturaAnterior = mostrarSomenteEstrutura;
			getLocalizacaoEstruturaSearch().clearTree();
		}
	}

	public boolean getMostrarSomenteEstrutura() {
		return mostrarSomenteEstrutura;
	}	
	
	public boolean possuiParenteEstrutura() {
		if (!isIdDefined()) {
			return false;
		}
		Localizacao pai = getInstance().getLocalizacaoPai();
		while (pai != null) {
			if (pai.getEstrutura()) {
				return true;
			}
			pai = pai.getLocalizacaoPai();
		}
		return false;
	}
	
	public boolean canSelect(EntityNode<Localizacao> node) {
		System.out.println(node);
		return true;
	}

	public boolean checkPermissaoLocalizacao(EntityNode<Localizacao> node) {
		Localizacao locAtual = UsuarioHome.getUsuarioLocalizacaoAtual()
				.getLocalizacao();
		if (node.getEntity().getIdLocalizacao() == locAtual.getIdLocalizacao()) {
			return true;
		}
		EntityNode<Localizacao> nodePai = node.getParent();
		while (nodePai != null) {
			if (nodePai.getEntity().getIdLocalizacao() == locAtual.getIdLocalizacao()) {
				return true;
			}
			nodePai = nodePai.getParent();
		}
		return false;
	}
	
	public List<Localizacao> getLocalizacoesEstrutura(){
		return localizacaoManager.getLocalizacoesEstrutura();
	}

}