package br.com.infox.epp.ws.services;

import static br.com.infox.epp.ws.messages.WSMessages.ME_ATTR_IDENTIDADE_INVALIDO;
import static br.com.infox.epp.ws.messages.WSMessages.ME_USUARIO_INEXISTENTE;
import static br.com.infox.epp.ws.messages.WSMessages.MS_SUCESSO_ATUALIZAR;
import static br.com.infox.epp.ws.messages.WSMessages.MS_SUCESSO_INSERIR;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.ValidationException;

import org.jboss.seam.util.RandomStringUtils;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.access.service.PasswordService;
import br.com.infox.epp.access.type.UsuarioEnum;
import br.com.infox.epp.mail.service.AccessMailService;
import br.com.infox.epp.meiocontato.entity.MeioContato;
import br.com.infox.epp.meiocontato.manager.MeioContatoManager;
import br.com.infox.epp.pessoa.documento.entity.PessoaDocumento;
import br.com.infox.epp.pessoa.documento.manager.PessoaDocumentoManager;
import br.com.infox.epp.pessoa.documento.type.TipoPesssoaDocumentoEnum;
import br.com.infox.epp.pessoa.entity.Pessoa;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.manager.PessoaFisicaManager;
import br.com.infox.epp.pessoa.type.EstadoCivilEnum;
import br.com.infox.epp.pessoa.type.TipoPessoaEnum;
import br.com.infox.epp.ws.bean.UsuarioBean;
import br.com.infox.epp.ws.bean.UsuarioSenhaBean;
import br.com.infox.epp.ws.interceptors.Log;
import br.com.infox.epp.ws.interceptors.TokenAuthentication;
import br.com.infox.epp.ws.interceptors.ValidarParametros;
import br.com.infox.epp.ws.interceptors.TokenAuthentication.TipoExcecao;
import br.com.infox.epp.ws.messages.CodigosServicos;

@Stateless
@TokenAuthentication(tipoExcecao=TipoExcecao.STRING)
@ValidarParametros
public class UsuarioRestService {

	@Inject
	private UsuarioLoginManager usuarioLoginManager;
	@Inject
	private PessoaFisicaManager pessoaFisicaManager;
	@Inject
	private MeioContatoManager meioContatoManager;
	@Inject
	private PessoaDocumentoManager pessoaDocumentoManager;
	@Inject
	private PasswordService passwordService;
	@Inject
	private AccessMailService accessMailService;

	@Log(codigo = CodigosServicos.WS_PERFIS_GRAVAR_USUARIO)
	public String gravarUsuario(Object bean) throws DAOException {
		UsuarioBean usuarioBean = (UsuarioBean) bean;
		PessoaFisica pessoaFisica = pessoaFisicaManager.getByCpf(usuarioBean.getCpf());
		return pessoaFisica == null ? inserirUsuario(usuarioBean) : atualizarUsuario(usuarioBean, pessoaFisica);
	}

	public String inserirUsuario(UsuarioBean usuarioBean) throws DAOException {
		PessoaFisica pessoaFisica = createPessoaFisica(usuarioBean);
		String randomPassword = RandomStringUtils.randomAlphabetic(8);
		UsuarioLogin usuarioLogin = createUsuarioLogin(usuarioBean, pessoaFisica, randomPassword);

		pessoaFisicaManager.persist(pessoaFisica);
		usuarioLoginManager.update(usuarioLogin);
		
		accessMailService.enviarEmailDeMudancaDeSenha("email", usuarioLogin, randomPassword);

		List<MeioContato> meioContatoList = createMeioContatoList(usuarioBean, pessoaFisica);
		for (MeioContato meioContato : meioContatoList) {
			meioContatoManager.persist(meioContato);
		}

		PessoaDocumento pessoaDocumento = createPessoaDocumento(usuarioBean, pessoaFisica);
		if (pessoaDocumento != null) {
			pessoaDocumentoManager.persist(pessoaDocumento);
		}

		return MS_SUCESSO_INSERIR.codigo();
	}

