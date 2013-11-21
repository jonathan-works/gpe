package br.com.infox.epp.cliente.entity;

import java.io.Serializable;

import javax.persistence.*;

import br.com.infox.core.constants.LengthConstants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(schema="public", name=Cbo.TABLE_NAME)
public class Cbo implements Serializable {

	public static final String TABLE_NAME = "tb_cbo";
	private static final long serialVersionUID = 1L;

	private int idCbo;
	private Integer codCbo;
	private String descricaoCbo;
	private Boolean ativo;
	
	@SequenceGenerator(name="generator", sequenceName="public.sq_tb_cbo")
	@Id
	@GeneratedValue(generator="generator")
	@Column(name="id_cbo", unique=true, nullable=false)
	public int getIdCbo() {
		return idCbo;
	}
	public void setIdCbo(int idCbo) {
		this.idCbo = idCbo;
	}
	
	@Column(name="cd_cbo", nullable=false)
	public Integer getCodCbo() {
		return codCbo;
	}
	public void setCodCbo(Integer codCbo) {
		this.codCbo = codCbo;
	}
	
	@Column(name="ds_cbo", nullable=false, length=LengthConstants.DESCRICAO_CLASSIFICACAO)
	@Size(max=LengthConstants.DESCRICAO_CLASSIFICACAO)
	public String getDescricaoCbo() {
		return descricaoCbo;
	}
	public void setDescricaoCbo(String descricaoCbo) {
		this.descricaoCbo = descricaoCbo;
	}
	
	@Column(name="in_ativo", nullable=false)
	@NotNull
	public Boolean getAtivo() {
		return this.ativo;
	}
	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
}
