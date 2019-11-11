package br.com.infox.epp.documento.list.associative;

import java.util.Map;

import javax.inject.Named;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;
import br.com.infox.epp.documento.entity.Variavel;

@Named
@ViewScoped
public class AssociativeVariavelList extends EntityList<Variavel> {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_EJBQL = "select o from Variavel o where not exists "
            + "(select 1 from VariavelTipoModelo v where " + "v.variavel = o";
    private static final String DEFAULT_ORDER = "variavel";

    private TipoModeloDocumento tipoModeloToIgnore;
    
    public void onClickVariaveisTab(TipoModeloDocumento tipoModeloToIgnore){
        this.tipoModeloToIgnore = tipoModeloToIgnore;
        newInstance();
        setEjbql(getDefaultEjbql());
    }

    @Override
    protected void addSearchFields() {
        addSearchField("variavel", SearchCriteria.CONTENDO);
        addSearchField("valorVariavel", SearchCriteria.CONTENDO);
        addSearchField("ativo", SearchCriteria.IGUAL);
    }

    @Override
    protected String getDefaultEjbql() {
        StringBuilder sb = new StringBuilder();
        sb.append(DEFAULT_EJBQL);
        if (tipoModeloToIgnore != null
                && tipoModeloToIgnore.getIdTipoModeloDocumento() != null) {
            sb.append(" and v.tipoModeloDocumento.idTipoModeloDocumento = #{associativeVariavelList.tipoModeloToIgnore.idTipoModeloDocumento}");
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    protected String getDefaultOrder() {
        return DEFAULT_ORDER;
    }

    @Override
    protected Map<String, String> getCustomColumnsOrder() {
        return null;
    }

    public TipoModeloDocumento getTipoModeloToIgnore() {
        return tipoModeloToIgnore;
    }

    public void setTipoModeloToIgnore(TipoModeloDocumento tipoModeloToIgnore) {
        this.tipoModeloToIgnore = tipoModeloToIgnore;
    }
}