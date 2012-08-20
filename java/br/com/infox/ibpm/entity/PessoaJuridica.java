package br.com.infox.ibpm.entity;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.Type;
import org.hibernate.validator.Length;

@Entity
@Table(schema="core", name=PessoaJuridica.TABLE_NAME)
public class PessoaJuridica implements Serializable {

	public static final String TABLE_NAME = "tb_pessoa_juridica";
	private static final long serialVersionUID = 1L;
	
	private int idPessoaJuridica;
	private String cnpj;
	private String nome;
	private String razaoSocial;
	private Boolean ativo;

	@SequenceGenerator(name="generator", sequenceName="core.sq_tb_pessoa_juridica")
	@Id
	@GeneratedValue(generator="generator")
	@Column(name="id_pessoa_juridica", unique=true, nullable=false)
	public int getIdPessoaJuridica() {
		return idPessoaJuridica;
	}
	public void setIdPessoaJuridica(int idPessoaJuridica) {
		this.idPessoaJuridica = idPessoaJuridica;
	}
	
	@Column(name="nr_cnpj", nullable=false, unique=true, length=20)
	@Length(max=20)
	public String getCnpj() {
		return cnpj;
	}
	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}
	
	@Column(name="nm_pessoa_juridica", nullable=false, length=150)
	@Length(max=150)
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	@Column(name="nm_razao_social", nullable=false, length=100)
	@Length(max=100)
	public String getRazaoSocial() {
		return razaoSocial;
	}
	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}
	
	@Column(name="in_ativo", nullable=false)
	public Boolean getAtivo() {
		return ativo;
	}
	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
}
