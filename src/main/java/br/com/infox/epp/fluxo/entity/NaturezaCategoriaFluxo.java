package br.com.infox.epp.fluxo.entity;

import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.core.persistence.ORConstants.PUBLIC;
import static br.com.infox.epp.fluxo.query.NaturezaCategoriaFluxoQuery.BY_RELATIONSHIP_QUERY;
import static br.com.infox.epp.fluxo.query.NaturezaCategoriaFluxoQuery.ID_CATEGORIA;
import static br.com.infox.epp.fluxo.query.NaturezaCategoriaFluxoQuery.ID_FLUXO;
import static br.com.infox.epp.fluxo.query.NaturezaCategoriaFluxoQuery.ID_NATUREZA;
import static br.com.infox.epp.fluxo.query.NaturezaCategoriaFluxoQuery.ID_NATUREZA_CATEGORIA_FLUXO;
import static br.com.infox.epp.fluxo.query.NaturezaCategoriaFluxoQuery.LIST_BY_NATUREZA;
import static br.com.infox.epp.fluxo.query.NaturezaCategoriaFluxoQuery.LIST_BY_NATUREZA_QUERY;
import static br.com.infox.epp.fluxo.query.NaturezaCategoriaFluxoQuery.LIST_BY_RELATIONSHIP;
import static br.com.infox.epp.fluxo.query.NaturezaCategoriaFluxoQuery.NATUREZA_CATEGORIA_FLUXO_ATTRIBUTE;
import static br.com.infox.epp.fluxo.query.NaturezaCategoriaFluxoQuery.SEQUENCE_NATRUEZA_CATEGORIA_FLUXO;
import static br.com.infox.epp.fluxo.query.NaturezaCategoriaFluxoQuery.TABLE_NATUREZA_CATEGORIA_FLUXO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
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

import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.itx.util.HibernateUtil;

@Entity
@Table(name=TABLE_NATUREZA_CATEGORIA_FLUXO, schema=PUBLIC, 
    uniqueConstraints={
        @UniqueConstraint(columnNames={ID_NATUREZA, ID_CATEGORIA, ID_FLUXO})
})
@NamedQueries(value={
    @NamedQuery(name=LIST_BY_RELATIONSHIP, query=BY_RELATIONSHIP_QUERY),
    @NamedQuery(name=LIST_BY_NATUREZA, query=LIST_BY_NATUREZA_QUERY)
})
public class NaturezaCategoriaFluxo implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private int idNaturezaCategoriaFluxo;
	private Natureza natureza;
	private Categoria categoria;
	private Fluxo fluxo;
	
	private List<ProcessoEpa> processoEpaList = new ArrayList<ProcessoEpa>(0);
	
	private List<NatCatFluxoLocalizacao> natCatFluxoLocalizacaoList = 
		new ArrayList<NatCatFluxoLocalizacao>(0);
	
	public void setIdNaturezaCategoriaFluxo(int idNaturezaCategoriaFluxo) {
		this.idNaturezaCategoriaFluxo = idNaturezaCategoriaFluxo;
	}

	@SequenceGenerator(name = GENERATOR, sequenceName = SEQUENCE_NATRUEZA_CATEGORIA_FLUXO)
	@Id
	@GeneratedValue(generator = GENERATOR)
	@Column(name = ID_NATUREZA_CATEGORIA_FLUXO, unique = true, nullable = false)
	public int getIdNaturezaCategoriaFluxo() {
		return idNaturezaCategoriaFluxo;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = ID_NATUREZA, nullable=false)
	@NotNull(message = "#{messages['beanValidation.notNull']}")
	public Natureza getNatureza() {
		return natureza;
	}
	
	public void setNatureza(Natureza natureza) {
		this.natureza = natureza;
	}

	public void setFluxo(Fluxo fluxo) {
		this.fluxo = fluxo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = ID_FLUXO, nullable=false)
	@NotNull(message = "#{messages['beanValidation.notNull']}")
	public Fluxo getFluxo() {
		return fluxo;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = ID_CATEGORIA, nullable=false)
	@NotNull(message = "#{messages['beanValidation.notNull']}")
	public Categoria getCategoria() {
		return categoria;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = NATUREZA_CATEGORIA_FLUXO_ATTRIBUTE)
	public List<NatCatFluxoLocalizacao> getNatCatFluxoLocalizacaoList() {
		return natCatFluxoLocalizacaoList;
	}
	
	public void setNatCatFluxoLocalizacaoList(List<NatCatFluxoLocalizacao> naturezaLocalizacaoList) {
		this.natCatFluxoLocalizacaoList = naturezaLocalizacaoList;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(natureza)
		  .append(" - ")
		  .append(categoria)
		  .append(" - ")
		  .append(fluxo);
		return sb.toString();
	}
	
	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + idNaturezaCategoriaFluxo;
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

    public void setProcessoEpaList(List<ProcessoEpa> processoEpaList) {
		this.processoEpaList = processoEpaList;
	}

	@OneToMany(fetch=FetchType.LAZY, mappedBy=NATUREZA_CATEGORIA_FLUXO_ATTRIBUTE)
	public List<ProcessoEpa> getProcessoEpaList() {
		return processoEpaList;
	}
	
	@Transient
	public boolean isAtivo(){
		return true;
	}
	
}