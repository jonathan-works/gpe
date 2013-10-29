package br.com.infox.ibpm.entity;

import java.text.DateFormat;
import java.util.Date;
import javax.persistence.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.epp.type.TipoPessoaEnum;
import br.com.infox.util.constants.LengthConstants;

@Entity
@Table(schema="public", name=PessoaFisica.TABLE_NAME)
@PrimaryKeyJoinColumn(name="id_pessoa_fisica", columnDefinition = "integer")
public class PessoaFisica extends Pessoa{
    public static final String EVENT_LOAD = "evtCarregarPessoaFisica";
	public static final String TABLE_NAME = "tb_pessoa_fisica";
	private static final long serialVersionUID = 1L;
	
	private String cpf;
	private Date dataNascimento;
	private String email;
	
	public PessoaFisica(){
		setTipoPessoa(TipoPessoaEnum.F);
	}
	
	@Column(name="nr_cpf", nullable=false, unique=true)
	@Size(max=LengthConstants.NUMERO_CPF)
	public String getCpf() {
		return cpf;
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	
	@Column(name="dt_nascimento", nullable=false)
	public Date getDataNascimento() {
		return dataNascimento;
	}
	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}
	
	@Column(name = "ds_email", length=LengthConstants.DESCRICAO_PADRAO, unique = true, nullable = false)
	@Size(max=LengthConstants.DESCRICAO_PADRAO)
	@NotNull
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	@Transient
	public String getDataFormatada(){
		return DateFormat.getDateInstance().format(dataNascimento);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cpf == null) ? 0 : cpf.hashCode());
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
		if (!(obj instanceof PessoaFisica)) {
		    return false;
		}
		PessoaFisica other = (PessoaFisica) obj;
		if (cpf == null) {
			if (other.cpf != null) {
			    return false;
			}
		} else if (!cpf.equals(other.cpf)) {
		    return false;
		}
		return true;
	}

}
