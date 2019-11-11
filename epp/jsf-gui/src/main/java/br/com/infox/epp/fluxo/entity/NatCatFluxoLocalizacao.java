package br.com.infox.epp.fluxo.entity;

import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.epp.fluxo.query.NatCatFluxoLocalizacaoQuery.COUNT_NCF_LOCALIZACAO_BY_LOC_NCF;
import static br.com.infox.epp.fluxo.query.NatCatFluxoLocalizacaoQuery.COUNT_NCF_LOCALIZACAO_BY_LOC_NCF_QUERY;
import static br.com.infox.epp.fluxo.query.NatCatFluxoLocalizacaoQuery.GET_NAT_CAT_FLUXO_LOCALIZACAO_BY_LOC_NCF;
import static br.com.infox.epp.fluxo.query.NatCatFluxoLocalizacaoQuery.GET_NAT_CAT_FLUXO_LOCALIZACAO_BY_LOC_NCF_QUERY;
import static br.com.infox.epp.fluxo.query.NatCatFluxoLocalizacaoQuery.HERANCA;
import static br.com.infox.epp.fluxo.query.NatCatFluxoLocalizacaoQuery.ID_LOCALIZACAO;
import static br.com.infox.epp.fluxo.query.NatCatFluxoLocalizacaoQuery.ID_NAT_CAT_FLUXO;
import static br.com.infox.epp.fluxo.query.NatCatFluxoLocalizacaoQuery.ID_NAT_CAT_FLUXO_LOCALIZACAO;
import static br.com.infox.epp.fluxo.query.NatCatFluxoLocalizacaoQuery.LIST_BY_NAT_CAT_FLUXO;
import static br.com.infox.epp.fluxo.query.NatCatFluxoLocalizacaoQuery.LIST_BY_NAT_CAT_FLUXO_QUERY;
import static br.com.infox.epp.fluxo.query.NatCatFluxoLocalizacaoQuery.SEQUENCE_NAT_CAT_FLUXO_LOCALIZACAO;
import static br.com.infox.epp.fluxo.query.NatCatFluxoLocalizacaoQuery.TABLE_NAT_CAT_FLUXO_LOCALIZACAO;

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
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.access.entity.Localizacao;

@Entity
@Table(name = TABLE_NAT_CAT_FLUXO_LOCALIZACAO, uniqueConstraints = { @UniqueConstraint(columnNames = {
    ID_NAT_CAT_FLUXO, ID_LOCALIZACAO }) })
@NamedQueries(value = {
    @NamedQuery(name = GET_NAT_CAT_FLUXO_LOCALIZACAO_BY_LOC_NCF, query = GET_NAT_CAT_FLUXO_LOCALIZACAO_BY_LOC_NCF_QUERY),
    @NamedQuery(name = COUNT_NCF_LOCALIZACAO_BY_LOC_NCF, query = COUNT_NCF_LOCALIZACAO_BY_LOC_NCF_QUERY),
    @NamedQuery(name = LIST_BY_NAT_CAT_FLUXO, query = LIST_BY_NAT_CAT_FLUXO_QUERY) })
public class NatCatFluxoLocalizacao implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(allocationSize=1, initialValue=1, name = GENERATOR, sequenceName = SEQUENCE_NAT_CAT_FLUXO_LOCALIZACAO)
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = ID_NAT_CAT_FLUXO_LOCALIZACAO, unique = true, nullable = false)
    private Integer idNatCatFluxoLocalizacao;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ID_NAT_CAT_FLUXO, nullable = false)
    @NotNull(message = "#{infoxMessages['beanValidation.notNull']}")
    private NaturezaCategoriaFluxo naturezaCategoriaFluxo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ID_LOCALIZACAO, nullable = false)
    @NotNull(message = "#{infoxMessages['beanValidation.notNull']}")
    private Localizacao localizacao;
    
    @Column(name = HERANCA, nullable = false)
    private Boolean heranca = Boolean.FALSE;

    public NatCatFluxoLocalizacao() {
    }

    public NatCatFluxoLocalizacao(NaturezaCategoriaFluxo naturezaCategoriaFluxo, Localizacao localizacao, boolean heranca) {
        this.naturezaCategoriaFluxo = naturezaCategoriaFluxo;
        this.localizacao = localizacao;
        this.heranca = heranca;
    }

    public Integer getIdNatCatFluxoLocalizacao() {
        return idNatCatFluxoLocalizacao;
    }

    public void setIdNatCatFluxoLocalizacao(Integer idNatCatFluxoLocalizacao) {
        this.idNatCatFluxoLocalizacao = idNatCatFluxoLocalizacao;
    }

    public NaturezaCategoriaFluxo getNaturezaCategoriaFluxo() {
        return naturezaCategoriaFluxo;
    }

    public void setNaturezaCategoriaFluxo(
            NaturezaCategoriaFluxo naturezaCategoriaFluxo) {
        this.naturezaCategoriaFluxo = naturezaCategoriaFluxo;
    }

    public Localizacao getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(Localizacao localizacao) {
        this.localizacao = localizacao;
    }

    public void setHeranca(Boolean heranca) {
        this.heranca = heranca;
    }

    public Boolean getHeranca() {
        return heranca;
    }

    @Transient
    public boolean isAtivo() {
        return (naturezaCategoriaFluxo.isAtivo() && localizacao.getAtivo());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((getIdNatCatFluxoLocalizacao() == null) ? 0 : getIdNatCatFluxoLocalizacao().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof NatCatFluxoLocalizacao)) {
            return false;
        }
        NatCatFluxoLocalizacao other = (NatCatFluxoLocalizacao) obj;
        if (getIdNatCatFluxoLocalizacao() == null) {
            if (other.getIdNatCatFluxoLocalizacao() != null) {
                return false;
            }
        } else if (!getIdNatCatFluxoLocalizacao().equals(other.getIdNatCatFluxoLocalizacao())) {
            return false;
        }
        return true;
    }

}
