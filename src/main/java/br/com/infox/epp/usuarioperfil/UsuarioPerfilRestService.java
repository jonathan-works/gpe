package br.com.infox.epp.usuarioperfil;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ValidationException;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.access.manager.LocalizacaoManager;
import br.com.infox.epp.access.manager.PerfilTemplateManager;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.access.manager.UsuarioPerfilManager;
import br.com.infox.epp.ws.exception.ConflictWSException;
import br.com.infox.epp.ws.interceptors.TokenAuthentication;
import br.com.infox.epp.ws.interceptors.ValidarParametros;

@TokenAuthentication
@ValidarParametros
@Stateless
public class UsuarioPerfilRestService {

	@Inject
	private UsuarioPerfilManager usuarioPerfilManager;
	@Inject
	private UsuarioLoginManager usuarioLoginManager;
	@Inject
	private LocalizacaoManager localizacaoManager;
	@Inject
	private PerfilTemplateManager perfilTemplateManager;
	
	private static class RelacionamentosUsuarioPerfil {
		private UsuarioLogin usuarioLogin;
		private PerfilTemplate perfilTemplate;
		private Localizacao localizacao;
		
		public RelacionamentosUsuarioPerfil(UsuarioLogin usuarioLogin, PerfilTemplate perfilTemplate, Localizacao localizacao) {
			super();
			this.usuarioLogin = usuarioLogin;
			this.perfilTemplate = perfilTemplate;
			this.localizacao = localizacao;
		}
		public UsuarioLogin getUsuarioLogin() {
			return usuarioLogin;
		}
		public PerfilTemplate getPerfilTemplate() {
			return perfilTemplate;
		}
		public Localizacao getLocalizacao() {
			return localizacao;
		}
		
		
	}
	
	private RelacionamentosUsuarioPerfil carregarRelacionamentos(String cpf, String codigoPerfil, String codigoLocalizacao) {
		UsuarioLogin usuarioLogin = usuarioLoginManager.getUsuarioFetchPessoaFisicaByNrCpf(cpf);
		if(usuarioLogin == null) {
			throw new ValidationException(String.format("Usuário com CPF %s não encontrado", cpf));
		}		
		PerfilTemplate perfilTemplate = perfilTemplateManager.getPerfilTemplateByCodigo(codigoPerfil);
		if(perfilTemplate == null) {
			throw new ValidationException(String.format("Perfil com código %s não encontrado", codigoPerfil));
		}
		Localizacao localizacao = localizacaoManager.getLocalizacaoByCodigo(codigoLocalizacao);
		if(localizacao == null) {
			throw new ValidationException(String.format("Localização com código %s não encontrada", codigoLocalizacao));
		}
		
		RelacionamentosUsuarioPerfil retorno = new RelacionamentosUsuarioPerfil(usuarioLogin, perfilTemplate, localizacao);
		
		return retorno;		
	}
	
	private UsuarioPerfilDTO toUsuarioPerfilDTO(UsuarioPerfil usuarioPerfil) {
		UsuarioPerfilDTO retorno = new UsuarioPerfilDTO();
		retorno.setUsuario(usuarioPerfil.getUsuarioLogin().getPessoaFisica().getCpf());
		retorno.setPerfil(usuarioPerfil.getPerfilTemplate().getCodigo());
		retorno.setLocalizacao(usuarioPerfil.getLocalizacao().getCodigo());
		retorno.setResponsavel(usuarioPerfil.getResponsavelLocalizacao());
		
		return retorno;
	}
	
	private void atualizarUsuarioPerfil(UsuarioPerfil usuarioPerfil, UsuarioPerfilDTO novosValores) {
		RelacionamentosUsuarioPerfil relacionamentos = carregarRelacionamentos(novosValores.getUsuario(), novosValores.getPerfil(), novosValores.getLocalizacao());
		
		usuarioPerfil.setUsuarioLogin(relacionamentos.getUsuarioLogin());
		usuarioPerfil.setPerfilTemplate(relacionamentos.getPerfilTemplate());
		usuarioPerfil.setLocalizacao(relacionamentos.getLocalizacao());
		usuarioPerfil.setResponsavelLocalizacao(novosValores.isResponsavel());
	}
	
