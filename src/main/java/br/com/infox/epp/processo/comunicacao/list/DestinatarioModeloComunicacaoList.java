package br.com.infox.epp.processo.comunicacao.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;

@Name(DestinatarioModeloComunicacaoList.NAME)
@Scope(ScopeType.PAGE)
@AutoCreate
public class DestinatarioModeloComunicacaoList extends EntityList<DestinatarioModeloComunicacao> {
	private static final long serialVersionUID = 1L;
	public static final String NAME = "destinatarioModeloComunicacaoList";
	
	private static final String DEFAULT_EJBQL = "select o from DestinatarioModeloComunicacao o where "
			+ "o.modeloComunicacao = #{destinatarioModeloComunicacaoList.modeloComunicacao}";
	private static final String DEFAULT_ORDER = "id";
	
	private ModeloComunicacao modeloComunicacao;

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

	public ModeloComunicacao getModeloComunicacao() {
		return modeloComunicacao;
	}
	
	public void setModeloComunicacao(ModeloComunicacao modeloComunicacao) {
		this.modeloComunicacao = modeloComunicacao;
	}
}
