package br.com.infox.ibpm.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.validator.NotNull;

@Entity
@Table(name = "tb_variavel_tipo_modelo", schema="public")
public class VariavelTipoModelo implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	private Integer idVariavelTipoModelo;
	private Variavel variavel;
	private TipoModeloDocumento tipoModeloDocumento;
		
	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_variavel_tipo_modelo")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_variavel_tipo_modelo", unique = true, nullable = false)
	public Integer getIdVariavelTipoModelo() {
		return idVariavelTipoModelo;
	}

	public void setIdVariavelTipoModelo(Integer idVariavelTipoModelo) {
		this.idVariavelTipoModelo = idVariavelTipoModelo;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_variavel", nullable = false)
	@NotNull
	public Variavel getVariavel() {
		return variavel;
	}

	public void setVariavel(Variavel variavel) {
		this.variavel = variavel;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_modelo_documento", nullable = false)
	@NotNull
	public TipoModeloDocumento getTipoModeloDocumento() {
		return tipoModeloDocumento;
	}

	public void setTipoModeloDocumento(TipoModeloDocumento tipoModeloDocumento) {
		this.tipoModeloDocumento = tipoModeloDocumento;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getIdVariavelTipoModelo() == null) {
			return false;
		}
		if (!(obj instanceof VariavelTipoModelo)) {
			return false;
		}
		VariavelTipoModelo other = (VariavelTipoModelo) obj;
		return getIdVariavelTipoModelo().equals(other.getIdVariavelTipoModelo());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getIdVariavelTipoModelo();
		return result;
	}
}