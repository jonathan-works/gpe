package br.com.infox.ibpm.component.tree;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;

import br.com.infox.access.entity.Papel;
import br.com.infox.component.tree.AbstractTreeHandler;
import br.com.infox.component.tree.EntityNode;
import br.com.infox.ibpm.entity.UsuarioLocalizacao;

@Name("papelTree")
@Scope(ScopeType.CONVERSATION)
@BypassInterceptors
public class PapelTreeHandler extends AbstractTreeHandler<Papel> {

	public static final String PAPEL_TREE_EVENT = "papelTreeHandlerSelected";

	private static final long serialVersionUID = 1L;

	private static final String QUERY_PAPEIS = "select grupo from Papel p " +
			"join p.grupos grupo " +
			"where p = :" + EntityNode.PARENT_NODE + 
			" and grupo.identificador not like '/%' order by grupo.nome";
	
	@Override
	protected String getQueryChildren() {
		return QUERY_PAPEIS;
	}

	@Override
	protected String getQueryRoots() {
		Papel papel = getPapelAtual();
		StringBuilder sb = new StringBuilder();	
		sb.append("select grupo from Papel p ");
		sb.append("right join p.grupos grupo where grupo.identificador ");
		sb.append("not like '/%' and p is null ");
		if (papel != null) {
			sb.append(" and grupo.idPapel = ");
			sb.append(papel.getIdPapel()).append(" ");
		} 
		sb.append("order by grupo.nome");
		return sb.toString();
	}
	
	protected Papel getPapelAtual() {
		UsuarioLocalizacao usuarioLocalizacao = (UsuarioLocalizacao) Contexts
				.getSessionContext().get("usuarioLogadoLocalizacaoAtual");
		if (usuarioLocalizacao != null) {
			return usuarioLocalizacao.getPapel();
		}
		return null;
	}

	@Override
	public List<EntityNode<Papel>> getRoots() {
		List<EntityNode<Papel>> nodes = new ArrayList<EntityNode<Papel>>();
		String[] hql = new String[]{getQueryChildren()};
		EntityNode<Papel> node = new EntityNode<Papel>(null, getPapelAtual(), hql);
		nodes.add(node);
		return nodes;
	}
	
	@Override
	protected String getEventSelected() {
		return PAPEL_TREE_EVENT;
	}

}