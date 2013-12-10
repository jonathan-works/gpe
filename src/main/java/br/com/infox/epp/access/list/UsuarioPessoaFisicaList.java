package br.com.infox.epp.access.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.pessoa.entity.PessoaFisica;

@Name(UsuarioPessoaFisicaList.NAME)
@Scope(ScopeType.PAGE)
public class UsuarioPessoaFisicaList extends EntityList<PessoaFisica> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "usuarioPessoaFisicaList";
    
    private static final String DEFAULT_EJBQL = "select p from UsuarioLogin ul inner join ul.pessoaFisica p "
            + "where ul = #{usuarioPessoaFisicaList.usuario}";
    private static final String DEFAULT_ORDER = "p.idPessoa";
    
    private UsuarioLogin usuario;

    @Override
    protected void addSearchFields() {
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
        // TODO Auto-generated method stub
        return null;
    }

    public UsuarioLogin getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioLogin usuario) {
        this.usuario = usuario;
    }

}
