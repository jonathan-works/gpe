package br.com.infox.epp.access.entity;

import static br.com.infox.core.persistence.ORConstants.ATIVO;
import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.epp.access.query.PerfilQuery.COL_ID_PERFIL;
import static javax.persistence.FetchType.EAGER;

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

import br.com.infox.epp.access.query.PerfilQuery;

@Entity
@Table(name = "tb_perfil")
@NamedQueries({
    @NamedQuery(name = PerfilQuery.LIST_PERFIS_DENTRO_DE_ESTRUTURA, query = PerfilQuery.LIST_PERFIS_DENTRO_DE_ESTRUTURA_QUERY)
})
public class Perfil {
    
    private Integer idPerfil;
    private String descricao;
    private Localizacao paiDaEstrutura;
    private PerfilTemplate perfilTemplate;
    private Boolean ativo;
    
    public Perfil() {
        perfilTemplate = new PerfilTemplate();
    }
    
    @SequenceGenerator(allocationSize=1, initialValue=1, name = GENERATOR, sequenceName = "sq_tb_perfil")
    @Id
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = COL_ID_PERFIL, unique = true, nullable = false)
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
    @JoinColumn(name = "id_localizacao_pai_estrutura")
    public Localizacao getPaiDaEstrutura() {
        return paiDaEstrutura;
    }

    public void setPaiDaEstrutura(Localizacao paiDaEstrutura) {
        this.paiDaEstrutura = paiDaEstrutura;
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
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getAtivo() == null) ? 0 : getAtivo().hashCode());
        result = prime * result
                + ((getDescricao() == null) ? 0 : getDescricao().hashCode());
        result = prime * result
                + ((getIdPerfil() == null) ? 0 : getIdPerfil().hashCode());
        result = prime * result
                + ((getPerfilTemplate().getLocalizacao() == null) ? 0 : getPerfilTemplate().getLocalizacao().hashCode());
        result = prime * result
                + ((getPaiDaEstrutura() == null) ? 0 : getPaiDaEstrutura().hashCode());
        result = prime * result + ((getPerfilTemplate().getPapel() == null) ? 0 : getPerfilTemplate().getPapel().hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof Perfil)) return false;
        Perfil other = (Perfil) obj;
        if (getAtivo() == null) {
            if (other.getAtivo() != null) return false;
        } else if (!getAtivo().equals(other.getAtivo())) return false;
        if (getDescricao() == null) {
            if (other.getDescricao() != null) return false;
        } else if (!getDescricao().equals(other.getDescricao())) return false;
        if (getIdPerfil() == null) {
            if (other.getIdPerfil() != null) return false;
        } else if (!getIdPerfil().equals(other.getIdPerfil())) return false;
        if (getPerfilTemplate().getLocalizacao() == null) {
            if (other.getPerfilTemplate().getLocalizacao() != null) return false;
        } else if (!getPerfilTemplate().getLocalizacao().equals(other.getPerfilTemplate().getLocalizacao())) return false;
        if (getPaiDaEstrutura() == null) {
            if (other.getPaiDaEstrutura() != null) return false;
        } else if (!getPaiDaEstrutura().equals(other.getPaiDaEstrutura())) return false;
        if (getPerfilTemplate().getPapel() == null) {
            if (other.getPerfilTemplate().getPapel() != null) return false;
        } else if (!getPerfilTemplate().getPapel().equals(other.getPerfilTemplate().getPapel())) return false;
        return true;
    }
    

}
