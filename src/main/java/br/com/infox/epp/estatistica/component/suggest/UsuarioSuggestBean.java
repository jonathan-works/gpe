package br.com.infox.epp.estatistica.component.suggest;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.suggest.AbstractSuggestBean;
import br.com.infox.epp.access.entity.UsuarioLogin;

@Name(UsuarioSuggestBean.NAME)
@Scope(ScopeType.PAGE)
public class UsuarioSuggestBean extends AbstractSuggestBean<UsuarioLogin> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "usuarioSuggestBean";

	@Override
	public UsuarioLogin load(Object id) {
		return entityManager.find(UsuarioLogin.class, id);
	}

	@Override
	public String getEjbql() {
		StringBuilder sb = new StringBuilder();
		sb.append("select new br.com.infox.componentes.suggest.SuggestItem(o.idUsuarioLogin, o.nomeUsuario) from UsuarioLogin o ");
		sb.append("where lower(o.nomeUsuario) like lower(concat (:");
		sb.append(INPUT_PARAMETER);
		sb.append(", '%')) ");
		sb.append("order by o.nomeUsuario");
		return sb.toString();
	}
}
