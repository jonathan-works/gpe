package br.com.infox.epp.access.dao;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.access.entity.UsuarioPerfil;

@Name(UsuarioPerfilDAO.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class UsuarioPerfilDAO extends DAO<UsuarioPerfil> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "usuarioPerfilDAO";

}
