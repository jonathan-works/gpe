package br.com.infox.epp.access.manager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.access.dao.UsuarioLocalizacaoDAO;
import br.com.infox.epp.access.entity.UsuarioLocalizacao;

@Name(UsuarioLocalizacaoManager.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class UsuarioLocalizacaoManager extends Manager<UsuarioLocalizacaoDAO, UsuarioLocalizacao> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "usuarioLocalizacaoManager";
	
	public boolean existeUsuarioLocalizacao(UsuarioLocalizacao usuarioLocalizacao) {
		return getDao().existeUsuarioLocalizacao(usuarioLocalizacao);
	}
}
