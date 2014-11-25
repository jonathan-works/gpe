package br.com.infox.epp.processo.comunicacao.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;

@Name(ExpedicaoComunicacaoList.NAME)
@Scope(ScopeType.PAGE)
public class ExpedicaoComunicacaoList extends EntityList<ModeloComunicacao> {
	private static final long serialVersionUID = 1L;
	public static final String NAME = "expedicaoComunicacaoList";
	
	private static final String DEFAULT_EJBQL = "select o from ModeloComunicacao o where o.finalizada = true and "
			+ "o.localizacaoResponsavelAssinatura = #{authenticator.getUsuarioPerfilAtual().localizacao} and "
			+ "(o.perfilResponsavelAssinatura is null or o.perfilResponsavelAssinatura = #{authenticator.getUsuarioPerfilAtual().perfilTemplate})";
	
	private static final String DEFAULT_ORDER = "id";

	@Override
	protected void addSearchFields() {
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
