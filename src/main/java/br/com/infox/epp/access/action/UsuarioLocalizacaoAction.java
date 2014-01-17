package br.com.infox.epp.access.action;


import static br.com.infox.core.constants.WarningConstants.UNCHECKED;
import static br.com.infox.epp.access.query.UsuarioLocalizacaoQuery.ESTRUTURA_CONDITION;
import static br.com.infox.epp.access.query.UsuarioLocalizacaoQuery.ESTRUTURA_NULL_CONDITION;
import static br.com.infox.epp.access.query.UsuarioLocalizacaoQuery.EXISTE_USUARIO_LOCALIZACAO_QUERY;
import static br.com.infox.epp.access.query.UsuarioLocalizacaoQuery.PARAM_ESTRUTURA;
import static br.com.infox.epp.access.query.UsuarioLocalizacaoQuery.PARAM_LOCALIZACAO;
import static br.com.infox.epp.access.query.UsuarioLocalizacaoQuery.PARAM_PAPEL;
import static br.com.infox.epp.access.query.UsuarioLocalizacaoQuery.PARAM_USUARIO;

import java.util.HashMap;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.core.tree.AbstractTreeHandler;
import br.com.infox.epp.access.component.tree.LocalizacaoEstruturaTreeHandler;
import br.com.infox.epp.access.component.tree.PapelTreeHandler;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.UsuarioLocalizacao;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.UsuarioLocalizacaoManager;

@Name(UsuarioLocalizacaoAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class UsuarioLocalizacaoAction extends AbstractCrudAction<UsuarioLocalizacao>{
	public static final String NAME = "usuarioLocalizacaoAction";
	
	@In
	private UsuarioLocalizacaoManager usuarioLocalizacaoManager;
	
	private UsuarioLogin usuarioGerenciado;
	
	@SuppressWarnings(UNCHECKED)
    private void limparArvores() {
	    clearTree((AbstractTreeHandler<Localizacao>) Component.getInstance(LocalizacaoEstruturaTreeHandler.NAME));
        clearTree((AbstractTreeHandler<Papel>) Component.getInstance(PapelTreeHandler.NAME));
	}
	
	private <T> void clearTree(final AbstractTreeHandler<T> handler) {
	    if (handler != null) {
	        handler.clearTree();
	    }
	}
	
	@Override
	public void newInstance() {
	    super.newInstance();
		final UsuarioLocalizacao instance = getInstance();
		instance.setResponsavelLocalizacao(Boolean.FALSE);
		instance.setUsuario(usuarioGerenciado);
		limparArvores();
	}
	
	public boolean existeUsuarioLocalizacao(final UsuarioLocalizacao usuarioLocalizacao) {
        final StringBuilder hql = new StringBuilder(EXISTE_USUARIO_LOCALIZACAO_QUERY);
        if (usuarioLocalizacao.getEstrutura() != null) {
            hql.append(ESTRUTURA_CONDITION);
        } else {
            hql.append(ESTRUTURA_NULL_CONDITION);
        }
        
        final HashMap<String, Object> params = new HashMap<>();
        params.put(PARAM_USUARIO, usuarioLocalizacao.getUsuario());
        params.put(PARAM_PAPEL, usuarioLocalizacao.getPapel());
        params.put(PARAM_LOCALIZACAO, usuarioLocalizacao.getLocalizacao());
        if (usuarioLocalizacao.getEstrutura() != null) {
            params.put(PARAM_ESTRUTURA, usuarioLocalizacao.getEstrutura());
        }
        
        return (Long) getGenericManager().getSingleResult(hql.toString(), params) > 0;
    }
	
	@Override
	protected boolean beforeSave() {
	    return !existeUsuarioLocalizacao(getInstance());
	}
	
	@Override
	protected void afterSave(String ret) {
	    if (PERSISTED.equals(ret)) {
	        newInstance();
	    }
	}
	
	@Override
	public String remove(final UsuarioLocalizacao usuarioLocalizacao) {
        setInstance(usuarioLocalizacao);
	    final String ret = super.remove(getInstance());
		newInstance();
		return ret;
	}

    public UsuarioLogin getUsuarioGerenciado() {
        return usuarioGerenciado;
    }

    public void setUsuarioGerenciado(final UsuarioLogin usuarioGerenciado) {
        this.usuarioGerenciado = usuarioGerenciado;
        getInstance().setUsuario(usuarioGerenciado);
    }
}
