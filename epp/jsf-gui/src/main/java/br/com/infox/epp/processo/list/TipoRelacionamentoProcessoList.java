package br.com.infox.epp.processo.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.processo.entity.TipoRelacionamentoProcesso;

@Name(TipoRelacionamentoProcessoList.NAME)
@Scope(ScopeType.CONVERSATION)
public class TipoRelacionamentoProcessoList extends EntityList<TipoRelacionamentoProcesso> {

    private static final String DEFAULT_ORDER = "o.ativo,o.tipoRelacionamento";
    private static final String DEFAULT_EJBQL = "select o from TipoRelacionamentoProcesso o";
    public static final String NAME = "tipoRelacionamentoProcessoList";
    private static final long serialVersionUID = 1L;

    @Override
    protected void addSearchFields() {
        addSearchField("codigo", SearchCriteria.CONTENDO);
        addSearchField("tipoRelacionamento", SearchCriteria.CONTENDO);
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

}
