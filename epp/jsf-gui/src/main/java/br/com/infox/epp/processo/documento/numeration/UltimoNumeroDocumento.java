package br.com.infox.epp.processo.documento.numeration;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.documento.entity.TipoModeloDocumento;

@Entity
@Table(name = "tb_ultimo_num_documento")
@NamedQueries({
		@NamedQuery(name = UltimoNumeroDocumento.GET_NEXT_VALUE, query = UltimoNumeroDocumento.GET_NEXT_VALUE_QUERY),
		@NamedQuery(name = UltimoNumeroDocumento.GET_NEXT_VALUE_BY_ANO, query = UltimoNumeroDocumento.GET_NEXT_VALUE_QUERY_BY_ANO) 
})
public class UltimoNumeroDocumento implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(allocationSize = 1, initialValue = 1, name = "UltimoNumeroDocumentoGenerator", sequenceName = "sq_ult_num_documento")
	@GeneratedValue(generator = "UltimoNumeroDocumentoGenerator", strategy = GenerationType.SEQUENCE)
	@Column(name = "id_ultimo_num_documento", unique = true, nullable = false)
	private Long id;

	@NotNull
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_modelo_documento", nullable = false)
	private TipoModeloDocumento tipoModeloDocumento;

	@Column(name = "nr_documento", nullable = false)
	private Long ultimoNumero;

	@Column(name = "nr_ano", nullable = true)
	private Integer ano;

	@Version
	@Column(name = "nr_version", nullable = false)
	private Long version = 0L;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	
	public TipoModeloDocumento getTipoModeloDocumento() {
		return tipoModeloDocumento;
	}

	public void setTipoModeloDocumento(TipoModeloDocumento tipoModeloDocumento) {
		this.tipoModeloDocumento = tipoModeloDocumento;
	}

	public Long getUltimoNumero() {
		return ultimoNumero;
	}

	public void setUltimoNumero(Long ultimoNumero) {
		this.ultimoNumero = ultimoNumero;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof UltimoNumeroDocumento))
			return false;
		UltimoNumeroDocumento other = (UltimoNumeroDocumento) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

	public static final String GET_NEXT_VALUE = "getNextValueForUltimoNumeroDocumento";
	public static final String GET_NEXT_VALUE_QUERY = "select o from UltimoNumeroDocumento o where o.tipoModeloDocumento = :tipoModeloDocumento and o.ano is null ";
	public static final String GET_NEXT_VALUE_BY_ANO = "getNextValueForUltimoNumeroDocumentoByAno";
	public static final String GET_NEXT_VALUE_QUERY_BY_ANO = "select o from UltimoNumeroDocumento o where o.tipoModeloDocumento = :tipoModeloDocumento and o.ano = :ano ";
	
}
