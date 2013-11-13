package br.com.infox.epp.pessoa.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import br.com.infox.epp.pessoa.entity.type.TipoPessoaEnum;
import br.com.infox.ibpm.entity.PessoaFisica;
import br.com.infox.ibpm.entity.PessoaJuridica;
import br.com.infox.util.constants.LengthConstants;

@Entity
@Table(name=Pessoa.TABLE_NAME, schema="public")
@Inheritance(strategy=InheritanceType.JOINED)
public class Pessoa implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "tb_pessoa";
	
	private Integer idPessoa;
	private TipoPessoaEnum tipoPessoa;
	private String nome;
	private Boolean ativo;
	
	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_pessoa")
	@Id
	@GeneratedValue(generator="generator")
	@Column(name="id_pessoa", unique=true, nullable=false)
	public Integer getIdPessoa() {
		return idPessoa;
	}
	public void setIdPessoa(Integer idPessoa) {
		this.idPessoa = idPessoa;
	}
	
	@Column(name="tp_pessoa", nullable=false, columnDefinition="varchar(1)", length=LengthConstants.FLAG)
	@Enumerated(EnumType.STRING)
	public TipoPessoaEnum getTipoPessoa() {
		return tipoPessoa;
	}
	
	public void setTipoPessoa(TipoPessoaEnum tipoPessoa) {
		this.tipoPessoa = tipoPessoa;
	}
	
	@Column(name="nm_pessoa", nullable=false, length=LengthConstants.NOME_ATRIBUTO)
	@Size(max=LengthConstants.NOME_ATRIBUTO)
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	@Column(name="in_ativo", nullable=false)
	public Boolean getAtivo() {
		return ativo;
	}
	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
	
	@Override
	public String toString(){
		return nome;
	}
	
	@Transient
	public String getCodigo(){
		if (this instanceof PessoaFisica) {
			return ((PessoaFisica)this).getCpf();
		} else if (this instanceof PessoaJuridica) {
			return ((PessoaJuridica)this).getCnpj();
		}
		return "";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((idPessoa == null) ? 0 : idPessoa.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Pessoa)) {
			return false;
		}
		Pessoa other = (Pessoa) obj;
		if (idPessoa == null) {
			if (other.idPessoa != null) {
				return false;
			}
		} else if (!idPessoa.equals(other.idPessoa)) {
			return false;
		}
		return true;
	}
	
	
}
