package br.com.infox.epp.processo.comunicacao.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.processo.comunicacao.service.RespostaComunicacaoService;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.entity.Processo;

@Name(RespostaComunicacaoList.NAME)
@Scope(ScopeType.PAGE)
@AutoCreate
public class RespostaComunicacaoList extends EntityList<Documento> {
	public static final String NAME = "respostaComunicacaoList";
	private static final long serialVersionUID = 1L;

	private static final String DEFAULT_EJBQL = "select o from Documento o where "
			+ "o.processo = #{respostaComunicacaoList.processoComunicacao} and "
			+ "exists (select 1 from MetadadoProcesso m where m.processo = #{respostaComunicacaoList.processoComunicacao} "
			+ "and m.metadadoType = '" + RespostaComunicacaoService.RESPOSTA_COMUNICACAO + "' and "
			+ "m.valor = cast(o.id as string))";
	
	private static final String DEFAULT_ORDER = "dataInclusao";

	private Processo processoComunicacao;		
	
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
	
	public Processo getProcessoComunicacao() {
		return processoComunicacao;
	}
	
	public void setProcessoComunicacao(Processo processoComunicacao) {
		this.processoComunicacao = processoComunicacao;
	}
}