	public String atualizarUsuario(UsuarioBean usuarioBean, PessoaFisica pessoaFisica) throws DAOException {
		updatePessoaFisica(usuarioBean, pessoaFisica);
		pessoaFisicaManager.update(pessoaFisica);

		UsuarioLogin usuarioLogin = usuarioLoginManager.getUsuarioLoginByPessoaFisica(pessoaFisica);
		if (usuarioLogin == null) {
		    String randomPassword = RandomStringUtils.randomAlphabetic(8);
			usuarioLogin = createUsuarioLogin(usuarioBean, pessoaFisica, randomPassword);
		} else {
			updateUsuarioLogin(usuarioBean, usuarioLogin);
		}
		usuarioLoginManager.update(usuarioLogin);

		updateMeioContatoList(usuarioBean, pessoaFisica);

		PessoaDocumento pessoaDocumento = pessoaDocumentoManager.getPessoaDocumentoByPessoaTipoDocumento(pessoaFisica,
				TipoPesssoaDocumentoEnum.CI);
		if (pessoaDocumento == null) {
			pessoaDocumento = createPessoaDocumento(usuarioBean, pessoaFisica);
			if (pessoaDocumento != null) {
				pessoaDocumentoManager.persist(pessoaDocumento);
			}
		} else {
			if (hasPessoaDocumento(usuarioBean)) {
				updatePessoaDocumento(usuarioBean, pessoaDocumento);
				pessoaDocumentoManager.update(pessoaDocumento);
			} else {
				pessoaDocumentoManager.remove(pessoaDocumento);
			}
		}

		return MS_SUCESSO_ATUALIZAR.codigo();
	}

	@Log(codigo = CodigosServicos.WS_PERFIS_ATUALIZAR_SENHA)
	public String atualizarSenha(Object bean) throws DAOException {
		UsuarioSenhaBean usuarioSenhaBean = (UsuarioSenhaBean) bean;
		PessoaFisica pessoaFisica = pessoaFisicaManager.getByCpf(usuarioSenhaBean.getCpf());
		UsuarioLogin usuarioLogin = usuarioLoginManager.getUsuarioLoginByPessoaFisica(pessoaFisica);
		if (usuarioLogin == null) {
			return ME_USUARIO_INEXISTENTE.codigo();
		} else {
			usuarioLogin.setSenha(
					passwordService.generatePasswordHash(usuarioSenhaBean.getSenha(), usuarioLogin.getSalt()));
			usuarioLoginManager.update(usuarioLogin);
			return MS_SUCESSO_ATUALIZAR.codigo();
		}
	}

	private UsuarioLogin createUsuarioLogin(UsuarioBean usuarioBean, PessoaFisica pessoaFisica, String randomPassword) {
		UsuarioLogin usuarioLogin = new UsuarioLogin();
		usuarioLogin.setTipoUsuario(UsuarioEnum.H);
		usuarioLogin.setNomeUsuario(usuarioBean.getNome());
		usuarioLogin.setLogin(usuarioBean.getCpf());
		usuarioLogin.setEmail(usuarioBean.getEmail());
		usuarioLogin.setAtivo(Boolean.TRUE);
		usuarioLogin.setBloqueio(Boolean.FALSE);
		usuarioLogin.setProvisorio(Boolean.FALSE);
		usuarioLogin.setPessoaFisica(pessoaFisica);

		String salt = passwordService.generatePasswordSalt();

		usuarioLogin.setSalt(salt);
		usuarioLogin.setSenha(passwordService.generatePasswordHash(randomPassword, salt));
		return usuarioLogin;
	}

	private PessoaFisica createPessoaFisica(UsuarioBean usuarioBean) {
		PessoaFisica pessoaFisica = new PessoaFisica();
		pessoaFisica.setNome(usuarioBean.getNome());
		pessoaFisica.setCpf(usuarioBean.getCpf());
		pessoaFisica.setTipoPessoa(TipoPessoaEnum.F);
		pessoaFisica.setAtivo(Boolean.FALSE);

		if (usuarioBean.getEstadoCivil() != null) {
			pessoaFisica.setEstadoCivil(EstadoCivilEnum.valueOf(usuarioBean.getEstadoCivil()));
		} else {
			pessoaFisica.setEstadoCivil(EstadoCivilEnum.N);
		}

		if (usuarioBean.getDataNascimento() != null) {
			pessoaFisica.setDataNascimento(usuarioBean.getDataNascimentoAsDate());
		} else {
			pessoaFisica.setDataNascimento(null);
		}
		return pessoaFisica;
	}

