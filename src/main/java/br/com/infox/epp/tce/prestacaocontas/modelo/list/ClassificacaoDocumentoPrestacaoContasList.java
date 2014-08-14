package br.com.infox.epp.tce.prestacaocontas.modelo.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.tce.prestacaocontas.modelo.entity.ModeloPrestacaoContasClassificacaoDocumento;

@Name(ClassificacaoDocumentoPrestacaoContasList.NAME)
@Scope(ScopeType.PAGE)
@AutoCreate
public class ClassificacaoDocumentoPrestacaoContasList extends EntityList<ModeloPrestacaoContasClassificacaoDocumento> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "classificacaoDocumentoPrestacaoContasList";
    private static final String DEFAULT_EJBQL = "select o from ModeloPrestacaoContasClassificacaoDocumento o";
    private static final String DEFAULT_ORDER = "o.classificacaoDocumento.tipoProcessoDocumento";

    @Override
    protected void addSearchFields() {
        addSearchField("modeloPrestacaoContas", SearchCriteria.IGUAL);
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
