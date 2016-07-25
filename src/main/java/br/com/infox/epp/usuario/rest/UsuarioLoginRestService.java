package br.com.infox.epp.usuario.rest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;

import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.access.type.UsuarioEnum;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.manager.PessoaFisicaManager;
import br.com.infox.epp.pessoa.type.EstadoCivilEnum;
import br.com.infox.epp.pessoa.type.TipoPessoaEnum;
import br.com.infox.epp.pessoaFisica.PessoaFisicaSearch;
import br.com.infox.epp.usuario.UsuarioDTOSearch;
import br.com.infox.epp.usuario.UsuarioLoginSearch;
import br.com.infox.epp.ws.interceptors.TokenAuthentication;
import br.com.infox.epp.ws.interceptors.ValidarParametros;

@Stateless
@TokenAuthentication
@ValidarParametros
public class UsuarioLoginRestService {

	@Inject
	private UsuarioLoginManager usuarioLoginManager;
	@Inject
	private UsuarioLoginSearch usuarioSearch;
	@Inject
	private UsuarioDTOSearch usuarioDTOSearch;
	@Inject
	private PessoaFisicaManager pessoaFisicaManager;
	@Inject
	private PessoaFisicaSearch pessoaFisicaSearch;
	
	public void atualizarUsuario(String cpf, UsuarioDTO usuarioDTO) {
		UsuarioLogin usuarioLogin = usuarioSearch.getUsuarioLoginByCpf(cpf);
		PessoaFisica pessoaFisica = pessoaFisicaSearch.getByCpf(cpf);
		aplicarValores(usuarioDTO, pessoaFisica);
		aplicarValores(usuarioDTO, usuarioLogin);
		usuarioLoginManager.update(usuarioLogin);
	}

	public UsuarioDTO getUsuarioByCpf(String cpf) {
		UsuarioLogin usuarioLogin = usuarioSearch.getUsuarioLoginByCpf(cpf);
		PessoaFisica pessoaFisica = pessoaFisicaSearch.getByCpf(cpf);
		return new UsuarioDTO(usuarioLogin, pessoaFisica);
	}

	public void removerUsuario(String cpf) {
		UsuarioLogin usuarioLogin = usuarioSearch.getUsuarioLoginByCpf(cpf);
		usuarioLogin.setAtivo(Boolean.FALSE);
		usuarioLoginManager.update(usuarioLogin);
	}
	
	private PessoaFisica getPessoaFisica(UsuarioDTO usuarioDTO) {
		PessoaFisica pessoaFisica = pessoaFisicaSearch.getByCpf(usuarioDTO.getCpf());
		if (pessoaFisica == null){
			pessoaFisica = aplicarValores(usuarioDTO, new PessoaFisica());
			pessoaFisicaManager.persist(pessoaFisica);
		}
		return pessoaFisica;
	}
	
	public void adicionarUsuario(UsuarioDTO usuarioDTO, boolean sendPasswordToMail) {
		UsuarioLogin usuarioLogin = aplicarValores(usuarioDTO, new UsuarioLogin());
		usuarioLogin.setAtivo(Boolean.TRUE);
		usuarioLogin.setBloqueio(Boolean.FALSE);
		usuarioLogin.setProvisorio(Boolean.FALSE);
		usuarioLoginManager.persist(usuarioLogin, sendPasswordToMail);
	}
	
	public void adicionarUsuario(UsuarioDTO usuarioDTO) {
		adicionarUsuario(usuarioDTO, false);
	}

	private UsuarioLogin aplicarValores(UsuarioDTO usuarioDTO, UsuarioLogin usuarioLogin) {
		usuarioLogin.setEmail(usuarioDTO.getEmail());
		usuarioLogin.setNomeUsuario(usuarioDTO.getNome());
		usuarioLogin.setLogin(usuarioDTO.getCpf());
		usuarioLogin.setTipoUsuario(UsuarioEnum.H);
		usuarioLogin.setPessoaFisica(getPessoaFisica(usuarioDTO));
		return usuarioLogin;
	}
	
	private PessoaFisica aplicarValores(UsuarioDTO usuarioDTO, PessoaFisica pessoaFisica) {
		pessoaFisica.setNome(usuarioDTO.getNome());
		pessoaFisica.setCpf(usuarioDTO.getCpf());
		pessoaFisica.setTipoPessoa(TipoPessoaEnum.F);
		if (usuarioDTO.getEstadoCivil() != null) {
			pessoaFisica.setEstadoCivil(EstadoCivilEnum.valueOf(usuarioDTO.getEstadoCivil()));
		} else {
			pessoaFisica.setEstadoCivil(EstadoCivilEnum.N);
		}
		if (usuarioDTO.getDataNascimento() != null) {
			try {
				pessoaFisica.setDataNascimento(new SimpleDateFormat(ConstantesDTO.DATE_PATTERN).parse(usuarioDTO.getDataNascimento()));
			} catch (ParseException e){
				throw new WebApplicationException(e, 400);
			}
		} else {
			pessoaFisica.setDataNascimento(null);
		}
		return pessoaFisica;
	}

	public List<UsuarioDTO> getUsuarios() {
		return usuarioDTOSearch.getUsuarioDTOList();
	}
		
}
