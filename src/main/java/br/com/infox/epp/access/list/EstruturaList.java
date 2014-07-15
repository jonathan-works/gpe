package br.com.infox.epp.access.list;

import java.util.Map;

import javax.enterprise.context.ConversationScoped;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.access.entity.Estrutura;

@Name(EstruturaList.NAME)
@ConversationScoped
public class EstruturaList extends EntityList<Estrutura> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "estruturaList";
    public static final String DEFAULT_EJBQL = "select o from Estrutura o";
    public static final String DEFAULT_ORDER = "nome";

    @Override
    protected void addSearchFields() {
        addSearchField("nome", SearchCriteria.CONTENDO);
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
