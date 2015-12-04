package br.com.infox.epp.access.entity;

import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.epp.access.query.UsuarioPerfilQuery.EXISTE_USUARIO_COM_DESCRICAO_PERFIL_ATIVO;
import static br.com.infox.epp.access.query.UsuarioPerfilQuery.EXISTE_USUARIO_COM_DESCRICAO_PERFIL_ATIVO_QUERY;
import static br.com.infox.epp.access.query.UsuarioPerfilQuery.GET_BY_USUARIO_LOGIN_PERFIL_TEMPLATE_LOCALIZACAO;
import static br.com.infox.epp.access.query.UsuarioPerfilQuery.GET_BY_USUARIO_LOGIN_PERFIL_TEMPLATE_LOCALIZACAO_QUERY;
import static br.com.infox.epp.access.query.UsuarioPerfilQuery.LIST_BY_USUARIO_LOGIN;
import static br.com.infox.epp.access.query.UsuarioPerfilQuery.*;
import static java.text.MessageFormat.format;
import static javax.persistence.FetchType.EAGER;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="tb_usuario_perfil")
@NamedQueries({
    @NamedQuery(name=GET_BY_USUARIO_LOGIN_PERFIL_TEMPLATE_LOCALIZACAO, query=GET_BY_USUARIO_LOGIN_PERFIL_TEMPLATE_LOCALIZACAO_QUERY),
    @NamedQuery(name=LIST_BY_USUARIO_LOGIN, query=LIST_BY_USUARIO_LOGIN_QUERY),
    @NamedQuery(name=EXISTE_USUARIO_COM_DESCRICAO_PERFIL_ATIVO, query=EXISTE_USUARIO_COM_DESCRICAO_PERFIL_ATIVO_QUERY),
    @NamedQuery(name = LIST_BY_LOCALIZACAO_ATIVO, query = LIST_BY_LOCALIZACAO_ATIVO_QUERY),
    @NamedQuery(name = LIST_BY_USUARIO_PERFIL_LOCALIZACAO_ATIVO, query = LIST_BY_USUARIO_PERFIL_LOCALIZACAO_ATIVO_QUERY)
})
public class UsuarioPerfil implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private Integer idUsuarioPerfil;
    private UsuarioLogin usuarioLogin;
    private PerfilTemplate perfilTemplate;
    private Localizacao localizacao;
    private Boolean responsavelLocalizacao;
    private Boolean ativo = Boolean.TRUE;
    
    public UsuarioPerfil() {
    }
    
    public UsuarioPerfil(UsuarioLogin usuarioLogin, PerfilTemplate perfilTemplate, Localizacao localizacao) {
        this(usuarioLogin, perfilTemplate, localizacao, Boolean.FALSE);
    }
    
    public UsuarioPerfil(UsuarioLogin usuarioLogin, PerfilTemplate perfilTemplate, Localizacao localizacao, boolean responsavelLocalizacao) {
        setUsuarioLogin(usuarioLogin);
        setPerfilTemplate(perfilTemplate);
        setLocalizacao(localizacao);
        setResponsavelLocalizacao(responsavelLocalizacao);
    }
    
    @SequenceGenerator(allocationSize=1, initialValue=1, name = GENERATOR, sequenceName = "sq_tb_usuario_perfil")
    @Id
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = "id_usuario_perfil", unique = true, nullable = false)
    public Integer getIdUsuarioPerfil() {
        return idUsuarioPerfil;
    }
    
    public void setIdUsuarioPerfil(Integer idUsuarioPerfil) {
        this.idUsuarioPerfil = idUsuarioPerfil;
    }
    
    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "id_usuario_login", nullable = false)
    @NotNull
    public UsuarioLogin getUsuarioLogin() {
        return usuarioLogin;
    }
    
    public void setUsuarioLogin(UsuarioLogin usuarioLogin) {
        this.usuarioLogin = usuarioLogin;
    }
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="id_perfil_template", nullable=false)
    @NotNull
    public PerfilTemplate getPerfilTemplate() {
        return perfilTemplate;
    }

    public void setPerfilTemplate(PerfilTemplate perfilTemplate) {
        this.perfilTemplate = perfilTemplate;
    }

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="id_localizacao", nullable=false)
    @NotNull
    public Localizacao getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(Localizacao localizacao) {
        this.localizacao = localizacao;
    }

    @Column(name="in_responsavel", nullable=false)
    @NotNull
    public Boolean getResponsavelLocalizacao() {
        return this.responsavelLocalizacao;
    }
    
    public void setResponsavelLocalizacao(Boolean responsavelLocalizacao) {
        this.responsavelLocalizacao = responsavelLocalizacao;
    }
    
    @Column(name="in_ativo", nullable=false)
    @NotNull
    public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	@Override
    public String toString() {
        return format("{0} - {1}", getLocalizacao(), getPerfilTemplate());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((getIdUsuarioPerfil() == null) ? 0 : getIdUsuarioPerfil().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof UsuarioPerfil)) return false;
        UsuarioPerfil other = (UsuarioPerfil) obj;
        if (getIdUsuarioPerfil() == null) {
            if (other.getIdUsuarioPerfil() != null) return false;
        } else if (!getIdUsuarioPerfil().equals(other.getIdUsuarioPerfil())) return false;
        return true;
    }
    

}
