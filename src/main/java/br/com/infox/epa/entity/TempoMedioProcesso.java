package br.com.infox.epa.entity;

import java.io.Serializable;
import java.text.MessageFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.com.itx.util.HibernateUtil;

@Entity
@Table(name=TempoMedioProcesso.TABLE_NAME, schema="public")
public class TempoMedioProcesso implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "vs_tempo_medio_processo";

	private int idNaturezaCategoriaFluxo;
	private NaturezaCategoriaFluxo naturezaCategoriaFluxo;
	private Float tempoMedio;
	private Integer prazo;

	@Id
	@Column(name = "id_natureza_categoria_fluxo", nullable=false, insertable=false, updatable=false)
	public int getIdNaturezaCategoriaFluxo() {
		return idNaturezaCategoriaFluxo;
	}
	public void setIdNaturezaCategoriaFluxo(int idNaturezaCategoriaFluxo) {
		this.idNaturezaCategoriaFluxo = idNaturezaCategoriaFluxo;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_natureza_categoria_fluxo", insertable=false, updatable=false)
	public NaturezaCategoriaFluxo getNaturezaCategoriaFluxo() {
		return naturezaCategoriaFluxo;
	}
	public void setNaturezaCategoriaFluxo(
			NaturezaCategoriaFluxo naturezaCategoriaFluxo) {
		this.naturezaCategoriaFluxo = naturezaCategoriaFluxo;
	}
	
	@Column(name="nr_tempo_medio", insertable=false, updatable=false)
	public Float getTempoMedio() {
		return this.tempoMedio;
	}
	public void setTempoMedio(Float tempoMedio) {
		this.tempoMedio = tempoMedio;
	}
	
	@Column(name="qt_prazo", insertable=false, updatable=false)
	public Integer getPrazo() {
		return this.prazo;
	}
	public void setPrazo(Integer prazo) {
		this.prazo = prazo;
	}
	
	@Transient
	public String getTempoMedioFormatado() {
		if (this.tempoMedio == null) {
			return "0";
		}
		return String.format("%.2f", this.tempoMedio);
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
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (obj instanceof TempoMedioProcesso)
            return false;
        TempoMedioProcesso other = (TempoMedioProcesso) HibernateUtil.removeProxy(obj);
        if (idNaturezaCategoriaFluxo != other.idNaturezaCategoriaFluxo)
            return false;
        return true;
    }
    
    @Override
	public String toString() {
		return MessageFormat.format("{0}", this.naturezaCategoriaFluxo.toString());
	}
}
