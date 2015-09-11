package br.com.infox.epp.endereco.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.endereco.entity.PessoaEndereco;
import br.com.infox.epp.pessoa.entity.Pessoa;

@Name(PessoaEnderecoList.NAME)
@Scope(ScopeType.PAGE)
@AutoCreate
public class PessoaEnderecoList extends EntityList<PessoaEndereco> {

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

    private static final long serialVersionUID = 1L;
    public static final String NAME = "pessoaEnderecoList";
    
    private static final String DEFAULT_EJBQL = "select o from PessoaEndereco o";
    private static final String DEFAULT_ORDER = "o.id";
    private static final String R1 = "o.pessoa = #{pessoaEnderecoList.pessoa}";
}
