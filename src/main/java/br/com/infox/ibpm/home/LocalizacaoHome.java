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

import org.jboss.seam.Component;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;

import br.com.infox.component.tree.EntityNode;
import br.com.infox.epp.documento.entity.ItemTipoDocumento;
import br.com.infox.ibpm.component.tree.LocalizacaoTreeHandler;
import br.com.infox.ibpm.entity.Localizacao;
import br.com.infox.ibpm.entity.UsuarioLocalizacao;
import br.com.infox.ibpm.manager.LocalizacaoManager;
import br.com.itx.component.AbstractHome;
import br.com.itx.util.ComponentUtil;


@Name(LocalizacaoHome.NAME)
public class LocalizacaoHome
		extends AbstractHome<Localizacao>{

	public static final String NAME = "localizacaoHome";
	
	private static final long serialVersionUID = 1L;
	private boolean mostrarSomenteEstrutura = false;
	private boolean mostrarSomenteEstruturaAnterior = false;
	
	@In private LocalizacaoManager localizacaoManager;
	
	@Override
	public void newInstance() {
		getInstance().setAtivo(Boolean.TRUE);
		limparTrees();	
		super.newInstance();
	}
	
	public static LocalizacaoHome instance() {
		return ComponentUtil.getComponent(LocalizacaoHome.NAME);
	}
	
	private void limparTrees(){
		final LocalizacaoTreeHandler ret = (LocalizacaoTreeHandler) Component.getInstance(LocalizacaoTreeHandler.NAME);
		ret.clearTree();
	}
	
	@Override
	public String persist() {
		String ret = super.persist();
		if (AbstractHome.PERSISTED.equals(ret)) {
			limparTrees();
		}
		return ret;	
	}
	
	@Override
	public String update() {
		/*
		 * Se o registro estiver como inativo na hora do update, todos os seus
		 * filhos serão inativados
		 */
		if (!getInstance().getAtivo()) {
			inactiveRecursive(instance);
			return AbstractHome.UPDATED;
		}
		return super.update();
	}
	
	@Override
	protected boolean beforePersistOrUpdate() {
		if (getInstance().getEstrutura()) {
			instance.setEstruturaFilho(null);
			instance.setLocalizacaoPai(null);
		}
		return true;
	}	
	
	/**
	 * Verifica se a localização está vinculada a algum ItemTIpoDocumento. Se
	 * não estiver, realiza a inativação em cascata
	 */
	public String inactiveRecursive(Localizacao localizacao) {
	    String ret = null;
		if (localizacao.getItemTipoDocumentoList().size() <= 0) {
			ret = inativarFilhos(localizacao);
		} else {
		    FacesMessages.instance().add(StatusMessage.Severity.ERROR,
		            "Registro está em uso não poderá ser excluido!");
		}
		return ret;
	}

	private String inativarFilhos(Localizacao localizacao){
	    inactive(localizacao);
		for (int i = 0, quantidadeFilhos = localizacao.getLocalizacaoList().size(); i < quantidadeFilhos; i++) {
			inativarFilhos(localizacao.getLocalizacaoList().get(i));
		}
		return AbstractHome.UPDATED;
	}
	
	/**
	 * Grava o registro atual e seta o localizacaoPai do próximo com o valor
	 * da localização do último registro inserido.
	 */
	public String persistAndNext() {
		String outcome = persist();
		if (AbstractHome.PERSISTED.equals(outcome)){
			Localizacao me = getInstance();
			newInstance();
			getInstance().setLocalizacaoPai(me);
		}
		return outcome;
	}

	public void setMostrarSomenteEstrutura(boolean mostrarSomenteEstrutura) {
		this.mostrarSomenteEstrutura = mostrarSomenteEstrutura;
		if (mostrarSomenteEstruturaAnterior != mostrarSomenteEstrutura) {
			mostrarSomenteEstruturaAnterior = mostrarSomenteEstrutura;
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
	
	//Vindo do antigo AbstractLocalizacaoHome
	
	   public void setLocalizacaoIdLocalizacao(Integer id) {
	        setId(id);
	    }

	    public Integer getLocalizacaoIdLocalizacao() {
	        return (Integer) getId();
	    }

	    @Override
	    public String remove(Localizacao obj) {
	        setInstance(obj);
	        final String ret = super.remove();
	        newInstance();
	        return ret;
	    }

	    public List<ItemTipoDocumento> getItemTipoDocumentoList() {
	        return getInstance() == null ? null : getInstance()
	                .getItemTipoDocumentoList();
	    }
	    
	    public List<Localizacao> getLocalizacaoList(){
	        return getInstance() == null ? null : getInstance().getLocalizacaoList();
	    }
	    
	    public List<UsuarioLocalizacao> getUsuarioLocalizacaoList() {
	        return getInstance() == null ? null : getInstance()
	                .getUsuarioLocalizacaoList();
	    }
	    
}