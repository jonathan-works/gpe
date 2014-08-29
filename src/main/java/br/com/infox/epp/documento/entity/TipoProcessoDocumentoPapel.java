package br.com.infox.epp.documento.entity;

import java.io.Serializable;

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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.documento.query.TipoProcessoDocumentoQuery;
import br.com.infox.epp.documento.type.TipoAssinaturaEnum;

@Entity
@Table(name = "tb_tipo_processo_documento_papel")
@NamedQueries(value = { 
		@NamedQuery(name = TipoProcessoDocumentoQuery.ASSINATURA_OBRIGATORIA, query = TipoProcessoDocumentoQuery.ASSINATURA_OBRIGATORIA_QUERY) 
})
public class TipoProcessoDocumentoPapel implements Serializable {
    
	private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(allocationSize=1, initialValue=1, name = "generator", sequenceName = "sq_tb_tipo_processo_documento_papel")
    @GeneratedValue(generator = "generator", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_tipo_processo_documento_papel", nullable = false, unique = true)
    private Integer idTipoProcessoDocumentoPapel;
    
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo_processo_documento", nullable = false)
    private TipoProcessoDocumento tipoProcessoDocumento;
    
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_papel", nullable = false)
    private Papel papel;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tp_assinatura", nullable=false)
    private TipoAssinaturaEnum tipoAssinatura;
    
    @NotNull
    @Column(name = "in_redator", nullable = false)
    private Boolean podeRedigir = Boolean.FALSE;

    public Integer getIdTipoProcessoDocumentoPapel() {
        return idTipoProcessoDocumentoPapel;
    }

    public void setIdTipoProcessoDocumentoPapel(Integer idTipoProcessoDocumentoPapel) {
        this.idTipoProcessoDocumentoPapel = idTipoProcessoDocumentoPapel;
    }

    public TipoProcessoDocumento getTipoProcessoDocumento() {
        return tipoProcessoDocumento;
    }

    public void setTipoProcessoDocumento(TipoProcessoDocumento tipoProcessoDocumento) {
        this.tipoProcessoDocumento = tipoProcessoDocumento;
    }
   
    public Papel getPapel() {
        return papel;
    }

    public void setPapel(Papel papel) {
        this.papel = papel;
    }

    public TipoAssinaturaEnum getTipoAssinatura() {
        return this.tipoAssinatura;
    }
    
    public void setTipoAssinatura(TipoAssinaturaEnum tipoAssinatura) {
        this.tipoAssinatura = tipoAssinatura;
    }
    
    public Boolean getPodeRedigir() {
		return podeRedigir;
	}

	public void setPodeRedigir(Boolean podeRedigir) {
		this.podeRedigir = podeRedigir;
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
        return getIdTipoProcessoDocumentoPapel().equals(other.getIdTipoProcessoDocumentoPapel());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getIdTipoProcessoDocumentoPapel();
        return result;
    }
}