	private List<MeioContato> createMeioContatoList(UsuarioBean usuarioBean, Pessoa pessoa) {
		List<MeioContato> meioContatoList = new ArrayList<>(4);

		if (usuarioBean.getTelefoneFixo() != null) {
			meioContatoList
					.add(meioContatoManager.createMeioContatoTelefoneFixo(usuarioBean.getTelefoneFixo(), pessoa));
		}

		if (usuarioBean.getTelefoneMovel() != null) {
			meioContatoList
					.add(meioContatoManager.createMeioContatoTelefoneMovel(usuarioBean.getTelefoneMovel(), pessoa));
		}

		if (usuarioBean.getEmailOpcional1() != null) {
			meioContatoList.add(meioContatoManager.createMeioContatoEmail(usuarioBean.getEmailOpcional1(), pessoa));
		}

		if (usuarioBean.getEmailOpcional2() != null) {
			meioContatoList.add(meioContatoManager.createMeioContatoEmail(usuarioBean.getEmailOpcional2(), pessoa));
		}

		return meioContatoList;
	}

	private PessoaDocumento createPessoaDocumento(UsuarioBean usuarioBean, PessoaFisica pessoaFisica) {
		PessoaDocumento pessoaDocumento = new PessoaDocumento();

		if (usuarioBean.getIdentidade() != null || usuarioBean.getDataExpedicao() != null
				|| usuarioBean.getOrgaoExpedidor() != null) {
			validatePessoaDocumento(usuarioBean);
			pessoaDocumento.setDocumento(usuarioBean.getIdentidade());
			pessoaDocumento.setOrgaoEmissor(usuarioBean.getOrgaoExpedidor());
			pessoaDocumento.setDataEmissao(usuarioBean.getDataExpedicaoAsDate());
			pessoaDocumento.setTipoDocumento(TipoPesssoaDocumentoEnum.CI);
			pessoaDocumento.setPessoa(pessoaFisica);
			return pessoaDocumento;
		}
		return null;
	}

	private void updatePessoaFisica(UsuarioBean usuarioBean, PessoaFisica pessoaFisica) {
		pessoaFisica.setNome(usuarioBean.getNome());
		if (usuarioBean.getDataNascimento() != null) {
			pessoaFisica.setDataNascimento(usuarioBean.getDataNascimentoAsDate());
		} else {
			pessoaFisica.setDataNascimento(null);
		}
		if (usuarioBean.getEstadoCivil() != null) {
			pessoaFisica.setEstadoCivil(EstadoCivilEnum.valueOf(usuarioBean.getEstadoCivil()));
		} else {
			pessoaFisica.setEstadoCivil(EstadoCivilEnum.N);
		}
	}

	private void updateUsuarioLogin(UsuarioBean usuarioBean, UsuarioLogin usuarioLogin) {
		usuarioLogin.setEmail(usuarioBean.getEmail());
		usuarioLogin.setNomeUsuario(usuarioBean.getNome());
		usuarioLogin.setLogin(usuarioBean.getCpf());
	}

	private void updatePessoaDocumento(UsuarioBean usuarioBean, PessoaDocumento pessoaDocumento) {
		validatePessoaDocumento(usuarioBean);
		pessoaDocumento.setDataEmissao(usuarioBean.getDataExpedicaoAsDate());
		pessoaDocumento.setDocumento(usuarioBean.getIdentidade());
		pessoaDocumento.setOrgaoEmissor(usuarioBean.getOrgaoExpedidor());
	}

	private void updateMeioContatoList(UsuarioBean usuarioBean, Pessoa pessoa) throws DAOException {
		meioContatoManager.removeMeioContatoByPessoa(pessoa);
		List<MeioContato> meioContatoList = createMeioContatoList(usuarioBean, pessoa);
		for (MeioContato meioContato : meioContatoList) {
			meioContatoManager.persist(meioContato);
		}
	}

	private boolean hasPessoaDocumento(UsuarioBean usuarioBean) {
		return usuarioBean.getDataExpedicao() != null || usuarioBean.getIdentidade() != null
				|| usuarioBean.getOrgaoExpedidor() != null;
	}

	private void validatePessoaDocumento(UsuarioBean usuarioBean) {
		if (usuarioBean.getIdentidade() == null) {
			throw new ValidationException(ME_ATTR_IDENTIDADE_INVALIDO.codigo());
		}
	}
}