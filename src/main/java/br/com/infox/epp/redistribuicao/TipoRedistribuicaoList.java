package br.com.infox.epp.redistribuicao;

import javax.inject.Named;

import br.com.infox.core.list.DataList;
import br.com.infox.core.list.RestrictionType;
import br.com.infox.epp.cdi.ViewScoped;

@Named
@ViewScoped
public class TipoRedistribuicaoList extends DataList<TipoRedistribuicao> {
    private static final long serialVersionUID = 1L;

    private final String DEFAULT_EJBQL = "select o from TipoRedistribuicao o ";
    private final String DEFAULT_ORDER = "o.descricao";

    // Filters controll
    private String codigo;
    private String descricao;
    private Boolean ativo;

    @Override
    protected String getDefaultEjbql() {
        return DEFAULT_EJBQL;
    }

    @Override
    protected String getDefaultOrder() {
        return DEFAULT_ORDER;
    }

    @Override
    protected void addRestrictionFields() {
        addRestrictionField("codigo", "o.codigo", RestrictionType.contendoLower);
        addRestrictionField("descricao", "o.descricao", RestrictionType.contendoLower);
        addRestrictionField("ativo", "o.ativo", RestrictionType.igual);
    }

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
    
}
