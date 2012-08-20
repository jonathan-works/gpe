package br.com.infox.ibpm.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.validator.Length;

@Entity
@Table(schema="public", name=PessoaFisica.TABLE_NAME)
public class PessoaFisica implements Serializable {

	public static final String TABLE_NAME = "tb_pessoa_fisica";
	private static final long serialVersionUID = 1L;
	
	private Integer idPessoaFisica;
	private String cpf;
	private String nome;
	private Date dataNascimento;
	private Boolean ativo;
	
	@SequenceGenerator(name="generator", sequenceName="core.sq_tb_pessoa_fisica")
	@Id
	@GeneratedValue(generator="generator")
	@Column(name="id_pessoa_fisica")
	public Integer getIdPessoaFisica() {
		return idPessoaFisica;
	}
	public void setIdPessoaFisica(Integer idPessoaFisica) {
		this.idPessoaFisica = idPessoaFisica;
	}
	
	@Column(name="nr_cpf", nullable=false, unique=true)
	@Length(max=20)
	public String getCpf() {
		return cpf;
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	
	@Column(name="nm_pessoa_fisica", nullable=false)
	@Length(max=150)
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	@Column(name="dt_nascimento", nullable=false)
	public Date getDataNascimento() {
		return dataNascimento;
	}
	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}
	
	@Column(name="in_ativo", nullable=false)
	public Boolean getAtivo() {
		return ativo;
	}
	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
}
