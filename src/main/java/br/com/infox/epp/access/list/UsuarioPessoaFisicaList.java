package br.com.infox.epp.access.list;

import java.util.Map;

import javax.inject.Named;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.pessoa.entity.PessoaFisica;

@Named
@ViewScoped
public class UsuarioPessoaFisicaList extends EntityList<PessoaFisica> {

    private static final long serialVersionUID = 1L;

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
