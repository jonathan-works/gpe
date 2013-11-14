package br.com.infox.epp.access.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.access.entity.UsuarioLocalizacao;

@Name(UsuarioLocalizacaoList.NAME)
@Scope(ScopeType.CONVERSATION)
public class UsuarioLocalizacaoList extends EntityList<UsuarioLocalizacao> {

	private static final long serialVersionUID = 1L;
	// Não pode ser usuarioLocalizacaoList por causa da variável de sessão que o Authenticator cria
	public static final String NAME = "usuarioLocalizacaoEntityList";
	private static final String DEFAULT_EJBQL = "select o from UsuarioLocalizacao o";
	private static final String DEFAULT_ORDER = "o.localizacao";
	
	@Override
	protected void addSearchFields() {
		addSearchField("usuario", SearchCriteria.IGUAL);
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
