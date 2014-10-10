package br.com.infox.epp.processo.partes.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;

@Scope(ScopeType.PAGE)
@Name(ParticipanteProcessoList.NAME)
public class ParticipanteProcessoList extends EntityList<ParticipanteProcesso> {

    public static final String NAME = "participanteProcessoList";
    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_EJBQL = "select o from ParticipanteProcesso o";
    private static final String DEFAULT_ORDER = "pessoa";
    private static final String REGRA_IS_RESPONSAVEL = "(#{authenticator.isUsuarioAtualResponsavel()}=true or o.ativo=true)";

    @Override
    protected void addSearchFields() {
        addSearchField("processo", SearchCriteria.IGUAL);
        addSearchField("pessoa", SearchCriteria.IGUAL);
        addSearchField("isResponsavelLocalizacao", SearchCriteria.IGUAL, REGRA_IS_RESPONSAVEL);
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
