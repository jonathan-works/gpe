package br.com.infox.epp.estado;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name=Estado.TABLE_NAME, schema="tce")
@NamedQueries({
    @NamedQuery(name = EstadoQuery.ESTADO_BY_SIGLA, query = EstadoQuery.ESTADO_BY_SIGLA_QUERY)
})
public class Estado implements Serializable {

	static final String TABLE_NAME = "tb_estado";

    private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name="EstadoGenerator", sequenceName="tce.sq_estado", initialValue=1, allocationSize=1)
	@GeneratedValue(generator="EstadoGenerator", strategy=GenerationType.SEQUENCE)
	@Column(name="id_estado")
	private Long idEstado;
	
	@NotNull
	@Column(name="nm_estado", nullable=false)
	private String nome;
	
	@NotNull
	@Column(name="sg_estado", nullable=false)
	private String sigla;
	
	@NotNull
	@Column(name="cd_ibge", nullable=false)
	private Short codigoIbge;

	public Long getIdEstado() {
		return idEstado;
	}

	public void setIdEstado(Long idEstado) {
		this.idEstado = idEstado;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public Short getCodigoIbge() {
		return codigoIbge;
	}

	public void setCodigoIbge(Short codigoIbge) {
		this.codigoIbge = codigoIbge;
	}
	
	@Override
	public String toString() {
		return getNome();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((getIdEstado() == null) ? 0 : getIdEstado().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Estado))
			return false;
		Estado other = (Estado) obj;
		if (getIdEstado() == null) {
			if (other.getIdEstado() != null)
				return false;
		} else if (!getIdEstado().equals(other.getIdEstado()))
			return false;
		return true;
	}

}
