package br.com.infox.epp.usuario.rest;

import static br.com.infox.epp.usuario.rest.ConstantesDTO.DATE_PATTERN;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.meiocontato.annotation.Email;
import br.com.infox.epp.meiocontato.entity.MeioContato;
import br.com.infox.epp.pessoa.annotation.Cpf;
import br.com.infox.epp.pessoa.annotation.Data;
import br.com.infox.epp.pessoa.annotation.EstadoCivil;
import br.com.infox.epp.pessoa.documento.entity.PessoaDocumento;
import br.com.infox.epp.pessoa.entity.PessoaFisica;

public class UsuarioDTO implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
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
	@EstadoCivil
	private String estadoCivil;
	
	private List<PessoaDocumentoDTO> documentos=new ArrayList<>();
	private List<MeioContatoDTO> meiosContato=new ArrayList<>();
	
	public UsuarioDTO() {
	}

	public UsuarioDTO(UsuarioLogin usuarioLogin, PessoaFisica pessoaFisica, List<PessoaDocumento> documentos, List<MeioContato> meiosContato){
		this(usuarioLogin, pessoaFisica);
		if (documentos != null && !documentos.isEmpty()){
			for (PessoaDocumento pessoaDocumento : documentos) {
				this.documentos.add(new PessoaDocumentoDTO(pessoaDocumento));
			}
		}
		if (meiosContato != null && !meiosContato.isEmpty()){
			for (MeioContato meioContato : meiosContato) {
				this.meiosContato.add(new MeioContatoDTO(meioContato));
			}
		}
	}
	
	public UsuarioDTO(UsuarioLogin usuarioLogin, PessoaFisica pessoaFisica) {
		this.nome = usuarioLogin.getNomeUsuario();
		this.cpf = pessoaFisica.getCpf();
		this.email = usuarioLogin.getEmail();
		this.dataNascimento = new SimpleDateFormat(DATE_PATTERN).format(pessoaFisica.getDataNascimento());
		this.estadoCivil = pessoaFisica.getEstadoCivil().name();
	}

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

	public String getEstadoCivil() {
		return estadoCivil;
	}

	public void setEstadoCivil(String estadoCivil) {
		this.estadoCivil = estadoCivil;
	}

	public List<PessoaDocumentoDTO> getDocumentos() {
		return documentos;
	}

	public void setDocumentos(List<PessoaDocumentoDTO> documentos) {
		this.documentos = documentos;
	}

	public List<MeioContatoDTO> getMeiosContato() {
		return meiosContato;
	}

	public void setMeiosContato(List<MeioContatoDTO> meiosContato) {
		this.meiosContato = meiosContato;
	}
	
}
