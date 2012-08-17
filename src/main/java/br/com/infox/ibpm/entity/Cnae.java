package br.com.infox.ibpm.entity;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.Type;
import org.hibernate.validator.Length;

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
	
	@Column(name="cd_cnae", nullable=false)
	@Length(max=9)
	public String getCodCnae() {
		return codCnae;
	}
	public void setCodCnae(String codCnae) {
		this.codCnae = codCnae;
	}
	
	@Column(name="ds_cnae", nullable=false, length=150)
	@Length(max=150)
	public String getDescricaoCnae() {
		return descricaoCnae;
	}
	public void setDescricaoCnae(String descricaoCnae) {
		this.descricaoCnae = descricaoCnae;
	}
	
	@Column(name="in_ativo", nullable=false)
	@Type(type="br.com.itx.type.SNType")
	public Boolean getAtivo() {
		return this.ativo;
	}
	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
}
