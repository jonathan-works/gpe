package br.com.infox.epp.ws.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.codehaus.jackson.annotate.JsonIgnore;

import br.com.infox.epp.meiocontato.annotation.Email;
import br.com.infox.epp.pessoa.annotation.Cpf;
import br.com.infox.epp.pessoa.annotation.Data;
import br.com.infox.epp.pessoa.annotation.EstadoCivil;

public class UsuarioBean {

	public static final String DATE_PATTERN = "dd/MM/yyyy";

	@NotNull
	@Size(min = 5, max = 150)
	private String nome;

	@Cpf
	@NotNull
	private String cpf;

	@Email
	@NotNull
	private String email;

	@Data(pattern = DATE_PATTERN, past = true)
	private String dataNascimento;

	private String identidade;

	private String orgaoExpedidor;

	@Data(pattern = DATE_PATTERN, past = true)
	private String dataExpedicao;

	private String telefoneFixo;

	private String telefoneMovel;

	@EstadoCivil
	private String estadoCivil;

	private String emailOpcional1;

	private String emailOpcional2;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(String dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public String getIdentidade() {
		return identidade;
	}

	public void setIdentidade(String identidade) {
		this.identidade = identidade;
	}

	public String getOrgaoExpedidor() {
		return orgaoExpedidor;
	}

	public void setOrgaoExpedidor(String orgaoExpedidor) {
		this.orgaoExpedidor = orgaoExpedidor;
	}

	public String getDataExpedicao() {
		return dataExpedicao;
	}

	public void setDataExpedicao(String dataExpedicao) {
		this.dataExpedicao = dataExpedicao;
	}

	public String getTelefoneFixo() {
		return telefoneFixo;
	}

	public void setTelefoneFixo(String telefoneFixo) {
		this.telefoneFixo = telefoneFixo;
	}

	public String getTelefoneMovel() {
		return telefoneMovel;
	}

	public void setTelefoneMovel(String telefoneMovel) {
		this.telefoneMovel = telefoneMovel;
	}

	public String getEmailOpcional1() {
		return emailOpcional1;
	}

	public void setEmailOpcional1(String emailOpcional1) {
		this.emailOpcional1 = emailOpcional1;
	}

	public String getEmailOpcional2() {
		return emailOpcional2;
	}

	public void setEmailOpcional2(String emailOpcional2) {
		this.emailOpcional2 = emailOpcional2;
	}

	public String getEstadoCivil() {
		return estadoCivil;
	}

	public void setEstadoCivil(String estadoCivil) {
		this.estadoCivil = estadoCivil;
	}

	@JsonIgnore
	public Date getDataNascimentoAsDate() {
	    if (dataNascimento == null) return null;
		try {
			return new SimpleDateFormat(DATE_PATTERN).parse(dataNascimento);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	@JsonIgnore
	public Date getDataExpedicaoAsDate() {
		if(dataExpedicao == null) {
			return null;
		}
		try {
			return new SimpleDateFormat(DATE_PATTERN).parse(dataExpedicao);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [nome=" + nome + ", cpf=" + cpf + ", email=" + email + ", dataNascimento="
				+ dataNascimento + ", identidade=" + identidade + ", orgaoExpedidor=" + orgaoExpedidor
				+ ", dataExpedicao=" + dataExpedicao + ", telefoneFixo=" + telefoneFixo + ", telefoneMovel="
				+ telefoneMovel + ", estadoCivil=" + estadoCivil + ", emailOpcional1=" + emailOpcional1
				+ ", emailOpcional2=" + emailOpcional2 + "]";
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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof UsuarioBean))
			return false;
		UsuarioBean other = (UsuarioBean) obj;
		if (cpf == null) {
			if (other.cpf != null)
				return false;
		} else if (!cpf.equals(other.cpf))
			return false;
		return true;
	}
}
