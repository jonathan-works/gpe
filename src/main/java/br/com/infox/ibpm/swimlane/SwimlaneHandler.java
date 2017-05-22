package br.com.infox.ibpm.swimlane;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.taskmgmt.def.Swimlane;

import com.google.common.base.Strings;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.core.util.ReflectionsUtil;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.access.entity.PerfilTemplate_;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioLogin_;
import br.com.infox.epp.access.manager.UsuarioPerfilManager;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.localizacao.LocalizacaoSearch;
import br.com.infox.ibpm.swimlane.SwimlaneConfiguration.Grupo;

public class SwimlaneHandler implements Serializable {

    private static final long serialVersionUID = 7688420965362230234L;
    private Swimlane swimlane;
    private boolean dirty;
    private boolean contabilizar = false;
    private PerfilTemplate perfil;
    private UsuarioLogin usuario;
    private Grupo grupo;
    private String expression;
    private List<PerfilTemplate> perfisPermitidosGrupo;
    private SwimlaneConfiguration configuration;
    private List<Pair<String, ?>> configuracoes;
    private Localizacao localizacaoRaiz;
    private LocalizacaoSearch localizacaoSearch = BeanManager.INSTANCE.getReference(LocalizacaoSearch.class);

    public SwimlaneHandler(Swimlane swimlane) {
        this.swimlane = swimlane;
        if (this.swimlane.getPooledActorsExpression() == null) {
            this.swimlane.setPooledActorsExpression("");
        }
        this.configuration = SwimlaneConfiguration.fromPooledActorsExpression(this.swimlane.getPooledActorsExpression());
        this.grupo = new Grupo();
    }

    public static List<SwimlaneHandler> createList(ProcessDefinition instance) {
        List<SwimlaneHandler> ret = new ArrayList<SwimlaneHandler>();
        Map<String, Swimlane> swimlanes = instance.getTaskMgmtDefinition().getSwimlanes();
        if (swimlanes == null) {
            return ret;
        }
        Collection<Swimlane> values = swimlanes.values();
        for (Swimlane swimlane : values) {
            ret.add(new SwimlaneHandler(swimlane));
        }
        return ret;
    }

    public void adicionarPerfil() {
        configuration.getPerfis().add(perfil);
        setPerfil(null);
        swimlane.setPooledActorsExpression(configuration.toPooledActorsExpression());
        reloadTableConfiguracoes();
    }
    
    public void adicionarUsuario() {
        configuration.getUsuarios().add(usuario);
        setUsuario(null);
        swimlane.setPooledActorsExpression(configuration.toPooledActorsExpression());
        reloadTableConfiguracoes();
    }
    
    public void adicionarGrupo() {
        configuration.getGrupos().add(grupo);
        this.grupo = new Grupo();
        perfisPermitidosGrupo = null;
        swimlane.setPooledActorsExpression(configuration.toPooledActorsExpression());
        reloadTableConfiguracoes();
    }
    
    public void adicionarExpressao() {
        configuration.getExpressoes().add(expression);
        expression = null;
        swimlane.setPooledActorsExpression(configuration.toPooledActorsExpression());
        reloadTableConfiguracoes();
    }
    
    public List<UsuarioLogin> getUsuarios(String nomeUsuario) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<UsuarioLogin> query = cb.createQuery(UsuarioLogin.class);
        Root<UsuarioLogin> usuario = query.from(UsuarioLogin.class);
        query.where(cb.isTrue(usuario.get(UsuarioLogin_.ativo)));
        
        if (!Strings.isNullOrEmpty(nomeUsuario)) {
            query.where(query.getRestriction(), cb.like(cb.lower(usuario.get(UsuarioLogin_.nomeUsuario)), "%" + nomeUsuario.toLowerCase() + "%"));
        }
        if (!configuration.getUsuarios().isEmpty()) {
            query.where(query.getRestriction(), usuario.in(configuration.getUsuarios()).not());
        }
        
