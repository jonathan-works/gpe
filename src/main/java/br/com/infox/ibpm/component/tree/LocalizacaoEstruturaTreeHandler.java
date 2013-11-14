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
package br.com.infox.ibpm.component.tree;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.core.Events;

import br.com.infox.component.tree.AbstractTreeHandler;
import br.com.infox.component.tree.EntityNode;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.UsuarioLocalizacao;
import br.com.infox.ibpm.home.UsuarioHome;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.EntityUtil;


@Name("localizacaoEstruturaTree")
@BypassInterceptors
public class LocalizacaoEstruturaTreeHandler extends AbstractTreeHandler<Localizacao> {

	private static final long serialVersionUID = 1L;
	private UsuarioLocalizacao usuarioLocalizacaoAtual;
	
	@Override
	protected String getQueryRoots() {
		StringBuilder sb = new StringBuilder("select n from Localizacao n ");
		sb.append("where ");
		Localizacao loc = getUsuarioLocalizacaoAtual().getEstrutura() != null ? 
				getUsuarioLocalizacaoAtual().getEstrutura() : getUsuarioLocalizacaoAtual().getLocalizacao();
		sb.append(" n.idLocalizacao = " + loc.getIdLocalizacao());
		sb.append(" ");
		sb.append("order by localizacao");
		return sb.toString();
	}
	
	@Override
	protected String getQueryChildren() {
		return "select n from Localizacao n where localizacaoPai = :" +
			EntityNode.PARENT_NODE;
	}
	
	@Override
	protected String getEventSelected() {
		return null;
	}

	@Override
	protected Localizacao getEntityToIgnore() {
		return ComponentUtil.getInstance("localizacaoHome");
	}
	
	@Override
	protected EntityNode<Localizacao> createNode() {
		return new LocalizacaoNode(getQueryChildrenList());
	}
	
	@Override
	protected void raiseEvents(EntityNode<Localizacao> en) {
		Events.instance().raiseEvent("evtSelectLocalizacaoEstrutura", getSelected(), getEstrutura(en));
	}
	
	private Localizacao getEstrutura(EntityNode<Localizacao> en) {
		EntityNode<Localizacao> parent = en.getParent();
		while (parent != null) {
			if (parent.getEntity().getEstruturaFilho() != null) {
				return parent.getEntity();
			}
			parent = parent.getParent();
		}
		return null;
	}

	public void setUsuarioLocalizacaoAtual(UsuarioLocalizacao usuarioLocalizacaoAtual) {
		this.usuarioLocalizacaoAtual = usuarioLocalizacaoAtual;
	}

	public UsuarioLocalizacao getUsuarioLocalizacaoAtual() {
		if (usuarioLocalizacaoAtual == null) {
			usuarioLocalizacaoAtual = UsuarioHome.getUsuarioLocalizacaoAtual();
			usuarioLocalizacaoAtual = EntityUtil.getEntityManager().find(UsuarioLocalizacao.class, usuarioLocalizacaoAtual.getIdUsuarioLocalizacao());
		}
		return usuarioLocalizacaoAtual;
	}
	
	@Override
	public void clearTree() {
		super.clearTree();
		usuarioLocalizacaoAtual = null;
	}

}