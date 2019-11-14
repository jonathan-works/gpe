package br.com.infox.epp.documento.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.processo.documento.query.DocumentoBinarioQuery;

@Entity
@Table(name = DocumentoBinario.TABLE_NAME)
@NamedQueries({
    @NamedQuery(name = DocumentoBinarioQuery.EXISTE_BINARIO, query = DocumentoBinarioQuery.EXISTE_BINARIO_QUERY)
})
public class DocumentoBinario implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "tb_documento_binario";

    @Id
    @NotNull
    @Column(name = "id_documento_binario", unique = true, nullable = false)
    private Integer id;
    
    @NotNull
    @Column(name = "ob_documento_binario", nullable = false)
    private byte[] documentoBinario;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public byte[] getDocumentoBinario() {
		return documentoBinario;
	}

	public void setDocumentoBinario(byte[] documentoBinario) {
		this.documentoBinario = documentoBinario;
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
		if (!(obj instanceof DocumentoBinario))
			return false;
		DocumentoBinario other = (DocumentoBinario) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}
	
}
