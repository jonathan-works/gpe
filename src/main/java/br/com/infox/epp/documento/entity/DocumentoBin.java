package br.com.infox.epp.documento.entity;

import static br.com.infox.epp.documento.query.DocumentoBinQuery.DOCUMENTO_BIN;
import static br.com.infox.epp.documento.query.DocumentoBinQuery.ID_DOCUMENTO_BIN;
import static br.com.infox.epp.documento.query.DocumentoBinQuery.TABLE_DOCUMENTO_BIN;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.com.infox.core.util.ArrayUtil;

@Entity
@Table(name = TABLE_DOCUMENTO_BIN)
public class DocumentoBin implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private int idDocumentoBin;
    private byte[] documentoBin;

    @Id
    @Column(name = ID_DOCUMENTO_BIN, unique = true, nullable = false)
    @NotNull
    public int getIdDocumentoBin() {
        return this.idDocumentoBin;
    }

    public void setIdDocumentoBin(int idDocumentoBin) {
        this.idDocumentoBin = idDocumentoBin;
    }

    @Column(name = DOCUMENTO_BIN, nullable = false)
    @NotNull
    public byte[] getDocumentoBin() {
        return ArrayUtil.copyOf(documentoBin);
    }

    public void setDocumentoBin(byte[] documentoBin) {
        this.documentoBin = ArrayUtil.copyOf(documentoBin);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DocumentoBin)) {
            return false;
        }
        DocumentoBin other = (DocumentoBin) obj;
        if (getIdDocumentoBin() != other.getIdDocumentoBin()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getIdDocumentoBin();
        return result;
    }

}
