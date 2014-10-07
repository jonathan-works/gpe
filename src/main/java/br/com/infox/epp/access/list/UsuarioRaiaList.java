package br.com.infox.epp.access.list;

import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.constants.WarningConstants;
import br.com.infox.core.list.EntityList;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.infox.epp.processo.home.ProcessoEpaHome;

@AutoCreate
@Scope(ScopeType.CONVERSATION)
@Name(UsuarioRaiaList.NAME)
public class UsuarioRaiaList extends EntityList<UsuarioLogin> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "usuarioRaiaList";

    private static final String DEFAULT_EJBQL = "select distinct u.* from tb_raia_perfil rp "
            + "inner join tb_usuario_perfil up on (up.id_perfil_template = rp.id_perfil_template) "
            + "inner join tb_usuario_login u on (u.id_usuario_login = up.id_usuario_login) "
            + "where u.id_pessoa_fisica is not null "
            + "and rp.id_fluxo = ";

    private static final String DEFAULT_ORDER = "u.nm_usuario";

    private List<PerfilTemplate> perfis;
    private PerfilTemplate perfil;
    private Fluxo fluxo;

    public UsuarioRaiaList() {
        setNativeQuery(true);
        setResultClass(UsuarioLogin.class);
    }

    @Override
    protected void addSearchFields() {
    }

    @Override
    protected String getDefaultEjbql() {
        return DEFAULT_EJBQL + getFluxo().getIdFluxo();
    }
    
    @Override
    protected String getDefaultOrder() {
        return DEFAULT_ORDER;
    }

    @Override
    protected Map<String, String> getCustomColumnsOrder() {
        return null;
    }
    
    @SuppressWarnings(WarningConstants.UNCHECKED)
    public List<PerfilTemplate> getPerfis() {
        if (perfis == null) {
            perfis = getEntityManager().createNativeQuery("select p.* from tb_raia_perfil rp "
                    + "inner join tb_perfil_template p on (p.id_perfil_template = rp.id_perfil_template) "
                    + "where rp.id_fluxo = :idFluxo", PerfilTemplate.class)
                .setParameter("idFluxo", getFluxo().getIdFluxo())
                .getResultList();
        }
        return perfis;
    }

    public PerfilTemplate getPerfil() {
        return perfil;
    }
    
    public void setPerfil(PerfilTemplate perfil) {
        this.perfil = perfil;
    }

    @Override
    public void newInstance() {
        super.newInstance();
        this.perfil = null;
        refreshQuery();
    }

    public void refreshQuery() {
        StringBuilder sb = new StringBuilder();

        if (getPerfil() != null) {
            sb.append(getDefaultEjbql());
            sb.append(" and up.id_perfil_template = ");
            sb.append(getPerfil().getId());
        } else {
            sb.append(getDefaultEjbql());
        }

        if (getEntity().getNomeUsuario() != null) {
            sb.append(" and lower(u.nm_usuario) like '%");
            sb.append(getEntity().getNomeUsuario().toLowerCase());
            sb.append("%'");
        }
        setEjbql(sb.toString());
    }
    
    private Fluxo getFluxo() {
        if (fluxo == null) {
            fluxo = getEntityManager().find(ProcessoEpa.class, ProcessoEpaHome.instance().getId()).getNaturezaCategoriaFluxo().getFluxo();
        }
        return fluxo;
    }
}
