package br.com.infox.epp.processo.comunicacao.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.documento.entity.Documento;

@Name(DocumentoComunicacaoList.NAME)
@Scope(ScopeType.PAGE)
@AutoCreate
public class DocumentoComunicacaoList extends EntityList<Documento> {
	public static final String NAME = "documentoComunicacaoList";
	private static final long serialVersionUID = 1L;
	
	private static final String DEFAULT_EJBQL = "select o.documento from DocumentoModeloComunicacao o "
			+ " where o.modeloComunicacao = #{documentoComunicacaoList.modeloComunicacao}";
	
	private static final String DEFAULT_ORDER = "o.documento.dataInclusao desc";
	
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
