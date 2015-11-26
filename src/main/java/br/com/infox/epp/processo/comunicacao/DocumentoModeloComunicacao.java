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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.processo.documento.entity.Documento;

@Entity
@Table(name = "tb_documento_modelo_comunic", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"id_documento", "id_modelo_comunicacao"})
})
public class DocumentoModeloComunicacao implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "DocumentoModeloComunicacaoGenerator", sequenceName = "sq_documento_modelo_comunic", allocationSize = 1, initialValue = 1)
	@GeneratedValue(generator = "DocumentoModeloComunicacaoGenerator", strategy = GenerationType.SEQUENCE)
	@Column(name = "id_documento_modelo_comunic")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@NotNull
	@JoinColumn(name = "id_documento", nullable = false)
	private Documento documento;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@NotNull
	@JoinColumn(name = "id_modelo_comunicacao", nullable = false)
	private ModeloComunicacao modeloComunicacao;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Documento getDocumento() {
		return documento;
	}

	public void setDocumento(Documento documento) {
		this.documento = documento;
	}

	public ModeloComunicacao getModeloComunicacao() {
		return modeloComunicacao;
	}

	public void setModeloComunicacao(ModeloComunicacao modeloComunicacao) {
		this.modeloComunicacao = modeloComunicacao;
	}
	
	public DocumentoModeloComunicacao makeCopy() throws CloneNotSupportedException {
		DocumentoModeloComunicacao novoDocumentoModeloComunicacao = (DocumentoModeloComunicacao) clone();
		novoDocumentoModeloComunicacao.setId(null);
		novoDocumentoModeloComunicacao.setDocumento(getDocumento().makeCopy());
		return novoDocumentoModeloComunicacao;
	}
}
