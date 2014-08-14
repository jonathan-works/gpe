package br.com.infox.epp.tce.prestacaocontas.modelo.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.tce.prestacaocontas.modelo.entity.ResponsavelModeloPrestacaoContas;

@Name(ResponsavelModeloPrestacaoContasList.NAME)
@Scope(ScopeType.PAGE)
@AutoCreate
public class ResponsavelModeloPrestacaoContasList extends EntityList<ResponsavelModeloPrestacaoContas> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "responsavelModeloPrestacaoContasList";
    public static final String DEFAULT_EJBQL = "select o from ResponsavelModeloPrestacaoContas o";
    public static final String DEFAULT_ORDER = "o.tipoParte.descricao";

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
