package br.com.infox.epp.documento.list.associative;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;

@Name(AssociativeTipoModeloDocumentoList.NAME)
@Scope(ScopeType.PAGE)
public class AssociativeTipoModeloDocumentoList extends EntityList<TipoModeloDocumento> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "associativeTipoModeloDocumentoList";

    private static final String DEFAULT_EJBQL = "select o from TipoModeloDocumento o where o not in "
            + "(select vti.tipoModeloDocumento from VariavelTipoModelo vti inner join vti.variavel v where v.variavel = #{associativeTipoModeloDocumentoList.variavelToIgnore})";
    private static final String DEFAULT_ORDER = "tipoModeloDocumento";

    private String variavelToIgnore = ".";

    @Override
    protected void addSearchFields() {
        addSearchField("grupoModeloDocumento", SearchCriteria.IGUAL);
        addSearchField("grupoModeloDocumento.idGrupoModeloDocumento", SearchCriteria.IGUAL);
        addSearchField("tipoModeloDocumento", SearchCriteria.CONTENDO);
        addSearchField("abreviacao", SearchCriteria.CONTENDO);
        addSearchField("ativo", SearchCriteria.IGUAL);
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

    public String getVariavelToIgnore() {
        return variavelToIgnore;
    }

    public void setVariavelToIgnore(String variavelToIgnore) {
        this.variavelToIgnore = variavelToIgnore;
    }

}
