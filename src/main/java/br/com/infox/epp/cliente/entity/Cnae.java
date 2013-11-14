package br.com.infox.epp.cliente.entity;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.core.constants.LengthConstants;


@Entity
@Table(schema="public", name=Cnae.TABLE_NAME)
public class Cnae implements Serializable {

	public static final String TABLE_NAME = "tb_cnae";
	private static final long serialVersionUID = 1L;

	private int idCnae;
	private String codCnae;
	private String descricaoCnae;
	private Boolean ativo;
	
	@SequenceGenerator(name="generator", sequenceName="public.sq_tb_cnae")
	@Id
	@GeneratedValue(generator="generator")
	@Column(name="id_cnae", unique=true, nullable=false)
	public int getIdCnae() {
		return idCnae;
	}
	public void setIdCnae(int idCnae) {
		this.idCnae = idCnae;
	}
	
	@Column(name="cd_cnae", nullable=false, length=LengthConstants.CNAE)
	@Size(max=LengthConstants.CNAE)
	public String getCodCnae() {
		return codCnae;
	}
	public void setCodCnae(String codCnae) {
		this.codCnae = codCnae;
	}
	
	@Column(name="ds_cnae", nullable=false, length=LengthConstants.DESCRICAO_CLASSIFICACAO)
	@Size(max=LengthConstants.DESCRICAO_CLASSIFICACAO)
	public String getDescricaoCnae() {
		return descricaoCnae;
	}
	public void setDescricaoCnae(String descricaoCnae) {
		this.descricaoCnae = descricaoCnae;
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
