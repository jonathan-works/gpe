package br.com.infox.epp.access.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.pessoa.entity.PessoaFisica;

@Name(UsuarioPessoaFisicaList.NAME)
@Scope(ScopeType.PAGE)
@AutoCreate
public class UsuarioPessoaFisicaList extends EntityList<PessoaFisica> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "usuarioPessoaFisicaList";

    private static final String DEFAULT_EJBQL = "select p from UsuarioLogin ul inner join ul.pessoaFisica p ";
    private static final String DEFAULT_ORDER = "p.idPessoa";
    private static final String R1 = "ul = #{usuarioPessoaFisicaList.usuario}";

    private UsuarioLogin usuario;

    @Override
    protected void addSearchFields() {
        addSearchField("ul", SearchCriteria.IGUAL, R1);
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
    
    @Override
    public void newInstance() {
    	super.newInstance();
    	setUsuario(null);
    }

    public UsuarioLogin getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioLogin usuario) {
        this.usuario = usuario;
    }

}
