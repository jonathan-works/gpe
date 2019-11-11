package br.com.infox.epp.processo.comunicacao.tipo.crud;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.infox.epp.documento.entity.ClassificacaoDocumento;

@Entity
@Table(name = "tb_tipo_comunic_classif_doc", uniqueConstraints = {
	@UniqueConstraint(columnNames = {
		"id_tipo_comunicacao", "id_classificacao_documento"
	})
})
public class TipoComunicacaoClassificacaoDocumento implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name = "TipoComunicacaoClassificacaoDocumentoGenerator", sequenceName = "sq_tipo_comunic_classif_doc", allocationSize = 1, initialValue = 1)
	@GeneratedValue(generator = "TipoComunicacaoClassificacaoDocumentoGenerator", strategy = GenerationType.SEQUENCE)
	@Column(name = "id_tipo_comunic_classif_doc")
	private Long id;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "id_tipo_comunicacao", nullable = false)
	private TipoComunicacao tipoComunicacao;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "id_classificacao_documento", nullable = false)
	private ClassificacaoDocumento classificacaoDocumento;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public TipoComunicacao getTipoComunicacao() {
		return tipoComunicacao;
	}

	public void setTipoComunicacao(TipoComunicacao tipoComunicacao) {
		this.tipoComunicacao = tipoComunicacao;
	}

	public ClassificacaoDocumento getClassificacaoDocumento() {
		return classificacaoDocumento;
	}

	public void setClassificacaoDocumento(ClassificacaoDocumento classificacaoDocumento) {
		this.classificacaoDocumento = classificacaoDocumento;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((classificacaoDocumento == null) ? 0 : classificacaoDocumento.hashCode());
		result = prime * result + ((tipoComunicacao == null) ? 0 : tipoComunicacao.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof TipoComunicacaoClassificacaoDocumento))
			return false;
		TipoComunicacaoClassificacaoDocumento other = (TipoComunicacaoClassificacaoDocumento) obj;
		if (classificacaoDocumento == null) {
			if (other.classificacaoDocumento != null)
				return false;
		} else if (!classificacaoDocumento.equals(other.classificacaoDocumento))
			return false;
		if (tipoComunicacao == null) {
			if (other.tipoComunicacao != null)
				return false;
		} else if (!tipoComunicacao.equals(other.tipoComunicacao))
			return false;
		return true;
	}
}
