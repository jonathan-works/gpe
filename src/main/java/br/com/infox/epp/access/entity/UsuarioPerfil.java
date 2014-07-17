package br.com.infox.epp.access.entity;

import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static javax.persistence.FetchType.EAGER;

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
public class UsuarioPerfil {

    private Integer idUsuarioPerfil;
    private UsuarioLogin usuarioLogin;
    private Perfil perfil;
    
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

}
