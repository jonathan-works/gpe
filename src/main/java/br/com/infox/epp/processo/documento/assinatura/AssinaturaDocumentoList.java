package br.com.infox.epp.processo.documento.assinatura;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;

@Name(AssinaturaDocumentoList.NAME)
@Scope(ScopeType.PAGE)
@AutoCreate
public class AssinaturaDocumentoList extends EntityList<AssinaturaDocumento> {

    private static final String DEFAULT_ORDER = "dataAssinatura desc";
    private static final String DEFAULT_EJBQL = "select o from AssinaturaDocumento o";
    private static final long serialVersionUID = 1L;
    public static final String NAME = "assinaturaDocumentoList";
    
    @Override
    protected void addSearchFields() {
        addSearchField("pessoaFisica", SearchCriteria.IGUAL);
        addSearchField("papel", SearchCriteria.IGUAL);
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

}
