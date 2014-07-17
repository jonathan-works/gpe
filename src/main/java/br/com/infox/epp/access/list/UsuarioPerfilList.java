package br.com.infox.epp.access.list;

import static br.com.infox.core.list.SearchCriteria.*;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.epp.access.entity.UsuarioPerfil;

@Name(UsuarioPerfilList.NAME)
@Scope(ScopeType.PAGE)
public class UsuarioPerfilList extends EntityList<UsuarioPerfil> {
    
    private static final long serialVersionUID = 1L;
    public static final String NAME = "usuarioPerfilList";
    
    private static final String DEFAULT_EJBQL = "select o from UsuarioPerfil o";
    private static final String DEFAULT_ORDER = "perfil.descricao";

    @Override
    protected void addSearchFields() {
        addSearchField("usuarioLogin", IGUAL);
    }

    @Override
    protected String getDefaultEjbql() {
        return DEFAULT_EJBQL;
    }

    @Override
    protected String getDefaultOrder() {
        return DEFAULT_ORDER;
    }

    @Override
    protected Map<String, String> getCustomColumnsOrder() {
        return null;
    }

}
