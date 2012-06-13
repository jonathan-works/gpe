package br.com.infox.ibpm.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.validator.NotNull;

import br.com.infox.access.entity.Papel;

@Entity
@Table(name = "tb_tipo_processo_documento_papel", schema="public")
public class TipoProcessoDocumentoPapel implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	private Integer idTipoProcessoDocumentoPapel;
	private TipoProcessoDocumento tipoProcessoDocumento;
	private Papel papel;
	private Boolean obrigatorio;
	
	public TipoProcessoDocumentoPapel() {
	}

	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_tipo_processo_documento_papel")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_tipo_processo_documento_papel", nullable = false, unique = true)
	public Integer getIdTipoProcessoDocumentoPapel() {
		return idTipoProcessoDocumentoPapel;
	}

	public void setIdTipoProcessoDocumentoPapel(Integer idTipoProcessoDocumentoPapel) {
		this.idTipoProcessoDocumentoPapel = idTipoProcessoDocumentoPapel;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_processo_documento", nullable = false)
	@NotNull
	public TipoProcessoDocumento getTipoProcessoDocumento() {
		return tipoProcessoDocumento;
	}

	public void setTipoProcessoDocumento(TipoProcessoDocumento tipoProcessoDocumento) {
		this.tipoProcessoDocumento = tipoProcessoDocumento;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_papel", nullable = false)
	@NotNull
	public Papel getPapel() {
		return papel;
	}

	public void setPapel(Papel papel) {
		this.papel = papel;
	}

	@Column(name = "in_obrigatorio", nullable = false)
	@NotNull
	public Boolean getObrigatorio() {
		return obrigatorio;
	}

	public void setObrigatorio(Boolean obrigatorio) {
		this.obrigatorio = obrigatorio;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getIdTipoProcessoDocumentoPapel() == null) {
			return false;
		}
		if (!(obj instanceof TipoProcessoDocumentoPapel)) {
			return false;
		}
		TipoProcessoDocumentoPapel other = (TipoProcessoDocumentoPapel) obj;
		if (getIdTipoProcessoDocumentoPapel() != other.getIdTipoProcessoDocumentoPapel()) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getIdTipoProcessoDocumentoPapel();
		return result;
	}
}