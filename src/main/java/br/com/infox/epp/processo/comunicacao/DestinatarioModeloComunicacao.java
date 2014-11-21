package br.com.infox.epp.processo.comunicacao;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;

@Entity
@Table(name = "tb_destinatario_modelo_comunic", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"id_pessoa_fisica", "id_modelo_comunicacao"}),
	@UniqueConstraint(columnNames = {"id_localizacao", "id_modelo_comunicacao"})
})
public class DestinatarioModeloComunicacao implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "DestinatarioModeloComunicacaoGenerator", allocationSize = 1, initialValue = 1, sequenceName = "sq_destinatario_modelo_comunic")
	@GeneratedValue(generator = "DestinatarioModeloComunicacaoGenerator", strategy = GenerationType.SEQUENCE)
	@Column(name = "id_destinatario_modelo_comunic")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pessoa_fisica")
	private PessoaFisica destinatario;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_localizacao")
	private Localizacao destino;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_modelo_comunicacao", nullable = false)
	private ModeloComunicacao modeloComunicacao;
	
	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(name = "tp_meio_expedicao", nullable = false)
	private MeioExpedicao meioExpedicao;
	
	@Column(name = "nr_prazo")
	private Integer prazo;
	
	@Column(name = "in_expedido", nullable = false)
	@NotNull
	private Boolean expedido = false;
	
	@JoinColumn(name = "id_documento_bin")
	@ManyToOne(fetch = FetchType.LAZY)
	private DocumentoBin comunicacao;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ModeloComunicacao getModeloComunicacao() {
		return modeloComunicacao;
	}

	public void setModeloComunicacao(ModeloComunicacao modeloComunicacao) {
		this.modeloComunicacao = modeloComunicacao;
	}

	public MeioExpedicao getMeioExpedicao() {
		return meioExpedicao;
	}

	public void setMeioExpedicao(MeioExpedicao meioExpedicao) {
		this.meioExpedicao = meioExpedicao;
	}

	public Integer getPrazo() {
		return prazo;
	}

	public void setPrazo(Integer prazo) {
		this.prazo = prazo;
	}

	public Localizacao getDestino() {
		return destino;
	}
	
	public void setDestino(Localizacao destino) {
		this.destino = destino;
	}
	
	public PessoaFisica getDestinatario() {
		return destinatario;
	}
	
	public void setDestinatario(PessoaFisica destinatario) {
		this.destinatario = destinatario;
	}
	
	public Boolean getExpedido() {
		return expedido;
	}
	
	public void setExpedido(Boolean expedido) {
		this.expedido = expedido;
	}
	
	public DocumentoBin getComunicacao() {
		return comunicacao;
	}
	
	public void setComunicacao(DocumentoBin comunicacao) {
		this.comunicacao = comunicacao;
	}
	
	@Transient
	public String getNome() {
		if (destinatario != null) {
			return destinatario.getNome();
		} else if (destino != null) {
			return destino.getCaminhoCompletoFormatado();
		}
		return null;
	}
}
