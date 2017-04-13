package br.com.infox.epp.documento.entity;

import java.io.Serializable;

import javax.persistence.Cacheable;
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
import br.com.infox.epp.documento.domain.RegraAssinatura;
import br.com.infox.epp.documento.query.ClassificacaoDocumentoPapelQuery;
import br.com.infox.epp.documento.query.ClassificacaoDocumentoQuery;
import br.com.infox.epp.documento.type.TipoAssinaturaEnum;

@Entity
@Table(name = ClassificacaoDocumentoPapel.TABLE_NAME)
@NamedQueries(value = { 
	@NamedQuery(name = ClassificacaoDocumentoQuery.ASSINATURA_OBRIGATORIA, query = ClassificacaoDocumentoQuery.ASSINATURA_OBRIGATORIA_QUERY),
	@NamedQuery(name = ClassificacaoDocumentoPapelQuery.PAPEL_PODE_ASSINAR_CLASSIFICACAO, query = ClassificacaoDocumentoPapelQuery.PAPEL_PODE_ASSINAR_CLASSIFICACAO_QUERY),
	@NamedQuery(name = ClassificacaoDocumentoPapelQuery.GET_BY_PAPEL_AND_CLASSIFICACAO, query = ClassificacaoDocumentoPapelQuery.GET_BY_PAPEL_AND_CLASSIFICACAO_QUERY),
	@NamedQuery(name = ClassificacaoDocumentoPapelQuery.CLASSIFICACAO_EXIGE_ASSINATURA, query = ClassificacaoDocumentoPapelQuery.CLASSIFICACAO_EXIGE_ASSINATURA_QUERY)
})
@Cacheable
public class ClassificacaoDocumentoPapel implements Serializable, RegraAssinatura {
    
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "tb_classificacao_doc_papel";

    @Id
    @SequenceGenerator(allocationSize=1, initialValue=1, name = "ClassificacaoPapelGenerator", sequenceName = "sq_classificacao_doc_papel")
    @GeneratedValue(generator = "ClassificacaoPapelGenerator", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_classificacao_doc_papel", nullable = false, unique = true)
    private Integer id;
    
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_classificacao_documento", nullable = false)
    private ClassificacaoDocumento classificacaoDocumento;
    
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

    @NotNull
    @Column(name = "in_assinatura_multipla", nullable = false)
    private Boolean assinaturasMultiplas = false;
    
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ClassificacaoDocumento getClassificacaoDocumento() {
		return classificacaoDocumento;
	}

	public void setClassificacaoDocumento(ClassificacaoDocumento classificacaoDocumento) {
		this.classificacaoDocumento = classificacaoDocumento;
	}

	@Override
	public Papel getPapel() {
		return papel;
	}

	public void setPapel(Papel papel) {
		this.papel = papel;
	}

	@Override
	public TipoAssinaturaEnum getTipoAssinatura() {
		return tipoAssinatura;
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
	public Boolean getAssinaturasMultiplas() {
        return assinaturasMultiplas;
    }
	
	public void setAssinaturasMultiplas(Boolean assinaturasMultiplas) {
        this.assinaturasMultiplas = assinaturasMultiplas;
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
		if (!(obj instanceof ClassificacaoDocumentoPapel))
			return false;
		ClassificacaoDocumentoPapel other = (ClassificacaoDocumentoPapel) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

}
