package br.com.infox.epp.access.entity;

import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static javax.persistence.FetchType.EAGER;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="tb_usuario_perfil")
public class UsuarioPerfil implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private Integer idUsuarioPerfil;
    private UsuarioLogin usuarioLogin;
    private Perfil perfil;

    private Boolean responsavelLocalizacao;
    
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
    
    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "id_perfil", nullable = false)
    @NotNull
    public Perfil getPerfil() {
        return perfil;
    }
    
    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }
    
    @Column(name="in_responsavel", nullable=false)
    @NotNull
    public Boolean getResponsavelLocalizacao() {
        return this.responsavelLocalizacao;
    }
    
    public void setResponsavelLocalizacao(Boolean responsavelLocalizacao) {
        this.responsavelLocalizacao = responsavelLocalizacao;
    }
    
    @Override
    public String toString() {
        return getPerfil().getDescricao();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((getIdUsuarioPerfil() == null) ? 0 : getIdUsuarioPerfil().hashCode());
        result = prime * result + ((getPerfil() == null) ? 0 : getPerfil().hashCode());
        result = prime * result
                + ((getUsuarioLogin() == null) ? 0 : getPerfil().hashCode());
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
        if (getPerfil() == null) {
            if (other.getPerfil() != null) return false;
        } else if (!getPerfil().equals(other.getPerfil())) return false;
        if (getUsuarioLogin() == null) {
            if (other.getUsuarioLogin() != null) return false;
        } else if (!getUsuarioLogin().equals(other.getUsuarioLogin())) return false;
        return true;
    }
    
    

}
