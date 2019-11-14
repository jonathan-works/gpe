package br.com.infox.epp.processo.documento.assinatura.entity;

import static br.com.infox.constants.LengthConstants.NOME_PADRAO;

import java.io.Serializable;
import java.text.MessageFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.constants.LengthConstants;
import br.com.infox.epp.documento.type.TipoAssinaturaEnum;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;

@Entity
@Table(name = RegistroAssinaturaSuficiente.TABLE_NAME)
public class RegistroAssinaturaSuficiente implements Serializable {

	@Id
	@SequenceGenerator(allocationSize = 1, initialValue = 1, name = GENERATOR_NAME, sequenceName = SEQUENCE_NAME)
	@GeneratedValue(generator = GENERATOR_NAME, strategy = GenerationType.SEQUENCE)
	@Column(name = COL_ID, unique = true, nullable = false)
	private Integer id;
	@NotNull
	@Size(min = LengthConstants.FLAG, max = NOME_PADRAO)
	@Column(name = COL_PAPEL, length = NOME_PADRAO, nullable = false)
	private String papel;
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = COL_TIPO_ASSINATURA, nullable = false)
	private TipoAssinaturaEnum tipoAssinatura;
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COL_DOCUMENTO_BIN, nullable = false)
	private DocumentoBin documentoBin;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPapel() {
		return papel;
	}

	public void setPapel(String papel) {
		this.papel = papel;
	}

	public TipoAssinaturaEnum getTipoAssinatura() {
		return tipoAssinatura;
	}

	public void setTipoAssinatura(TipoAssinaturaEnum tipoAssinatura) {
		this.tipoAssinatura = tipoAssinatura;
	}

	public DocumentoBin getDocumentoBin() {
		return documentoBin;
	}

	public void setDocumentoBin(DocumentoBin documentoBin) {
		this.documentoBin = documentoBin;
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
		if (!(obj instanceof RegistroAssinaturaSuficiente))
			return false;
		RegistroAssinaturaSuficiente other = (RegistroAssinaturaSuficiente) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return MessageFormat.format("{0} : {1}", papel,
				tipoAssinatura.getLabel());
	}

	public static final String COL_DOCUMENTO_BIN = "id_documento_bin";
	public static final String COL_TIPO_ASSINATURA = "tp_assinatura";
	public static final String COL_PAPEL = "ds_papel";
	public static final String COL_ID = "id_reg_assin_suf";
	public static final String SEQUENCE_NAME = "sq_reg_assin_suf";
	public static final String GENERATOR_NAME = "GeneratorRegistroAssinaturaSuficiente";
	public static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "tb_reg_assin_suf";
}
