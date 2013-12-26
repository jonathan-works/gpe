package br.com.infox.epp.documento.entity;

import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.core.persistence.ORConstants.PUBLIC;
import static br.com.infox.epp.documento.query.TipoModeloDocumentoPapelQuery.ID_PAPEL;
import static br.com.infox.epp.documento.query.TipoModeloDocumentoPapelQuery.ID_TIPO_MODELO_DOCUMENTO;
import static br.com.infox.epp.documento.query.TipoModeloDocumentoPapelQuery.ID_TIPO_MODELO_DOCUMENTO_PAPEL;
import static br.com.infox.epp.documento.query.TipoModeloDocumentoPapelQuery.SEQUENCE_TIPO_MODELO_DOCUMENTO_PAPEL;
import static br.com.infox.epp.documento.query.TipoModeloDocumentoPapelQuery.TABLE_TIPO_MODELO_DOCUMENTO_PAPEL;

import java.io.Serializable;

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
@Table(name = TABLE_TIPO_MODELO_DOCUMENTO_PAPEL, schema=PUBLIC)
public class TipoModeloDocumentoPapel implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int idTipoModeloDocumentoPapel;
	private TipoModeloDocumento tipoModeloDocumento;
	private Papel papel;
	
	@SequenceGenerator(name = GENERATOR, sequenceName = SEQUENCE_TIPO_MODELO_DOCUMENTO_PAPEL)
	@Id
	@GeneratedValue(generator = GENERATOR)
	@Column(name = ID_TIPO_MODELO_DOCUMENTO_PAPEL, unique = true, nullable = false)
	public int getIdTipoModeloDocumentoPapel() {
		return idTipoModeloDocumentoPapel;
	}
	
	public void setIdTipoModeloDocumentoPapel(int idTipoModeloDocumentoPapel) {
		this.idTipoModeloDocumentoPapel = idTipoModeloDocumentoPapel;
	}
	

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name=ID_TIPO_MODELO_DOCUMENTO, nullable=false)
	public TipoModeloDocumento getTipoModeloDocumento() {
		return tipoModeloDocumento;
	}
	
	public void setTipoModeloDocumento(TipoModeloDocumento tipoModeloDocumento) {
		this.tipoModeloDocumento = tipoModeloDocumento;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name=ID_PAPEL, nullable=false)
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