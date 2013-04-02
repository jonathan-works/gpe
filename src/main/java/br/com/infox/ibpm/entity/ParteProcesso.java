package br.com.infox.ibpm.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.com.infox.epa.entity.ProcessoEpa;

@Entity
@Table(name=ParteProcesso.TABLE_NAME, schema="public")
public class ParteProcesso implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "tb_parte_processo";
	
	private Integer idParteProcesso;
	private ProcessoEpa processo;
	private Pessoa pessoa;
	private Boolean ativo = true;
	
	public ParteProcesso() {
	}
	
	public ParteProcesso(ProcessoEpa processo, Pessoa pessoa) {
		this.processo = processo;
		this.pessoa = pessoa;
	}
	
	@SequenceGenerator(name="generator", sequenceName="public.sq_tb_parte_processo")
	@Id
	@Column(name="id_parte_processo")
	@GeneratedValue(generator="generator")
	public Integer getIdParteProcesso() {
		return idParteProcesso;
	}
	public void setIdParteProcesso(Integer idParteProcesso) {
		this.idParteProcesso = idParteProcesso;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_processo")
	public ProcessoEpa getProcesso() {
		return processo;
	}
	public void setProcesso(ProcessoEpa processo) {
		this.processo = processo;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_pessoa")
	public Pessoa getPessoa() {
		return pessoa;
	}
	
	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	@Column(name="in_ativo")
	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
	
}
