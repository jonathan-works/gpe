package br.com.infox.epp.estatistica.component.suggest;

import javax.inject.Named;

import br.com.infox.core.suggest.AbstractSuggestBean;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.cdi.ViewScoped;

@Named
@ViewScoped
public class UsuarioSuggestBean extends AbstractSuggestBean<UsuarioLogin> {

    private static final long serialVersionUID = 1L;

    @Override
    public UsuarioLogin load(Object id) {
        return entityManager.find(UsuarioLogin.class, id);
    }

    @Override
    public String getEjbql(String typed) {
        StringBuilder sb = new StringBuilder();
        sb.append("select new br.com.infox.componentes.suggest.SuggestItem(o.idUsuarioLogin, o.nomeUsuario) from UsuarioLogin o ");
        sb.append("where lower(o.nomeUsuario) like lower(concat (:");
        sb.append(INPUT_PARAMETER);
        sb.append(", '%')) ");
        sb.append("order by o.nomeUsuario");
        return sb.toString();
    }
}
