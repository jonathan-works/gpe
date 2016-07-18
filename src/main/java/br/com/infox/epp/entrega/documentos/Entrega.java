package br.com.infox.epp.entrega.documentos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.entrega.modelo.ModeloEntrega;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.entity.Pasta;

@Entity
@Table(name = "tb_entrega")
public class Entrega implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String GENERATOR_NAME = "EntregaGenerator";
	private static final String SEQUENCE_NAME = "sq_entrega";

	@Id
	@SequenceGenerator(allocationSize = 1, initialValue = 1, name = GENERATOR_NAME, sequenceName = SEQUENCE_NAME)
	@GeneratedValue(generator = GENERATOR_NAME, strategy = GenerationType.SEQUENCE)
	@Column(name = "id_entrega", unique = true, nullable = false)
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_localizacao", nullable = false)
	private Localizacao localizacao;

	@Column(name = "dt_entrega")
	private Date dataEntrega;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_modelo_entrega", nullable = false)
	private ModeloEntrega modeloEntrega;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_pasta", nullable = false)
	private Pasta pasta;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_entrega")
	private UsuarioLogin usuarioEntrega;
	
	@OneToMany(mappedBy = "entrega", fetch = FetchType.LAZY)
	private List<EntregaResponsavel> responsaveis = new ArrayList<>();
	
	@ManyToOne
	@JoinColumn(name="id_certidao_entrega")
	private DocumentoBin certidaoEntrega;

	public DocumentoBin getCertidaoEntrega() {
		return certidaoEntrega;
	}

	public void setCertidaoEntrega(DocumentoBin certidaoEntrega) {
		this.certidaoEntrega = certidaoEntrega;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Localizacao getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(Localizacao localizacao) {
		this.localizacao = localizacao;
	}

	public Date getDataEntrega() {
		return dataEntrega;
	}

	public void setDataEntrega(Date dataEntrega) {
		this.dataEntrega = dataEntrega;
	}

	public ModeloEntrega getModeloEntrega() {
		return modeloEntrega;
	}

	public void setModeloEntrega(ModeloEntrega modeloEntrega) {
		this.modeloEntrega = modeloEntrega;
	}

	public Pasta getPasta() {
		return pasta;
	}

	public void setPasta(Pasta pasta) {
		this.pasta = pasta;
	}

	public List<EntregaResponsavel> getResponsaveis() {
		return responsaveis;
	}

	public void setResponsaveis(List<EntregaResponsavel> responsaveis) {
		this.responsaveis = responsaveis;
	}
	
	public UsuarioLogin getUsuarioEntrega() {
		return usuarioEntrega;
	}
	
	public void setUsuarioEntrega(UsuarioLogin usuarioEntrega) {
		this.usuarioEntrega = usuarioEntrega;
	}
}
