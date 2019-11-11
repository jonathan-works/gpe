package br.com.infox.epp.processo.comunicacao.list;

import java.util.Map;

import javax.inject.Named;

import br.com.infox.core.list.DataList;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;

@Named
@ViewScoped
public class DestinatarioModeloComunicacaoList extends DataList<DestinatarioModeloComunicacao> {
	private static final long serialVersionUID = 1L;
	public static final String NAME = "destinatarioModeloComunicacaoList";
	
	private static final String DEFAULT_EJBQL = "select o from DestinatarioModeloComunicacao o where "
			+ "o.modeloComunicacao = #{destinatarioModeloComunicacaoList.modeloComunicacao}";
	private static final String DEFAULT_ORDER = "id";
	
	private ModeloComunicacao modeloComunicacao;

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
