package br.com.infox.epp.processo.comunicacao.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.entity.Processo;

@Name(RespostaComunicacaoList.NAME)
@Scope(ScopeType.PAGE)
@AutoCreate
public class RespostaComunicacaoList extends EntityList<Documento> {
	public static final String NAME = "respostaComunicacaoList";
	private static final long serialVersionUID = 1L;

	private static final String DEFAULT_EJBQL = "select o.documento from DocumentoRespostaComunicacao o "
			+ "where o.comunicacao = #{respostaComunicacaoList.processo} and o.enviado = false";
	
	private static final String DEFAULT_ORDER = "o.documento.dataInclusao desc";

	private Processo processo;		
	
	@Override
	protected void addSearchFields() {
		addSearchField("descricao", SearchCriteria.CONTENDO);
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
}
