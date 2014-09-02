package br.com.infox.epp.meiocontato.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.meiocontato.entity.MeioContato;
import br.com.infox.epp.pessoa.entity.Pessoa;

@Name(MeioContatoList.NAME)
@Scope(ScopeType.PAGE)
public class MeioContatoList extends EntityList<MeioContato> {
    private static final long serialVersionUID = 1L;
    public static final String NAME = "meioContatoList";
    
    private static final String DEFAULT_EJBQL = "select o from MeioContato o";
    private static final String DEFAULT_ORDER = "o.idMeioContato";
    private static final String R1 = "o.pessoa = #{meioContatoList.pessoa}";
    
    private Pessoa pessoa;
    
    @Override
    protected void addSearchFields() {
        addSearchField("o", SearchCriteria.IGUAL, R1);
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
    
    public Pessoa getPessoa() {
        return pessoa;
    }
    
    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }
}