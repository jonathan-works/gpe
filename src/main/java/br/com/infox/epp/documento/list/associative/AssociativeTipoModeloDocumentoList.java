package br.com.infox.epp.documento.list.associative;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;
import br.com.infox.epp.documento.entity.Variavel;

@Name(AssociativeTipoModeloDocumentoList.NAME)
@Scope(ScopeType.PAGE)
public class AssociativeTipoModeloDocumentoList extends EntityList<TipoModeloDocumento> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "associativeTipoModeloDocumentoList";

    private static final String DEFAULT_EJBQL = "select o from TipoModeloDocumento o where o not in "
            + "(select v.tipoModeloDocumento from VariavelTipoModelo v where v.variavel = #{associativeTipoModeloDocumentoList.variavelToIgnore})";
    private static final String DEFAULT_ORDER = "tipoModeloDocumento";

    private Variavel variavelToIgnore;

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

    public Variavel getVariavelToIgnore() {
        return variavelToIgnore;
    }

    public void setVariavelToIgnore(Variavel variavelToIgnore) {
        this.variavelToIgnore = variavelToIgnore;
    }

}
