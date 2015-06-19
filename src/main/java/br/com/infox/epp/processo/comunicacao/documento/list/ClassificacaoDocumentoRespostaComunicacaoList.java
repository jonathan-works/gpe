package br.com.infox.epp.processo.comunicacao.documento.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;

@Name(ClassificacaoDocumentoRespostaComunicacaoList.NAME)
@AutoCreate
@Scope(ScopeType.PAGE)
public class ClassificacaoDocumentoRespostaComunicacaoList extends EntityList<ClassificacaoDocumento> {
	private static final long serialVersionUID = 1L;
	public static final String NAME = "classificacaoDocumentoRespostaComunicacaoList";
	
	protected static final String DEFAULT_EJBQL = "select o from ClassificacaoDocumento o "
			+ "where not exists (select 1 from TipoComunicacaoClassificacaoDocumento t where t.classificacaoDocumento = o "
				+ "	and t.tipoComunicacao = #{tipoComunicacaoCrudAction.instance})" ;
	private static final String DEFAULT_ORDER = "descricao";

	private static final String R1 = "o.ativo = #{classificacaoDocumentoRespostaComunicacaoList.ativo}";
	
	private Boolean ativo = true;
	
    @Override
    protected void addSearchFields() {
        addSearchField("codigoDocumento", SearchCriteria.CONTENDO);
        addSearchField("descricao", SearchCriteria.CONTENDO);
        addSearchField("inTipoDocumento", SearchCriteria.IGUAL);
        addSearchField("ativo", SearchCriteria.IGUAL, R1);
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
	
	public Boolean getAtivo() {
		return ativo;
	}
	
	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
}
