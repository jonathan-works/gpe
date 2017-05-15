package br.com.infox.epp.usuario.rest;

import static br.com.infox.epp.usuario.rest.ConstantesDTO.DATE_PATTERN;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.type.UsuarioEnum;
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
	@MetodoLogin
	private String metodoLogin;
	
	private List<PessoaDocumentoDTO> documentos=new ArrayList<>();
	private List<MeioContatoDTO> meiosContato=new ArrayList<>();
	
	public UsuarioDTO() {
	}
	
	public UsuarioDTO(UsuarioLogin usuarioLogin, PessoaFisica pessoaFisica, List<PessoaDocumento> documentos, List<MeioContato> meiosContato){
		this.nome = usuarioLogin.getNomeUsuario();
		this.cpf = pessoaFisica.getCpf();
		this.email = usuarioLogin.getEmail();
		this.metodoLogin = metodoLogin(usuarioLogin.getTipoUsuario());
		if (pessoaFisica.getDataNascimento() != null) {
			this.dataNascimento = new SimpleDateFormat(DATE_PATTERN).format(pessoaFisica.getDataNascimento());
		}
		if (pessoaFisica.getEstadoCivil() != null) {
			this.estadoCivil = pessoaFisica.getEstadoCivil().name();
		}
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
		this.email = usuarioLogin.getEmail();
		this.metodoLogin = metodoLogin(usuarioLogin.getTipoUsuario());
		if (pessoaFisica != null){
        		this.cpf = pessoaFisica.getCpf();
        		if (pessoaFisica.getDataNascimento() != null) {
        			this.dataNascimento = new SimpleDateFormat(DATE_PATTERN).format(pessoaFisica.getDataNascimento());
        		}
        		if (pessoaFisica.getEstadoCivil() != null) {
        			this.estadoCivil = pessoaFisica.getEstadoCivil().name();
        		}
        		List<PessoaDocumento> pessoaDocumentoList = pessoaFisica.getPessoaDocumentoList();
        		if (pessoaDocumentoList != null) {
        			setDocumentos(new ArrayList<PessoaDocumentoDTO>());
        			for (PessoaDocumento pessoaDocumento : pessoaDocumentoList) {
        				getDocumentos().add(new PessoaDocumentoDTO(pessoaDocumento));
        			}
        		}
        		List<MeioContato> meioContatoList = pessoaFisica.getMeioContatoList();
        		if (meioContatoList != null) {
        			setMeiosContato(new ArrayList<MeioContatoDTO>());
        			for (MeioContato meioContato : meioContatoList) {
        				getMeiosContato().add(new MeioContatoDTO(meioContato));
        			}
        		}
		}
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

        public String getMetodoLogin() {
        return metodoLogin;
    }

    public void setMetodoLogin(String metodoLogin) {
        this.metodoLogin = metodoLogin;
    }

        public static UsuarioEnum metodoLogin(String string){
            switch (string) {
            case "CERTIFICADO":
                return UsuarioEnum.C;
            case "SENHA":
                return UsuarioEnum.P;
            case "SEM_LOGIN":
                return UsuarioEnum.S;
            case "SENHA_E_CERTIFICADO":
            default:
                return UsuarioEnum.H;
            }
        }
        
        public static String metodoLogin(UsuarioEnum usuarioEnum){
            switch (usuarioEnum) {
                case C:
                    return "CERTIFICADO";
                case P:
                    return "SENHA";
                case S:
                    return "SEM_LOGIN";
                case H:
                default:
                    return "SENHA_E_CERTIFICADO";
            }
        }

		@Override
		public String toString() {
			return "UsuarioDTO [nome=" + nome + ", cpf=" + cpf + ", email=" + email + ", dataNascimento="
					+ dataNascimento + ", estadoCivil=" + estadoCivil + ", metodoLogin=" + metodoLogin + ", documentos="
					+ documentos + ", meiosContato=" + meiosContato + "]";
		}
	
}
