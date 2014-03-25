package br.com.infox.epp.processo.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.processo.entity.RelacionamentoProcesso;

@Name(RelacionamentoProcessoList.NAME)
@Scope(ScopeType.CONVERSATION)
public class RelacionamentoProcessoList extends EntityList<RelacionamentoProcesso> {
    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_EJBQL = "select o from RelacionamentoProcesso o inner join o.relacionamento r";
    private static final String DEFAULT_ORDER = "r.dataRelacionamento";

    private static final String R1 = "r in (select r2 from RelacionamentoProcesso rp inner join rp.relacionamento r2 where rp.numeroProcesso=#{relacionamentoCrudAction.processo})";
    private static final String R2 = "o.numeroProcesso != #{relacionamentoCrudAction.processo}";

    public static final String NAME = "relacionamentoProcessoList";

    @Override
    protected void addSearchFields() {
        addSearchField("relacionamento", SearchCriteria.IGUAL, R1);
        addSearchField("processo", SearchCriteria.IGUAL, R2);
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
