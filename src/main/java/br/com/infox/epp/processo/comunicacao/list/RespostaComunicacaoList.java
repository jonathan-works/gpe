package br.com.infox.epp.processo.comunicacao.list;

import java.util.Map;

import javax.inject.Named;

import br.com.infox.core.list.DataList;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.processo.comunicacao.DocumentoRespostaComunicacao;
import br.com.infox.epp.processo.entity.Processo;

@Named
@ViewScoped
public class RespostaComunicacaoList extends DataList<DocumentoRespostaComunicacao> {
    
	public static final String NAME = "respostaComunicacaoList";
	private static final long serialVersionUID = 1L;

	private static final String DEFAULT_EJBQL = "select o from DocumentoRespostaComunicacao o ";
	private static final String DEFAULT_WHERE = "where o.comunicacao = #{respostaComunicacaoList.processo} and o.enviado = false";
	
	private static final String DEFAULT_ORDER = "o.documento.dataInclusao desc";
	
	private static final String FILTRO_DESCRICAO_DOCUMENTO = "exists ( select 1 from Documento d where d.descricao like concat('%', #{respostaComunicacaoList.descricaoDocumento}, '%') "
	        + "and o.documento = d) ";

	private Processo processo;
	private String descricaoDocumento;
	
    @Override
	protected void addRestrictionFields() {
		addRestrictionField("descricaoDocumento", FILTRO_DESCRICAO_DOCUMENTO);
	}

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
    
    @Override
    public void newInstance() {
        super.newInstance();
        this.descricaoDocumento = null;
    }
}
