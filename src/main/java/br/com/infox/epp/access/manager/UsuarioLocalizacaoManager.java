package br.com.infox.epp.access.manager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.dao.UsuarioLocalizacaoDAO;
import br.com.infox.epp.access.entity.UsuarioLocalizacao;

@Name(UsuarioLocalizacaoManager.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class UsuarioLocalizacaoManager extends GenericManager {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "usuarioLocalizacaoManager";
	
	@In
	private UsuarioLocalizacaoDAO usuarioLocalizacaoDAO;
	
	public boolean existeUsuarioLocalizacao(UsuarioLocalizacao usuarioLocalizacao) {
		return usuarioLocalizacaoDAO.existeUsuarioLocalizacao(usuarioLocalizacao);
	}
	
	@Override
	public <T> T persist(T o) throws DAOException {
		return usuarioLocalizacaoDAO.persist(o);
	}
}
