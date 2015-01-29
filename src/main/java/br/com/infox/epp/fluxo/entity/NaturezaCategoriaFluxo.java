package br.com.infox.epp.fluxo.entity;

import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.epp.fluxo.query.NaturezaCategoriaFluxoQuery.ATIVOS_BY_FLUXO;
import static br.com.infox.epp.fluxo.query.NaturezaCategoriaFluxoQuery.ATIVOS_BY_FLUXO_QUERY;
import static br.com.infox.epp.fluxo.query.NaturezaCategoriaFluxoQuery.BY_RELATIONSHIP_QUERY;
import static br.com.infox.epp.fluxo.query.NaturezaCategoriaFluxoQuery.ID_CATEGORIA;
import static br.com.infox.epp.fluxo.query.NaturezaCategoriaFluxoQuery.ID_FLUXO;
import static br.com.infox.epp.fluxo.query.NaturezaCategoriaFluxoQuery.ID_NATUREZA;
import static br.com.infox.epp.fluxo.query.NaturezaCategoriaFluxoQuery.ID_NATUREZA_CATEGORIA_FLUXO;
import static br.com.infox.epp.fluxo.query.NaturezaCategoriaFluxoQuery.LIST_BY_NATUREZA;
import static br.com.infox.epp.fluxo.query.NaturezaCategoriaFluxoQuery.LIST_BY_NATUREZA_QUERY;
import static br.com.infox.epp.fluxo.query.NaturezaCategoriaFluxoQuery.LIST_BY_RELATIONSHIP;
import static br.com.infox.epp.fluxo.query.NaturezaCategoriaFluxoQuery.NATCATFLUXO_BY_DS_NATUREZA_DS_CATEGORIA;
import static br.com.infox.epp.fluxo.query.NaturezaCategoriaFluxoQuery.NATCATFLUXO_BY_DS_NATUREZA_DS_CATEGORIA_DISPONIVEIS;
import static br.com.infox.epp.fluxo.query.NaturezaCategoriaFluxoQuery.NATCATFLUXO_BY_DS_NATUREZA_DS_CATEGORIA_DISPONIVEIS_QUERY;
import static br.com.infox.epp.fluxo.query.NaturezaCategoriaFluxoQuery.NATCATFLUXO_BY_DS_NATUREZA_DS_CATEGORIA_QUERY;
import static br.com.infox.epp.fluxo.query.NaturezaCategoriaFluxoQuery.NATUREZA_CATEGORIA_FLUXO_ATTRIBUTE;
import static br.com.infox.epp.fluxo.query.NaturezaCategoriaFluxoQuery.SEQUENCE_NATRUEZA_CATEGORIA_FLUXO;
import static br.com.infox.epp.fluxo.query.NaturezaCategoriaFluxoQuery.TABLE_NATUREZA_CATEGORIA_FLUXO;
import static java.text.MessageFormat.format;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.hibernate.util.HibernateUtil;

@Entity
@Table(name = TABLE_NATUREZA_CATEGORIA_FLUXO, uniqueConstraints = { @UniqueConstraint(columnNames = {
    ID_NATUREZA, ID_CATEGORIA, ID_FLUXO }) })
@NamedQueries(value = {
    @NamedQuery(name = ATIVOS_BY_FLUXO, query = ATIVOS_BY_FLUXO_QUERY),
    @NamedQuery(name = LIST_BY_RELATIONSHIP, query = BY_RELATIONSHIP_QUERY),
    @NamedQuery(name = LIST_BY_NATUREZA, query = LIST_BY_NATUREZA_QUERY),
    @NamedQuery(name = NATCATFLUXO_BY_DS_NATUREZA_DS_CATEGORIA_DISPONIVEIS, query = NATCATFLUXO_BY_DS_NATUREZA_DS_CATEGORIA_DISPONIVEIS_QUERY),
    @NamedQuery(name = NATCATFLUXO_BY_DS_NATUREZA_DS_CATEGORIA, query = NATCATFLUXO_BY_DS_NATUREZA_DS_CATEGORIA_QUERY)
})
public class NaturezaCategoriaFluxo implements Serializable {

    private static final long serialVersionUID = 1L;

    @SequenceGenerator(allocationSize=1, initialValue=1, name = GENERATOR, sequenceName = SEQUENCE_NATRUEZA_CATEGORIA_FLUXO)
    @Id
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = ID_NATUREZA_CATEGORIA_FLUXO, unique = true, nullable = false)
    private Integer idNaturezaCategoriaFluxo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ID_NATUREZA, nullable = false)
    @NotNull(message = "#{infoxMessages['beanValidation.notNull']}")
    private Natureza natureza;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ID_CATEGORIA, nullable = false)
    @NotNull(message = "#{infoxMessages['beanValidation.notNull']}")
    private Categoria categoria;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ID_FLUXO, nullable = false)
    @NotNull(message = "#{infoxMessages['beanValidation.notNull']}")
    private Fluxo fluxo;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = NATUREZA_CATEGORIA_FLUXO_ATTRIBUTE)
    private List<Processo> processoList = new ArrayList<>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = NATUREZA_CATEGORIA_FLUXO_ATTRIBUTE)
    private List<NatCatFluxoLocalizacao> natCatFluxoLocalizacaoList = new ArrayList<NatCatFluxoLocalizacao>(0);

    public NaturezaCategoriaFluxo() {
    }

    public NaturezaCategoriaFluxo(final Natureza natureza,
            final Categoria categoria, final Fluxo fluxo) {
        this.natureza = natureza;
        this.categoria = categoria;
        this.fluxo = fluxo;
    }
    

    public Integer getIdNaturezaCategoriaFluxo() {
		return idNaturezaCategoriaFluxo;
	}

	public void setIdNaturezaCategoriaFluxo(Integer idNaturezaCategoriaFluxo) {
		this.idNaturezaCategoriaFluxo = idNaturezaCategoriaFluxo;
	}

	public Natureza getNatureza() {
		return natureza;
	}

	public void setNatureza(Natureza natureza) {
		this.natureza = natureza;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public Fluxo getFluxo() {
		return fluxo;
	}

	public void setFluxo(Fluxo fluxo) {
		this.fluxo = fluxo;
	}

	public List<Processo> getProcessoList() {
		return processoList;
	}

	public void setProcessoList(List<Processo> processoList) {
		this.processoList = processoList;
	}

	public List<NatCatFluxoLocalizacao> getNatCatFluxoLocalizacaoList() {
		return natCatFluxoLocalizacaoList;
	}

	public void setNatCatFluxoLocalizacaoList(List<NatCatFluxoLocalizacao> natCatFluxoLocalizacaoList) {
		this.natCatFluxoLocalizacaoList = natCatFluxoLocalizacaoList;
	}

	@Override
    public String toString() {
        return format("{0} - {1} - {2}", natureza, categoria, fluxo);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + (idNaturezaCategoriaFluxo == null ? 0 : idNaturezaCategoriaFluxo.hashCode());
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
        if (!(obj instanceof NaturezaCategoriaFluxo)) {
            return false;
        }
        NaturezaCategoriaFluxo other = (NaturezaCategoriaFluxo) HibernateUtil.removeProxy(obj);
        if (idNaturezaCategoriaFluxo != other.idNaturezaCategoriaFluxo) {
            return false;
        }
        return true;
    }

    @Transient
    public boolean isAtivo() {
        return true;
    }
    
    @Transient
    public boolean hasItens() {
        return !getCategoria().getCategoriaItemList().isEmpty();
    }
    
    @Transient
    public boolean necessitaPartes() {
        return getNatureza().getHasPartes();
    }

}
