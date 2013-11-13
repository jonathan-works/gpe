package br.com.infox.epp.processo.partes.entity;

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
import javax.persistence.Transient;

import br.com.infox.epp.pessoa.entity.Pessoa;
import br.com.infox.epp.processo.entity.ProcessoEpa;

@Entity
@Table(name=ParteProcesso.TABLE_NAME, schema="public")
public class ParteProcesso implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "tb_parte_processo";
	public static final String ALTERACAO_ATIVIDADE_PARTE_PROCESSO = "alteracaoAtividadeParteProcesso";
	
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
	
	@Transient
	public String getNomeParte(){
		return getPessoa().getNome();
	}
	
	public void setNomeParte(String nome){
		getPessoa().setNome(nome);
	}
	
}
