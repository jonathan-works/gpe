package br.com.infox.epp.access.entity;

import static br.com.infox.core.constants.LengthConstants.*;
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
@Table(name="tb_perfil_template")
public class PerfilTemplate {

    private Integer id;
    private String descricao;
    private Localizacao localizacao;
    private Papel papel;
    private Boolean ativo;
    
    @Id
    @SequenceGenerator(allocationSize=1, initialValue=1, name = GENERATOR, sequenceName = "sq_perfil_template")
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    @Column(name="ds_perfil_temp", length=DESCRICAO_PADRAO, nullable=false)
    @NotNull
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "id_localizacao")
    public Localizacao getLocalizacao() {
        return localizacao;
    }
    
    public void setLocalizacao(Localizacao localizacao) {
        this.localizacao = localizacao;
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
