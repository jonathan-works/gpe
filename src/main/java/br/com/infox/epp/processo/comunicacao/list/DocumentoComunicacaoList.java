package br.com.infox.epp.processo.comunicacao.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.entity.Processo;

@Name(DocumentoComunicacaoList.NAME)
@Scope(ScopeType.PAGE)
@AutoCreate
public class DocumentoComunicacaoList extends EntityList<Documento> {
	public static final String NAME = "documentoComunicacaoList";
	private static final long serialVersionUID = 1L;
	
	private static final String DEFAULT_EJBQL = "select o from Documento o where "
			+ " o.processo = #{documentoComunicacaoList.processo} and "
			+ " o.documentoBin.id in "
			+ "(select dm.documento.documentoBin.id from DocumentoModeloComunicacao dm "
			+ " where dm.modeloComunicacao = #{documentoComunicacaoList.modeloComunicacao})";
	
	private static final String DEFAULT_ORDER = "dataInclusao desc";
	
	private Processo processo;
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

	public Processo getProcesso() {
		return processo;
	}
	
	public void setProcesso(Processo processo) {
		this.processo = processo;
	}
	
	public ModeloComunicacao getModeloComunicacao() {
		return modeloComunicacao;
	}
	
	public void setModeloComunicacao(ModeloComunicacao modeloComunicacao) {
		this.modeloComunicacao = modeloComunicacao;
	}
}
