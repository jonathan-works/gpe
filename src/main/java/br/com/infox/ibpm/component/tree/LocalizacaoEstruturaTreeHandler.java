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
package br.com.infox.ibpm.component.tree;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.core.Events;
import org.richfaces.component.UITree;
import org.richfaces.event.TreeSelectionChangeEvent;

import br.com.infox.component.tree.AbstractTreeHandler;
import br.com.infox.component.tree.EntityNode;
import br.com.infox.ibpm.entity.Localizacao;
import br.com.infox.ibpm.entity.UsuarioLocalizacao;
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
	public void processTreeSelectionChange(TreeSelectionChangeEvent ev) {
		UITree tree = (UITree) ev.getSource();
		treeId = tree.getId();
		@SuppressWarnings("unchecked")
		EntityNode<Localizacao> en = (EntityNode<Localizacao>) tree.getData(); 
		setSelected(en.getEntity());
		closeParentPanel(tree);
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