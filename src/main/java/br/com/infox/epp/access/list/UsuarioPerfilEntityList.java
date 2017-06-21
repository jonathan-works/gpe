package br.com.infox.epp.access.list;

import static br.com.infox.core.list.SearchCriteria.IGUAL;

import java.util.Map;

import javax.inject.Named;

import br.com.infox.core.list.EntityList;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.cdi.ViewScoped;

@Named
@ViewScoped
public class UsuarioPerfilEntityList extends EntityList<UsuarioPerfil> {
    
    private static final long serialVersionUID = 1L;
    
    private static final String DEFAULT_EJBQL = "select o from UsuarioPerfil o where o.ativo = true ";
    private static final String DEFAULT_ORDER = "perfilTemplate.descricao";

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