        query.orderBy(cb.asc(usuario.get(UsuarioLogin_.nomeUsuario)));
        return entityManager.createQuery(query).getResultList();
    }
    
    public List<PerfilTemplate> getPerfis(String nomePerfil) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<PerfilTemplate> query = cb.createQuery(PerfilTemplate.class);
        Root<PerfilTemplate> perfil = query.from(PerfilTemplate.class);
        query.where(cb.isTrue(perfil.get(PerfilTemplate_.ativo)));
        
        if (!Strings.isNullOrEmpty(nomePerfil)) {
            query.where(query.getRestriction(), cb.like(cb.lower(perfil.get(PerfilTemplate_.descricao)), "%" + nomePerfil.toLowerCase() + "%"));
        }
        if (!configuration.getPerfis().isEmpty()) {
            query.where(query.getRestriction(), perfil.in(configuration.getPerfis()).not());
        }
        
        query.orderBy(cb.asc(perfil.get(PerfilTemplate_.descricao)));
        return entityManager.createQuery(query).getResultList();
    }
    
    public List<Localizacao> getLocalizacoes(String nomeLocalizacao) {
    	if(localizacaoRaiz == null){
    		localizacaoRaiz = localizacaoSearch.getLocalizacaoRaizSistema();
    	}
    	
    	return localizacaoSearch.getLocalizacaoSuggestTree(localizacaoRaiz, nomeLocalizacao);
    }
    
    public void updatePerfisGrupo(){
    	perfisPermitidosGrupo = null; 
    	getPerfisPermitidosGrupo();
    }
    public List<PerfilTemplate> getPerfisPermitidosGrupo() {
        if (perfisPermitidosGrupo == null && this.grupo.getLocalizacao() != null) {
            perfisPermitidosGrupo = BeanManager.INSTANCE.getReference(UsuarioPerfilManager.class).getPerfisPermitidos(this.grupo.getLocalizacao());
            for (Grupo grupo : configuration.getGrupos()) {
                perfisPermitidosGrupo.remove(grupo.getPerfil());
            }
        }
        return perfisPermitidosGrupo;
    }
    
    public List<Pair<String, ?>> getConfiguracoes() {
        if (configuracoes == null) {
            configuracoes = new ArrayList<>();
            for (PerfilTemplate perfilTemplate : configuration.getPerfis()) {
                configuracoes.add(new ImmutablePair<>("perfil", perfilTemplate));
            }
            for (UsuarioLogin usuarioLogin : configuration.getUsuarios()) {
                configuracoes.add(new ImmutablePair<>("usuario", usuarioLogin));
            }
            for (Grupo grupo : configuration.getGrupos()) {
                configuracoes.add(new ImmutablePair<>("grupo", grupo));
            }
            for (String expressao : configuration.getExpressoes()) {
                configuracoes.add(new ImmutablePair<>("el", expressao));
            }
        }
        return configuracoes;
    }
    
    public void removerConfiguracao(Pair<String, ?> configuracao) {
        if (configuracao.getLeft().equals("perfil")) {
            removerItem(configuracao.getRight(), configuration.getPerfis());
        } else if (configuracao.getLeft().equals("usuario")) {
            removerItem(configuracao.getRight(), configuration.getUsuarios());
        } else if (configuracao.getLeft().equals("grupo")) {
            removerItem(configuracao.getRight(), configuration.getGrupos());
        } else if (configuracao.getLeft().equals("el")) {
            removerItem(configuracao.getRight(), configuration.getExpressoes());
        }
        swimlane.setPooledActorsExpression(configuration.toPooledActorsExpression());
        reloadTableConfiguracoes();
    }
    
    private void removerItem(Object item, List<?> itens) {
        Iterator<?> it = itens.iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            if (item.equals(obj)) {
                it.remove();
                break;
            }
        }
    }

    public String getCaminhoLocalizacao(Localizacao localizacao) {
        StringBuilder sb = new StringBuilder();
        String[] tokens = localizacao.getCaminhoCompleto().split("\\|");
        for (String token : tokens) {
            if (sb.length() > 0) {
                sb.append(" / ");
            }
            sb.append(token);
        }
        return sb.toString();
    }
    
    public UsuarioLogin getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioLogin usuario) {
        this.usuario = usuario;
    }
    
    public Grupo getGrupo() {
        return grupo;
    }
    
    public String getExpression() {
        return expression;
    }
    
    public void setExpression(String expression) {
        this.expression = expression;
    }

    public boolean getContabilizar() {
        return contabilizar;
    }
    
    public void setContabilizar(boolean contabilizar) {
        this.contabilizar = contabilizar;
    }

    public PerfilTemplate getPerfil() {
        return perfil;
    }
    
    public void setPerfil(PerfilTemplate perfilTemplate) {
        this.perfil = perfilTemplate;
        this.contabilizar = false;
    }
    
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SwimlaneHandler)) {
            return false;
        }
        SwimlaneHandler other = (SwimlaneHandler) obj;
        return this.getSwimlane().getKey().equals(other.getSwimlane().getKey());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.getSwimlane().hashCode();
        return result;
    }
    
    @Override
    public String toString() {
        return swimlane.getName();
    }
    

    public Swimlane getSwimlane() {
        return swimlane;
    }

    public void setSwimlane(Swimlane swimlane) {
        this.swimlane = swimlane;
    }

    public String getName() {
        return swimlane.getName();
    }

    public void setName(String name) {
        Map<String, Swimlane> swimlanes = swimlane.getTaskMgmtDefinition().getSwimlanes();
        swimlanes.remove(swimlane.getName());
        ReflectionsUtil.setValue(swimlane, "name", name);
        swimlane.getTaskMgmtDefinition().addSwimlane(swimlane);
    }
    
    public Localizacao getLocalizacao() {
        return grupo.getLocalizacao();
    }

    public void setLocalizacao(Localizacao localizacao) {
        grupo.setLocalizacao(localizacao);
        grupo.setPerfil(null);
        perfisPermitidosGrupo = null;
    }
    
    private EntityManager getEntityManager() {
        return EntityManagerProducer.getEntityManager();
    }
    
    private void reloadTableConfiguracoes() {
        configuracoes = null;
        getConfiguracoes();
    }
}