	public UsuarioPerfilDTO find(String cpf, String codigoPerfil, String codigoLocalizacao) {
		return toUsuarioPerfilDTO(getUsuarioPerfil(cpf, codigoPerfil, codigoLocalizacao));
	}
	private UsuarioPerfil getUsuarioPerfil(String cpf, String codigoPerfil, String codigoLocalizacao) {
		UsuarioPerfilDTO usuarioPerfilDTO = new UsuarioPerfilDTO(cpf, codigoPerfil, codigoLocalizacao);
		
		
		UsuarioPerfil retorno = getUsuarioPerfil(usuarioPerfilDTO);
		if(retorno == null) {
			throw new NoResultException();
		}
		return retorno;
	}
	
	
	private UsuarioPerfil getUsuarioPerfil(UsuarioPerfilDTO usuarioPerfilDTO) {
		RelacionamentosUsuarioPerfil relacionamentos = carregarRelacionamentos(usuarioPerfilDTO.getUsuario(), usuarioPerfilDTO.getPerfil(), usuarioPerfilDTO.getLocalizacao());
		
		return usuarioPerfilManager.getByUsuarioLoginPerfilTemplateLocalizacao(relacionamentos.getUsuarioLogin(), relacionamentos.getPerfilTemplate(), relacionamentos.getLocalizacao());		
	}
	
	public List<UsuarioPerfilDTO> listar(String cpf) {
		if(cpf == null) {
			throw new ValidationException("Deve ser informado o CPF do usuário");
		}
		UsuarioLogin usuarioLogin = usuarioLoginManager.getUsuarioFetchPessoaFisicaByNrCpf(cpf);
		List<UsuarioPerfil> usuarios = usuarioPerfilManager.listByUsuarioLogin(usuarioLogin);
		List<UsuarioPerfilDTO> retorno = new ArrayList<>();
		for(UsuarioPerfil usuario : usuarios) {
			retorno.add(toUsuarioPerfilDTO(usuario));
		}
		
		return retorno;
	}
	
	public void novo(UsuarioPerfilDTO usuarioPerfilDTO) {
		UsuarioPerfil usuarioBanco = getUsuarioPerfil(usuarioPerfilDTO);
		if(usuarioBanco != null) {
		    throw new ConflictWSException(InfoxMessages.getInstance().get("constraintViolation.uniqueViolation"));
		}
		
		UsuarioPerfil usuarioPerfil = new UsuarioPerfil();
		atualizarUsuarioPerfil(usuarioPerfil, usuarioPerfilDTO);
		usuarioPerfilManager.persist(usuarioPerfil);
	}
	
	public void atualizar(String cpf, String codigoPerfil, String codigoLocalizacao, UsuarioPerfilDTO usuarioPerfilDTO) {
		UsuarioPerfil usuarioPerfil = getUsuarioPerfil(cpf, codigoPerfil, codigoLocalizacao);
		
		UsuarioPerfil usuarioBanco = getUsuarioPerfil(usuarioPerfilDTO);
		if(usuarioBanco != null && !usuarioBanco.equals(usuarioPerfil)) {
			throw new ValidationException("UsuarioPerfil já cadastrado");
		}
		
		atualizarUsuarioPerfil(usuarioPerfil, usuarioPerfilDTO);
		usuarioPerfilManager.update(usuarioPerfil);
	}
	
	public void delete(String cpf, String codigoPerfil, String codigoLocalizacao) {
		UsuarioPerfil usuarioPerfil = getUsuarioPerfil(cpf, codigoPerfil, codigoLocalizacao);
		usuarioPerfilManager.remove(usuarioPerfil);
	}
}
