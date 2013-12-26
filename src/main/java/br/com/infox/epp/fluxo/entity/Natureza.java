package br.com.infox.epp.fluxo.entity;

import static br.com.infox.core.persistence.ORConstants.ATIVO;
import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.core.persistence.ORConstants.PUBLIC;
import static br.com.infox.epp.fluxo.query.NaturezaQuery.DESCRICAO_NATUREZA;
import static br.com.infox.epp.fluxo.query.NaturezaQuery.ID_NATUREZA;
import static br.com.infox.epp.fluxo.query.NaturezaQuery.NATUREZA_ATTRIBUTE;
import static br.com.infox.epp.fluxo.query.NaturezaQuery.OBRIGATORIO_PARTES;
import static br.com.infox.epp.fluxo.query.NaturezaQuery.SEQUENCE_NATUREZA;
import static br.com.infox.epp.fluxo.query.NaturezaQuery.TABLE_NATUREZA;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import br.com.infox.core.constants.LengthConstants;
import br.com.itx.util.HibernateUtil;

@Entity
@Table(name=TABLE_NATUREZA, schema=PUBLIC)
public class Natureza implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private int idNatureza;
	private String natureza;
	private Boolean hasPartes;
	private Boolean ativo;
	
	private List<NaturezaCategoriaFluxo> natCatFluxoList = 
		new ArrayList<NaturezaCategoriaFluxo>(0);
	
	@SequenceGenerator(name = GENERATOR, sequenceName = SEQUENCE_NATUREZA)
	@Id
	@GeneratedValue(generator = GENERATOR)
	@Column(name = ID_NATUREZA, unique = true, nullable = false)
	public int getIdNatureza() {
		return idNatureza;
	}
	public void setIdNatureza(int idNatureza) {
		this.idNatureza = idNatureza;
	}
	
	@Column(name=DESCRICAO_NATUREZA, length=LengthConstants.DESCRICAO_PEQUENA, nullable=false, unique=true)
	@Size(max=LengthConstants.DESCRICAO_PEQUENA)
	public String getNatureza() {
		return natureza;
	}
	
	public void setNatureza(String natureza) {
		this.natureza = natureza;
	}
	
	@Column(name=OBRIGATORIO_PARTES, nullable=false)
	public Boolean getHasPartes() {
		return hasPartes;
	}
	public void setHasPartes(Boolean hasPartes) {
		this.hasPartes = hasPartes;
	}
	@Column(name=ATIVO, nullable=false)
	public Boolean getAtivo() {
		return ativo;
	}
	
	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
	public void setNatCatFluxoList(List<NaturezaCategoriaFluxo> natCatFluxoList) {
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
        result = prime * result + idNatureza;
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
        if (!(obj instanceof Natureza)) {
            return false;
        }
        Natureza other = (Natureza) HibernateUtil.removeProxy(obj);
        if (idNatureza != other.idNatureza) {
            return false;
        }
        return true;
    }
	
}