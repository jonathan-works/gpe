package br.com.infox.epp.access.entity;

import static br.com.infox.core.persistence.ORConstants.ATIVO;
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
@Table(name = "tb_perfil")
public class Perfil {
    
    private Integer idPerfil;
    private String descricao;
    private Localizacao localizacao;
    private Localizacao paiDaEstrutura;
    private Papel papel;
    private Boolean ativo;
    
    @SequenceGenerator(allocationSize=1, initialValue=1, name = GENERATOR, sequenceName = "sq_tb_perfil")
    @Id
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = "id_perfil", unique = true, nullable = false)
    public Integer getIdPerfil() {
        return idPerfil;
    }
    
    public void setIdPerfil(Integer idPerfil) {
        this.idPerfil = idPerfil;
    }
    
    @Column(name = "ds_perfil", nullable = false, unique = true)
    @NotNull
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "id_localizacao", nullable = false)
    @NotNull
    public Localizacao getLocalizacao() {
        return localizacao;
    }
    
    public void setLocalizacao(Localizacao localizacao) {
        this.localizacao = localizacao;
    }
    
    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "id_localizacao_pai_estrutura")
    public Localizacao getPaiDaEstrutura() {
        return paiDaEstrutura;
    }

    public void setPaiDaEstrutura(Localizacao paiDaEstrutura) {
        this.paiDaEstrutura = paiDaEstrutura;
    }

    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "id_papel", nullable = false)
    @NotNull
    public Papel getPapel() {
        return papel;
    }
    
    public void setPapel(Papel papel) {
        this.papel = papel;
    }

    @Column(name = ATIVO, nullable = false)
    @NotNull
    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
    
    @Override
    public String toString() {
        return getDescricao();
    }

}
