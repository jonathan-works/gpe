package br.com.infox.epp.access.list;

import java.util.List;
import java.util.Map;

import javax.inject.Named;

import br.com.infox.core.list.DataList;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.entity.Fluxo;

@Named
@ViewScoped
public class UsuarioRaiaList extends DataList<UsuarioLogin> {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_JPQL = "select distinct o from UsuarioLogin o "
        + "inner join fetch o.pessoaFisica pf "
        + "inner join o.usuarioPerfilList up inner join up.perfilTemplate pt , RaiaPerfil rp ";
        
    private static final String DEFAULT_WHERE = "where rp.perfilTemplate.id = pt.id "
        + "and rp.fluxo.idFluxo = {idFluxo}";

    private static final String DEFAULT_ORDER = "o.nomeUsuario";

    private List<PerfilTemplate> perfis;
    private String nomeUsuario;
    private PerfilTemplate perfil;
    private Fluxo fluxo;

    @Override
    protected void addRestrictionFields() {
        addRestrictionField("nomeUsuario", "o.nomeUsuario like concat('%', #{usuarioRaiaList.nomeUsuario} ,'%')");
        addRestrictionField("perfil", "rp.perfilTemplate.id = #{usuarioRaiaList.perfil.id}");
    }

    @Override
    protected String getDefaultEjbql() {
        return DEFAULT_JPQL;
    }
    
    @Override
    protected String getDefaultWhere() {
        return DEFAULT_WHERE.replace("{idFluxo}", getFluxo().getIdFluxo().toString());
    }
    
    @Override
    protected String getDefaultOrder() {
        return DEFAULT_ORDER;
    }

    @Override
    protected Map<String, String> getCustomColumnsOrder() {
        return null;
    }
    
    public List<PerfilTemplate> getPerfis() {
        if (perfis == null) {
            String jpql = "select distinct pt from RaiaPerfil rp inner join rp.perfilTemplate pt where rp.fluxo = :fluxo order by pt.descricao ";
            perfis = getEntityManager().createQuery(jpql, PerfilTemplate.class).setParameter("fluxo", getFluxo()).getResultList();
        }
        return perfis;
    }
    
    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public PerfilTemplate getPerfil() {
        return perfil;
    }
    
    public void setPerfil(PerfilTemplate perfil) {
        this.perfil = perfil;
    }

    public Fluxo getFluxo() {
        return fluxo;
    }

    public void setFluxo(Fluxo fluxo) {
        this.fluxo = fluxo;
    }
    
}
