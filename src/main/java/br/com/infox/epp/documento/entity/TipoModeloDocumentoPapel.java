package br.com.infox.epp.documento.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.com.infox.epp.access.entity.Papel;

@Entity
@Table(name = TipoModeloDocumentoPapel.TABLE_NAME, schema="public")
public class TipoModeloDocumentoPapel implements java.io.Serializable {

	public static final String TABLE_NAME = "tb_tipo_modelo_documento_papel";

	private static final long serialVersionUID = 1L;
	
	private int idTipoModeloDocumentoPapel;
	private TipoModeloDocumento tipoModeloDocumento;
	private Papel papel;
	
	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_tipo_modelo_documento_papel")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_tipo_modelo_documento_papel", unique = true, nullable = false)
	public int getIdTipoModeloDocumentoPapel() {
		return idTipoModeloDocumentoPapel;
	}
	
	public void setIdTipoModeloDocumentoPapel(int idTipoModeloDocumentoPapel) {
		this.idTipoModeloDocumentoPapel = idTipoModeloDocumentoPapel;
	}
	

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_tipo_modelo_documento", nullable=false)
	public TipoModeloDocumento getTipoModeloDocumento() {
		return tipoModeloDocumento;
	}
	
	public void setTipoModeloDocumento(TipoModeloDocumento tipoModeloDocumento) {
		this.tipoModeloDocumento = tipoModeloDocumento;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_papel", nullable=false)
	public Papel getPapel() {
		return papel;
	}
	
	public void setPapel(Papel papel) {
		this.papel = papel;
	}
	
	@Override
	public String toString() {
		return tipoModeloDocumento + " / " + papel;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof TipoModeloDocumentoPapel)) {
			return false;
		}
		TipoModeloDocumentoPapel other = (TipoModeloDocumentoPapel) obj;
		if (getIdTipoModeloDocumentoPapel() != other.getIdTipoModeloDocumentoPapel()) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getIdTipoModeloDocumentoPapel();
		return result;
	}
}