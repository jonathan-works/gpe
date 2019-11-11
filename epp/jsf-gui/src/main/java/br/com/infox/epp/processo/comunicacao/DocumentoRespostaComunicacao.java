package br.com.infox.epp.processo.comunicacao;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
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
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.processo.comunicacao.query.DocumentoRespostaComunicacaoQuery;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.entity.Processo;

@Entity
@Table(name = "tb_doc_resposta_comunicacao", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"id_processo", "id_documento"})
})
@NamedQueries({
	@NamedQuery(name = DocumentoRespostaComunicacaoQuery.REMOVER_DOCUMENTO_RESPOSTA, query = DocumentoRespostaComunicacaoQuery.REMOVER_DOCUMENTO_RESPOSTA_QUERY),
	@NamedQuery(name = DocumentoRespostaComunicacaoQuery.GET_COMUNICACAO_VINCULADA, query = DocumentoRespostaComunicacaoQuery.GET_COMUNICACAO_VINCULADA_QUERY),
	@NamedQuery(name = DocumentoRespostaComunicacaoQuery.UPDATE_DOCUMENTO_COMO_ENVIADO, query = DocumentoRespostaComunicacaoQuery.UPDATE_DOCUMENTO_COMO_ENVIADO_QUERY)
})
public class DocumentoRespostaComunicacao implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "DocumentoRespostaComunicacaoGenerator", sequenceName = "sq_doc_resposta_comunicacao", initialValue = 1)
	@GeneratedValue(generator = "DocumentoRespostaComunicacaoGenerator", strategy = GenerationType.SEQUENCE)
	@Column(name = "id_doc_resposta_comunicacao")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@NotNull
	@JoinColumn(name = "id_processo", nullable = false)
	private Processo comunicacao;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@NotNull
	@JoinColumn(name = "id_documento", nullable = false)
	private Documento documento;
	
	@NotNull
	@Column(name = "in_doc_enviado", nullable = false)
	private Boolean enviado = Boolean.FALSE;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Processo getComunicacao() {
		return comunicacao;
	}
	
	public void setComunicacao(Processo comunicacao) {
		this.comunicacao = comunicacao;
	}
	
	public Documento getDocumento() {
		return documento;
	}

	public void setDocumento(Documento documento) {
		this.documento = documento;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((documento == null) ? 0 : documento.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime
				* result
				+ ((comunicacao == null) ? 0 : comunicacao
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof DocumentoRespostaComunicacao))
			return false;
		DocumentoRespostaComunicacao other = (DocumentoRespostaComunicacao) obj;
		if (documento == null) {
			if (other.documento != null)
				return false;
		} else if (!documento.equals(other.documento))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (comunicacao == null) {
			if (other.comunicacao != null)
				return false;
		} else if (!comunicacao.equals(other.comunicacao))
			return false;
		return true;
	}

	public Boolean getEnviado() {
		return enviado;
	}

	public void setEnviado(Boolean enviado) {
		this.enviado = enviado;
	}
}
