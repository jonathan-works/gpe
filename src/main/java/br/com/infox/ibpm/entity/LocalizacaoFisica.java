package br.com.infox.ibpm.entity;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.validator.Length;

@Entity
@Table(schema="public", name=LocalizacaoFisica.TABLE_NAME)
public class LocalizacaoFisica implements Serializable {

	public static final String TABLE_NAME = "tb_localizacao_fisica";
	private static final long serialVersionUID = 1L;

	private int idLocalizacaoFisica;
	private Integer nrPrateleira;
	private Integer nrCaixa;
	private String descricaoSala;
	private Boolean ativo;
	
	@SequenceGenerator(name="generator", sequenceName="sq_tb_localizacao_fisica")
	@Id
	@GeneratedValue(generator="generator")
	@Column(name="id_localizacao_fisica", unique=true, nullable=false)
	public int getIdLocalizacaoFisica() {
		return idLocalizacaoFisica;
	}
	public void setIdLocalizacaoFisica(int idLocalizacaoFisica) {
		this.idLocalizacaoFisica = idLocalizacaoFisica;
	}
	
	@Column(name="nr_prateleira", nullable=false)
	public Integer getNrPrateleira() {
		return nrPrateleira;
	}
	public void setNrPrateleira(Integer nrPrateleira) {
		this.nrPrateleira = nrPrateleira;
	}
	
	@Column(name="nr_caixa", nullable=false)
	public Integer getNrCaixa() {
		return nrCaixa;
	}
	public void setNrCaixa(Integer nrCaixa) {
		this.nrCaixa = nrCaixa;
	}
	
	@Column(name="ds_sala", nullable=false, length=150)
	@Length(max=150)
	public String getDescricaoSala() {
		return descricaoSala;
	}
	public void setDescricaoSala(String descricaoSala) {
		this.descricaoSala = descricaoSala;
	}
	
	@Column(name="in_ativo", nullable=false)
	public Boolean getAtivo() {
		return this.ativo;
	}
	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
	@Override
	public String toString() {
		return descricaoSala + " [Prateleira=" + nrPrateleira + ", Caixa="
				+ nrCaixa + "]";
	}
	
}
