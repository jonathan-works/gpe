package br.com.infox.ibpm.entity;

import javax.persistence.*;

import org.hibernate.validator.Length;

import br.com.infox.epp.type.TipoPessoaEnum;

@Entity
@Table(schema="public", name=PessoaJuridica.TABLE_NAME)
@PrimaryKeyJoinColumn(name="id_pessoa_juridica", columnDefinition = "integer")
public class PessoaJuridica extends Pessoa {

	public static final String TABLE_NAME = "tb_pessoa_juridica";
	private static final long serialVersionUID = 1L;
	
	private String cnpj;
	private String razaoSocial;
	
	public PessoaJuridica(){
		setTipoPessoa(TipoPessoaEnum.J);
	}
	
	@Column(name="nr_cnpj", nullable=false, unique=true, length=20)
	@Length(max=20)
	public String getCnpj() {
		return cnpj;
	}
	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}
	
	@Column(name="nm_razao_social", nullable=false, length=100)
	@Length(max=100)
	public String getRazaoSocial() {
		return razaoSocial;
	}
	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cnpj == null) ? 0 : cnpj.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PessoaJuridica other = (PessoaJuridica) obj;
		if (cnpj == null) {
			if (other.cnpj != null)
				return false;
		} else if (!cnpj.equals(other.cnpj))
			return false;
		return true;
	}
}
