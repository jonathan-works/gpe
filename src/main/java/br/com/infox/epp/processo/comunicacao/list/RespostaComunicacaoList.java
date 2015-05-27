package br.com.infox.epp.processo.comunicacao.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.processo.comunicacao.DocumentoRespostaComunicacao;
import br.com.infox.epp.processo.entity.Processo;

@Name(RespostaComunicacaoList.NAME)
@Scope(ScopeType.PAGE)
@AutoCreate
public class RespostaComunicacaoList extends EntityList<DocumentoRespostaComunicacao> {
	public static final String NAME = "respostaComunicacaoList";
	private static final long serialVersionUID = 1L;

	private static final String DEFAULT_EJBQL = "select o from DocumentoRespostaComunicacao o "
			+ "where o.comunicacao = #{respostaComunicacaoList.processo} and o.enviado = false";
	
	private static final String DEFAULT_ORDER = "o.documento.dataInclusao desc";
	
	private static final String FILTRO_DESCRICAO_DOCUMENTO = "exists ( select 1 from Documento d where d.descricao like concat('%', #{respostaComunicacaoList.descricaoDocumento}, '%') "
	        + "and o.documento = d) ";

	private Processo processo;
	private String descricaoDocumento;
	
	
    @Override
	protected void addSearchFields() {
		addSearchField("descricaoDocumento", SearchCriteria.CONTENDO, FILTRO_DESCRICAO_DOCUMENTO);
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
	
	public String getDescricaoDocumento() {
        return descricaoDocumento;
    }

    public void setDescricaoDocumento(String descricaoDocumento) {
        this.descricaoDocumento = descricaoDocumento;
    }
}
