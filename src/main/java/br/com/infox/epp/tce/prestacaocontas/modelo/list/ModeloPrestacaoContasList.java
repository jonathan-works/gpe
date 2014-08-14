package br.com.infox.epp.tce.prestacaocontas.modelo.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.tce.prestacaocontas.modelo.entity.ModeloPrestacaoContas;

@Name(ModeloPrestacaoContasList.NAME)
@Scope(ScopeType.PAGE)
public class ModeloPrestacaoContasList extends EntityList<ModeloPrestacaoContas> {
    private static final long serialVersionUID = 1L;
    public static final String NAME = "modeloPrestacaoContasList";

    public static final String DEFAULT_EJBQL = "select o from ModeloPrestacaoContas o";
    public static final String DEFAULT_ORDER = "o.nome";
    
    @Override
    protected void addSearchFields() {
        addSearchField("tipoPrestacaoContas", SearchCriteria.IGUAL);
        addSearchField("esfera", SearchCriteria.IGUAL);
        addSearchField("grupoPrestacaoContas", SearchCriteria.IGUAL);
        addSearchField("anoExercicio", SearchCriteria.IGUAL);
        addSearchField("nome", SearchCriteria.CONTENDO);
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
