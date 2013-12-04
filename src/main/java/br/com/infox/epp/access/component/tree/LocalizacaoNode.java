package br.com.infox.epp.access.component.tree;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import br.com.infox.core.tree.EntityNode;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Localizacao;

public class LocalizacaoNode extends LocalizacaoNodeSearch {

	private static final long serialVersionUID = 1L;

	public LocalizacaoNode(String[] queryChildrenList) {
		super(queryChildrenList);
	}

	public LocalizacaoNode(LocalizacaoNode localizacaoNode, Localizacao n,
			String[] queryChildren) {
		super(localizacaoNode, n, queryChildren);
	}

	@Override
	public boolean canSelect() {
		return checkPermissaoLocalizacao(this);
	}
	
	private boolean checkPermissaoLocalizacao(final EntityNode<Localizacao> node) {
        final Localizacao locAtual = Authenticator.getUsuarioLocalizacaoAtual().getLocalizacao();
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
	
	@Override
	protected EntityNode<Localizacao> createChildNode(Localizacao n) {
		LocalizacaoNode node = new LocalizacaoNode(this, n, getQueryChildren());
		node.setShowEstrutura(getShowEstrutura());
		return node;
	}
	
	@Override
	protected EntityNode<Localizacao> createRootNode(Localizacao n) {
		LocalizacaoNode node = new LocalizacaoNode(null, n, getQueryChildren());
		node.setShowEstrutura(getShowEstrutura());
		return node;
	}	
	
	@Override
	public List<EntityNode<Localizacao>> getRoots(Query queryRoots) {
		Localizacao estrutura = Authenticator.getUsuarioLocalizacaoAtual().getEstrutura();
		if (estrutura != null) {
			List<EntityNode<Localizacao>> nodes = new ArrayList<EntityNode<Localizacao>>();
			EntityNode<Localizacao> no = new LocalizacaoNode(null, estrutura, getQueryChildren());
			nodes.add(no);
			return nodes;
		} 
		return super.getRoots(queryRoots);
	}
}