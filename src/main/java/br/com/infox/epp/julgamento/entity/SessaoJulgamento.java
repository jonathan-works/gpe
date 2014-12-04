package br.com.infox.epp.julgamento.entity;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.pessoa.entity.PessoaFisica;

@Entity
@Table(name = "tb_sessao_julgamento")
public class SessaoJulgamento implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(initialValue=1, allocationSize=1, name="SessaoJulgamentoGenerator", sequenceName="sq_sessao_julgamento")
	@GeneratedValue(generator="SessaoJulgamentoGenerator", strategy=GenerationType.SEQUENCE)
	@Column(name = "id_sessao_julgamento", unique = true, nullable = false)
	private Long id;
	
	@NotNull
	@Column(name = "nm_sessao_julgamento", nullable = false)
	private String nome;
	
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dt_sessao_julgamento", nullable = false)
	private Date data;
	
	@NotNull
	@Temporal(TemporalType.TIME)
	@Column(name = "vl_hora_inicio", nullable = false)
	private Date horaInicio;
	
	@NotNull
	@Temporal(TemporalType.TIME)
	@Column(name = "vl_hora_fim", nullable = false)
	private Date horaFim;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_sala", nullable = false)
	private Sala sala;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pessoa_fisica", nullable = false)
	private PessoaFisica procurador;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_status_sessao_julgamento")
	private StatusSessaoJulgamento statusSessao;
	
	@NotNull
    @Column(name = "in_ativo", nullable = false)
    private Boolean ativo;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public Date getHoraInicio() {
		return horaInicio;
	}

	public void setHoraInicio(Date horaInicio) {
		this.horaInicio = horaInicio;
	}

	public Date getHoraFim() {
		return horaFim;
	}

	public void setHoraFim(Date horaFim) {
		this.horaFim = horaFim;
	}

	public Sala getSala() {
		return sala;
	}

	public void setSala(Sala sala) {
		this.sala = sala;
	}

	public PessoaFisica getProcurador() {
		return procurador;
	}

	public void setProcurador(PessoaFisica procurador) {
		this.procurador = procurador;
	}

	public StatusSessaoJulgamento getStatusSessao() {
		return statusSessao;
	}

	public void setStatusSessao(StatusSessaoJulgamento statusSessao) {
		this.statusSessao = statusSessao;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
	
	@Override
	public String toString() {
		return nome;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof SessaoJulgamento))
			return false;
		SessaoJulgamento other = (SessaoJulgamento) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
