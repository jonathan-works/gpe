package br.com.infox.epp.access.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.access.component.tree.LocalizacaoTreeHandler;
import br.com.infox.epp.access.component.tree.PapelTreeHandler;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.seam.util.ComponentUtil;

@Name(UsuarioLoginList.NAME)
@Scope(ScopeType.PAGE)
public class UsuarioLoginList extends EntityList<UsuarioLogin> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "usuarioLoginList";
    
    private static final String TEMPLATE = "/Usuario/usuarioTemplate.xls";
    private static final String DOWNLOAD_XLS_NAME = "Usuarios.xls";

    private static final String DEFAULT_EJBQL = "select o from UsuarioLogin o ";
    
    private static final String FILTRO_BY_LOCALIZACAO = "exists (select 1 from UsuarioPerfil upl "
    							+ " where upl.localizacao = #{usuarioLoginList.localizacao} "
    							+ " and upl.usuarioLogin = o and upl.ativo = true)";
    
    private static final String FILTRO_BY_PAPEL = "exists (select 1 from UsuarioPerfil upl inner join upl.perfilTemplate pt "
    		+ " where pt.papel = #{usuarioLoginList.papel} and upl.usuarioLogin = o and upl.ativo = true)";
    
    private static final String FILTRO_BY_CPF = "exists (select 1 from PessoaFisica pf where o.pessoaFisica = pf and pf.cpf = #{usuarioLoginList.cpf} )";
    
    private static final String DEFAULT_ORDER = "o.nomeUsuario";
    
    private Localizacao localizacao;
    private Papel papel;
    private String cpf;
    
    @Override
    public void newInstance() {
    	super.newInstance();
    	setLocalizacao(null);
    	setPapel(null);
    	setCpf(null);
    	LocalizacaoTreeHandler localizacaoTreeHandler = ComponentUtil.getComponent(LocalizacaoTreeHandler.NAME);
    	localizacaoTreeHandler.clearTree();
    	PapelTreeHandler papelTreeHandler = ComponentUtil.getComponent(PapelTreeHandler.NAME);
    	papelTreeHandler.clearTree();
    }
    
    @Override
    protected void addSearchFields() {
        addSearchField("tipoUsuario", SearchCriteria.IGUAL);
        addSearchField("nomeUsuario", SearchCriteria.CONTENDO);
        addSearchField("cpf", SearchCriteria.IGUAL, FILTRO_BY_CPF);
        addSearchField("login", SearchCriteria.CONTENDO);
        addSearchField("bloqueio", SearchCriteria.IGUAL);
        addSearchField("provisorio", SearchCriteria.IGUAL);
        addSearchField("ativo", SearchCriteria.IGUAL);
        addSearchField("localizacao", SearchCriteria.IGUAL, FILTRO_BY_LOCALIZACAO);
        addSearchField("papel", SearchCriteria.IGUAL, FILTRO_BY_PAPEL);
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
    public String getTemplate() {
        return TEMPLATE;
    }

    @Override
    public String getDownloadXlsName() {
        return DOWNLOAD_XLS_NAME;
    }

	public Localizacao getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(Localizacao localizacao) {
		this.localizacao = localizacao;
	}

	public Papel getPapel() {
		return papel;
	}

	public void setPapel(Papel papel) {
		this.papel = papel;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

}
