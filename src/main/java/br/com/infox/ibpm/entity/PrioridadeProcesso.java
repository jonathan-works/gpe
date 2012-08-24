package br.com.infox.ibpm.entity;

import java.io.Serializable;

import javax.persistence.*;

@Entity
@Table(schema="public", name=PrioridadeProcesso.TABLE_NAME)
public class PrioridadeProcesso implements Serializable {
	
	public static final String TABLE_NAME = "tb_prioridade_processo";
	private static final long serialVersionUID = 1L;
	
	private Integer idPrioridade;
	private String descricaoPrioridade;
	private Integer peso;
	private Boolean ativo;
	
	@SequenceGenerator(name="generator", sequenceName="core.sq_tb_prioridade_processo")
	@Id
	@GeneratedValue(generator="generator")
	@Column(name="id_prioridade_processo")
	public Integer getIdPrioridade() {
		return idPrioridade;
	}
	public void setIdPrioridade(Integer idPrioridade) {
		this.idPrioridade = idPrioridade;
	}
	
	@Column(name="ds_prioridade_processo", nullable=false)
	public String getDescricaoPrioridade() {
		return descricaoPrioridade;
	}
	public void setDescricaoPrioridade(String descricaoPrioridade) {
		this.descricaoPrioridade = descricaoPrioridade;
	}
	
	@Column(name="nr_peso", nullable=false)
	public Integer getPeso() {
		return peso;
	}
	public void setPeso(Integer peso) {
		this.peso = peso;
	}
	
	@Column(name="in_ativo")
	public Boolean getAtivo() {
		return ativo;
	}
	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
	
}
