package br.com.infox.epp.entrega.documentos;

import java.io.Serializable;
import java.util.Date;

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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.epp.pessoa.entity.Pessoa;
import br.com.infox.epp.processo.partes.entity.TipoParte;

@Entity
@Table(name = "tb_entrega_responsavel")
public class EntregaResponsavel implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String GENERATOR_NAME = "EntregaResponsavelGenerator";
	private static final String SEQUENCE_NAME = "sq_entrega_responsavel";

	@Id
	@SequenceGenerator(allocationSize = 1, initialValue = 1, name = GENERATOR_NAME, sequenceName = SEQUENCE_NAME)
	@GeneratedValue(generator = GENERATOR_NAME, strategy = GenerationType.SEQUENCE)
	@Column(name = "id_entrega_responsavel", unique = true, nullable = false)
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_entrega", nullable = false)
	private Entrega entrega;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pessoa", nullable = false)
	private Pessoa pessoa;
	
	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "ds_nome", nullable = false)
	private String nome;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_parte")
	private TipoParte tipoParte;

	@Column(name = "dt_inicio")
	private Date dataInicio;

	@Column(name = "dt_fim")
	private Date dataFim;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_responsavel_vinculado")
	private EntregaResponsavel responsavelVinculado;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Entrega getEntrega() {
		return entrega;
	}

	public void setEntrega(Entrega entrega) {
		this.entrega = entrega;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public TipoParte getTipoParte() {
		return tipoParte;
	}

	public void setTipoParte(TipoParte tipoParte) {
		this.tipoParte = tipoParte;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public EntregaResponsavel getResponsavelVinculado() {
		return responsavelVinculado;
	}

	public void setResponsavelVinculado(EntregaResponsavel responsavelVinculado) {
		this.responsavelVinculado = responsavelVinculado;
	}
	
	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
}
