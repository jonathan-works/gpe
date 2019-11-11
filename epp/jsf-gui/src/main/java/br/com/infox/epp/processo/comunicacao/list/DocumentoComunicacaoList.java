package br.com.infox.epp.processo.comunicacao.list;

import java.util.Map;

import javax.ejb.Stateful;
import javax.inject.Named;

import br.com.infox.core.list.DataList;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.documento.entity.Documento;

@Named
@ViewScoped
@Stateful
public class DocumentoComunicacaoList extends DataList<Documento> {
	public static final String NAME = "documentoComunicacaoList";
	private static final long serialVersionUID = 1L;
	
	private static final String DEFAULT_EJBQL = "select o.documento from DocumentoModeloComunicacao o ";
	private static final String DEFAULT_WHERE = "where o.modeloComunicacao = #{documentoComunicacaoList.modeloComunicacao}";
	
	private static final String DEFAULT_ORDER = "o.documento.dataInclusao desc";
	
	private ModeloComunicacao modeloComunicacao;

	@Override
	protected String getDefaultEjbql() {
		return DEFAULT_EJBQL;
	}

	@Override
	protected String getDefaultWhere() {
	    return DEFAULT_WHERE;
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
