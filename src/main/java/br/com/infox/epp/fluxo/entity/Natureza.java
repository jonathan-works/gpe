package br.com.infox.epp.fluxo.entity;

import static br.com.infox.core.constants.LengthConstants.DESCRICAO_PEQUENA;
import static br.com.infox.core.constants.LengthConstants.FLAG;
import static br.com.infox.core.persistence.ORConstants.ATIVO;
import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.epp.fluxo.query.NaturezaQuery.DESCRICAO_NATUREZA;
import static br.com.infox.epp.fluxo.query.NaturezaQuery.ID_NATUREZA;
import static br.com.infox.epp.fluxo.query.NaturezaQuery.LOCKED;
import static br.com.infox.epp.fluxo.query.NaturezaQuery.NATUREZA_ATTRIBUTE;
import static br.com.infox.epp.fluxo.query.NaturezaQuery.NUMERO_PARTES_FISICAS;
import static br.com.infox.epp.fluxo.query.NaturezaQuery.NUMERO_PARTES_JURIDICAS;
import static br.com.infox.epp.fluxo.query.NaturezaQuery.OBRIGATORIO_PARTES;
import static br.com.infox.epp.fluxo.query.NaturezaQuery.SEQUENCE_NATUREZA;
import static br.com.infox.epp.fluxo.query.NaturezaQuery.TABLE_NATUREZA;
import static br.com.infox.epp.fluxo.query.NaturezaQuery.TIPO_PARTES;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.epp.processo.partes.type.ParteProcessoEnum;
import br.com.infox.hibernate.util.HibernateUtil;

@Entity
@Table(name = TABLE_NATUREZA)
public class Natureza implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private Integer idNatureza;
    private String natureza;
    private Boolean hasPartes;
    private ParteProcessoEnum tipoPartes;
    private Integer numeroPartesFisicas;
    private Integer numeroPartesJuridicas;
    private Boolean locked = Boolean.FALSE;
    private Boolean ativo;

    private List<NaturezaCategoriaFluxo> natCatFluxoList = new ArrayList<NaturezaCategoriaFluxo>(0);

    public Natureza() {
    }

    public Natureza(final String natureza, final Boolean hasPartes,
            final ParteProcessoEnum tipoPartes, final Integer numeroPartes,
            final Boolean ativo) {
        this.natureza = natureza;
        this.hasPartes = hasPartes;
        this.tipoPartes = tipoPartes;
        this.numeroPartesFisicas = numeroPartes;
        this.ativo = ativo;
    }

    @SequenceGenerator(name = GENERATOR, sequenceName = SEQUENCE_NATUREZA)
    @Id
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = ID_NATUREZA, unique = true, nullable = false)
    public Integer getIdNatureza() {
        return idNatureza;
    }

    public void setIdNatureza(final Integer idNatureza) {
        this.idNatureza = idNatureza;
    }

    @Column(name = DESCRICAO_NATUREZA, length = DESCRICAO_PEQUENA, nullable = false, unique = true)
    @Size(min = FLAG, max = DESCRICAO_PEQUENA)
    public String getNatureza() {
        return natureza;
    }

    public void setNatureza(final String natureza) {
        this.natureza = natureza;
    }

    @Column(name = OBRIGATORIO_PARTES, nullable = false)
    @NotNull
    public Boolean getHasPartes() {
        return hasPartes;
    }

    public void setHasPartes(final Boolean hasPartes) {
        this.hasPartes = hasPartes;
    }

    @Column(name = TIPO_PARTES)
    @Enumerated(EnumType.STRING)
    public ParteProcessoEnum getTipoPartes() {
        return tipoPartes;
    }

    public void setTipoPartes(ParteProcessoEnum tipoPartes) {
        this.tipoPartes = tipoPartes;
    }

    @Column(name = NUMERO_PARTES_FISICAS)
    public Integer getNumeroPartesFisicas() {
        return numeroPartesFisicas;
    }

    public void setNumeroPartesFisicas(Integer numeroPartesFisicas) {
        this.numeroPartesFisicas = numeroPartesFisicas;
    }
    
    @Column(name = NUMERO_PARTES_JURIDICAS)
    public Integer getNumeroPartesJuridicas(){
        return numeroPartesJuridicas;
    }
    
    public void setNumeroPartesJuridicas(Integer numeroPartesJuridicas) {
        this.numeroPartesJuridicas = numeroPartesJuridicas;
    }

    @Column(name = LOCKED, nullable = false)
    @NotNull
    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    @Column(name = ATIVO, nullable = false)
    @NotNull
    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(final Boolean ativo) {
        this.ativo = ativo;
    }

    public void setNatCatFluxoList(
            final List<NaturezaCategoriaFluxo> natCatFluxoList) {
        this.natCatFluxoList = natCatFluxoList;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = NATUREZA_ATTRIBUTE)
    public List<NaturezaCategoriaFluxo> getNatCatFluxoList() {
        return natCatFluxoList;
    }

    @Override
    public String toString() {
        return natureza;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + (idNatureza == null ? 0 : idNatureza.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Natureza)) {
            return false;
        }
        final Natureza other = (Natureza) HibernateUtil.removeProxy(obj);
        if (idNatureza != other.idNatureza) {
            return false;
        }
        return true;
    }
    
    public boolean apenasPessoaFisica() {
        return ParteProcessoEnum.F.equals(getTipoPartes());
    }

    public boolean apenasPessoaJuridica() {
        return ParteProcessoEnum.J.equals(getTipoPartes());
    }

}
