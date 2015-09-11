package br.com.infox.epp.ws;

import static br.com.infox.epp.ws.messages.WSMessages.ME_LOCALIZACAO_DA_UG_INEXISTENTE;
import static br.com.infox.epp.ws.messages.WSMessages.ME_PAPEL_INEXISTENTE;
import static br.com.infox.epp.ws.messages.WSMessages.ME_PERFIL_INEXISTENTE;
import static br.com.infox.epp.ws.messages.WSMessages.ME_UNIDADE_GESTORA_NAO_CADASTRADA;
import static br.com.infox.epp.ws.messages.WSMessages.ME_USUARIO_INEXISTENTE;
import static br.com.infox.epp.ws.messages.WSMessages.ME_USUARIO_SEM_PERFIL_ASSOCIADO;
import static br.com.infox.epp.ws.messages.WSMessages.MS_SUCESSO_ATUALIZAR;
import static br.com.infox.epp.ws.messages.WSMessages.ME_ATTR_DATAEXPEDICAO_INVALIDO;
import static br.com.infox.epp.ws.messages.WSMessages.ME_ATTR_IDENTIDADE_INVALIDO;
import static br.com.infox.epp.ws.messages.WSMessages.ME_ATTR_ORGAOEXPEDIDOR_INVALIDO;
import static br.com.infox.epp.ws.messages.WSMessages.ME_ENDERECO_INCOMPLETO;
import static br.com.infox.epp.ws.messages.WSMessages.ME_MUNICIPIO_NAO_ENCONTRADO;
import static br.com.infox.epp.ws.messages.WSMessages.ME_PAPEL_INEXISTENTE;
import static br.com.infox.epp.ws.messages.WSMessages.ME_PERFIL_INEXISTENTE;
import static br.com.infox.epp.ws.messages.WSMessages.ME_USUARIO_INEXISTENTE;
import static br.com.infox.epp.ws.messages.WSMessages.ME_USUARIO_SEM_PERFIL_ASSOCIADO;
import static br.com.infox.epp.ws.messages.WSMessages.MS_SUCESSO_ATUALIZAR;
import static br.com.infox.epp.ws.messages.WSMessages.MS_SUCESSO_INSERIR;
import static br.com.infox.epp.ws.messages.WSMessages.ME_ATTR_DATAEXPEDICAO_INVALIDO;
import static br.com.infox.epp.ws.messages.WSMessages.ME_ATTR_IDENTIDADE_INVALIDO;
import static br.com.infox.epp.ws.messages.WSMessages.ME_ATTR_ORGAOEXPEDIDOR_INVALIDO;
import static br.com.infox.epp.ws.messages.WSMessages.ME_ENDERECO_INCOMPLETO;
import static br.com.infox.epp.ws.messages.WSMessages.ME_LOCALIZACAO_DA_UG_INEXISTENTE;
import static br.com.infox.epp.ws.messages.WSMessages.ME_MUNICIPIO_NAO_ENCONTRADO;
import static br.com.infox.epp.ws.messages.WSMessages.ME_ENDERECO_INCOMPLETO;
import static br.com.infox.epp.ws.messages.WSMessages.ME_PAPEL_INEXISTENTE;
import static br.com.infox.epp.ws.messages.WSMessages.ME_PERFIL_INEXISTENTE;
import static br.com.infox.epp.ws.messages.WSMessages.ME_UNIDADE_GESTORA_NAO_CADASTRADA;
import static br.com.infox.epp.ws.messages.WSMessages.ME_USUARIO_INEXISTENTE;
import static br.com.infox.epp.ws.messages.WSMessages.ME_USUARIO_SEM_PERFIL_ASSOCIADO;
import static br.com.infox.epp.ws.messages.WSMessages.MS_SUCESSO_ATUALIZAR;
import static br.com.infox.epp.ws.messages.WSMessages.MS_SUCESSO_INSERIR;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.validation.ValidationException;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Transactional;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.dao.UsuarioLoginDAO;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.access.manager.EstruturaManager;
import br.com.infox.epp.access.manager.LocalizacaoManager;
import br.com.infox.epp.access.manager.PapelManager;
import br.com.infox.epp.access.manager.PerfilTemplateManager;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.access.manager.UsuarioPerfilManager;
import br.com.infox.epp.access.type.UsuarioEnum;
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
import br.com.infox.epp.endereco.entity.Endereco;
import br.com.infox.epp.endereco.entity.PessoaEndereco;
import br.com.infox.epp.endereco.manager.EnderecoManager;
import br.com.infox.epp.endereco.manager.PessoaEnderecoManager;
import br.com.infox.epp.view.municipio.MunicipioDAO;
import br.com.infox.epp.ws.PasswordService;
import br.com.infox.epp.ws.bean.UsuarioBean;
import br.com.infox.epp.ws.bean.UsuarioPerfilBean;
import br.com.infox.epp.ws.bean.UsuarioSenhaBean;

public class UnidadeGestoraService implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "unidadeGestoraService";

	@In
	private UsuarioLoginManager usuarioLoginManager;
	@In
	private UsuarioLoginDAO usuarioLoginDAO;
	@In
	private PessoaFisicaManager pessoaFisicaManager;
	@In
	private MeioContatoManager meioContatoManager;
	@In
	private PessoaDocumentoManager pessoaDocumentoManager;
	@In
	private PasswordService passwordServiceWS;
	@In
	private PapelManager papelManager;
	@In
	private PerfilTemplateManager perfilTemplateManager;
	@In
	private LocalizacaoManager localizacaoManager;
	@In
	private UsuarioPerfilManager usuarioPerfilManager;
	@In
	private EstruturaManager estruturaManager;
	@In
	private ActionMessagesService actionMessagesService;
	@In
	private EnderecoManager enderecoManager;
	@In
	private PessoaEnderecoManager pessoaEnderecoManager;
	@In
	private MunicipioDAO municipioTceDAO;

	@Transactional
	public String gravarUsuario(Object bean) throws DAOException {
		UsuarioBean usuarioBean = (UsuarioBean) bean;
		PessoaFisica pessoaFisica = pessoaFisicaManager.getByCpf(usuarioBean.getCpf());
		return pessoaFisica == null ? inserirUsuario(usuarioBean) : atualizarUsuario(usuarioBean, pessoaFisica);
	}

	public String inserirUsuario(UsuarioBean usuarioBean) throws DAOException {
		PessoaFisica pessoaFisica = createPessoaFisica(usuarioBean);
		UsuarioLogin usuarioLogin = createUsuarioLogin(usuarioBean, pessoaFisica);
		
		pessoaFisicaManager.persist(pessoaFisica);
		usuarioLoginDAO.persist(usuarioLogin);
		
		List<MeioContato> meioContatoList = createMeioContatoList(usuarioBean, pessoaFisica);
		for (MeioContato meioContato : meioContatoList) {
			meioContatoManager.persist(meioContato);
		}
		
		PessoaDocumento pessoaDocumento = createPessoaDocumento(usuarioBean, pessoaFisica);
		if (pessoaDocumento != null) {
		    pessoaDocumentoManager.persist(pessoaDocumento);
		}
		
		if (hasEndereco(usuarioBean)) {
		    if (!isValidEndereco(usuarioBean)) {
        throw new ValidationException(ME_ENDERECO_INCOMPLETO.codigo());
		    }
		    if (!isValidMunicipio(usuarioBean)) {
		        throw new ValidationException(ME_MUNICIPIO_NAO_ENCONTRADO.codigo());
		    }
		    PessoaEndereco pessoaEndereco = createPessoaEndereco(usuarioBean, pessoaFisica);
            if (pessoaEndereco != null) {
                enderecoManager.persist(pessoaEndereco.getEndereco());
                pessoaEnderecoManager.persist(pessoaEndereco);
            }
		}
		
		return MS_SUCESSO_INSERIR.codigo();
	}

	public String atualizarUsuario(UsuarioBean usuarioBean, PessoaFisica pessoaFisica) throws DAOException {
		updatePessoaFisica(usuarioBean, pessoaFisica);
		pessoaFisicaManager.update(pessoaFisica);

		UsuarioLogin usuarioLogin = usuarioLoginManager.getUsuarioLoginByPessoaFisica(pessoaFisica);
		if (usuarioLogin == null) {
			usuarioLogin = createUsuarioLogin(usuarioBean, pessoaFisica);
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

		if (hasEndereco(usuarioBean)) {
			if (!isValidEndereco(usuarioBean)) {
				throw new ValidationException(ME_ENDERECO_INCOMPLETO.codigo());
			}
			if (!isValidMunicipio(usuarioBean)) {
				throw new ValidationException(ME_MUNICIPIO_NAO_ENCONTRADO.codigo());
			}
			PessoaEndereco pessoaEndereco = pessoaEnderecoManager.getByPessoa(pessoaFisica);
			if (pessoaEndereco != null) {
				updatePessoaEndereco(usuarioBean, pessoaEndereco);
			} else {
				PessoaEndereco novoPessoaEndereco = createPessoaEndereco(usuarioBean, pessoaFisica);
				enderecoManager.persist(novoPessoaEndereco.getEndereco());
				pessoaEnderecoManager.persist(novoPessoaEndereco);
			}
		} else {
			PessoaEndereco pessoaEndereco = pessoaEnderecoManager.getByPessoa(pessoaFisica);
			if (pessoaEndereco != null) {
				pessoaEnderecoManager.remove(pessoaEndereco);
				enderecoManager.remove(pessoaEndereco.getEndereco());
			}
		}

		return MS_SUCESSO_ATUALIZAR.codigo();
	}

	public String atualizarSenha(Object bean) throws DAOException {
		UsuarioSenhaBean usuarioSenhaBean = (UsuarioSenhaBean) bean;
		PessoaFisica pessoaFisica = pessoaFisicaManager.getByCpf(usuarioSenhaBean.getCpf());
		UsuarioLogin usuarioLogin = usuarioLoginManager.getUsuarioLoginByPessoaFisica(pessoaFisica);
		if (usuarioLogin == null) {
			return ME_USUARIO_INEXISTENTE.codigo();
		} else {
			usuarioLogin.setSenha(
					passwordServiceWS.generatePasswordHash(usuarioSenhaBean.getSenha(), usuarioLogin.getSalt()));
			usuarioLoginManager.update(usuarioLogin);
			return MS_SUCESSO_ATUALIZAR.codigo();
		}
	}

	public String adicionarPerfil(Object bean) throws DAOException {
		UsuarioPerfilBean usuarioPerfilBean = (UsuarioPerfilBean) bean;
		UsuarioLogin usuarioLogin = usuarioLoginManager.getUsuarioFetchPessoaFisicaByNrCpf(usuarioPerfilBean.getCpf());
		if (usuarioLogin == null) {
			return ME_USUARIO_INEXISTENTE.codigo();
		}

		Papel papel = papelManager.getPapelByIdentificador(usuarioPerfilBean.getPapel());
		if (papel == null) {
			return ME_PAPEL_INEXISTENTE.codigo();
		}

		/*Localizacao localizacaoUG = localizacaoManager
				.getLocalizacaoDentroEstrutura(COD_LOCALIZACAO_GERAL_UNIDADE_GESTORA.getValue());
		if (localizacaoUG == null) {
			return ME_LOCALIZACAO_DA_UG_INEXISTENTE.codigo();
		}

		Localizacao localizacaoDaUG = unidadeGestoraDAO
				.getLocalizacaoByUnidadeGestora(usuarioPerfilBean.getCodigoUnidadeGestora());
		if (localizacaoDaUG == null) {
			return ME_UNIDADE_GESTORA_NAO_CADASTRADA.codigo();
		}

		PerfilTemplate perfilTemplate = getPerfilTemplateCreateIfNotExists(localizacaoUG, papel);

		UsuarioPerfil usuarioPerfil = usuarioPerfilManager.getByUsuarioLoginPerfilTemplateLocalizacao(usuarioLogin,
				perfilTemplate, localizacaoDaUG);
		if (usuarioPerfil != null) {
			return MS_SUCESSO_ATUALIZAR.codigo();
		}

		usuarioPerfilManager.persist(new UsuarioPerfil(usuarioLogin, perfilTemplate, localizacaoDaUG));
		if (!usuarioLogin.getAtivo()) {
			usuarioLogin.setAtivo(Boolean.TRUE);
			usuarioLoginManager.update(usuarioLogin);
		}*/

		return MS_SUCESSO_INSERIR.codigo();
	}

	public String removerPerfil(Object bean) throws DAOException {
		UsuarioPerfilBean usuarioPerfilBean = (UsuarioPerfilBean) bean;
        UsuarioLogin usuarioLogin = usuarioLoginManager.getUsuarioFetchPessoaFisicaByNrCpf(usuarioPerfilBean.getCpf());
        if (usuarioLogin == null) {
        	return ME_USUARIO_INEXISTENTE.codigo();
        }
        
        Papel papel = papelManager.getPapelByIdentificador(usuarioPerfilBean.getPapel());
        if (papel == null) {
        	return ME_PAPEL_INEXISTENTE.codigo();
        }
        
        /*Localizacao localizacaoUG = localizacaoManager.getLocalizacaoDentroEstrutura(COD_LOCALIZACAO_GERAL_UNIDADE_GESTORA.getValue());
        if (localizacaoUG == null){
        	return ME_LOCALIZACAO_DA_UG_INEXISTENTE.codigo();
        }
        
        Localizacao localizacaoPai = unidadeGestoraDAO.getLocalizacaoByUnidadeGestora(usuarioPerfilBean.getCodigoUnidadeGestora());
        if (localizacaoPai == null){
        	return ME_UNIDADE_GESTORA_NAO_CADASTRADA.codigo();
        }
        
        PerfilTemplate perfilTemplate = perfilTemplateManager.getByLocalizacaoPapel(localizacaoUG, papel);
        if (perfilTemplate == null){
        	return ME_PERFIL_INEXISTENTE.codigo();
        }
        
        UsuarioPerfil usuarioPerfil = usuarioPerfilManager.getByUsuarioLoginPerfilTemplateLocalizacao(usuarioLogin, perfilTemplate, localizacaoPai);
        if (usuarioPerfil == null){
        	return ME_USUARIO_SEM_PERFIL_ASSOCIADO.codigo();
        }
        
        usuarioPerfilManager.removeByUsuarioPerfilTemplateLocalizacao(usuarioLogin, perfilTemplate, localizacaoPai);*/
        return MS_SUCESSO_ATUALIZAR.codigo();
	}

	private UsuarioLogin createUsuarioLogin(UsuarioBean usuarioBean, PessoaFisica pessoaFisica) {
		UsuarioLogin usuarioLogin = new UsuarioLogin();
		usuarioLogin.setTipoUsuario(UsuarioEnum.H);
		usuarioLogin.setNomeUsuario(usuarioBean.getNome());
		usuarioLogin.setLogin(usuarioBean.getCpf());
		usuarioLogin.setEmail(usuarioBean.getEmail());
		usuarioLogin.setAtivo(Boolean.FALSE);
		usuarioLogin.setBloqueio(Boolean.FALSE);
		usuarioLogin.setProvisorio(Boolean.FALSE);
		usuarioLogin.setPessoaFisica(pessoaFisica);

		String salt = passwordServiceWS.generatePasswordSalt();
		String randomPassword = passwordServiceWS.generatePasswordSalt();

		usuarioLogin.setSalt(salt);
		usuarioLogin.setSenha(passwordServiceWS.generatePasswordHash(randomPassword, salt));
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

	private PessoaEndereco createPessoaEndereco(UsuarioBean usuarioBean, PessoaFisica pessoaFisica) {
		Endereco endereco = new Endereco();
		endereco.setLogradouro(usuarioBean.getLogradouro());
		endereco.setComplemento(usuarioBean.getComplemento());
		endereco.setNumero(usuarioBean.getNumeroEndereco());
		endereco.setBairro(usuarioBean.getBairro());
		endereco.setCep(usuarioBean.getCep());
		endereco.setCodigoMunicipio(usuarioBean.getCodigoMunicipio());

		return new PessoaEndereco(pessoaFisica, endereco);
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

	private void updatePessoaEndereco(UsuarioBean usuarioBean, PessoaEndereco pessoaEndereco) throws DAOException {
		Endereco endereco = pessoaEndereco.getEndereco();
		endereco.setBairro(usuarioBean.getBairro());
		endereco.setCep(usuarioBean.getCep());
		endereco.setComplemento(usuarioBean.getComplemento());
		endereco.setLogradouro(usuarioBean.getLogradouro());
		endereco.setNumero(usuarioBean.getNumeroEndereco());
		endereco.setCodigoMunicipio(usuarioBean.getCodigoMunicipio());
		enderecoManager.update(endereco);
	}

	private PerfilTemplate getPerfilTemplateCreateIfNotExists(Localizacao localizacao, Papel papel)
			throws DAOException {
		PerfilTemplate perfilTemplate = perfilTemplateManager.getByLocalizacaoPapel(localizacao, papel);
		if (perfilTemplate == null) {
			perfilTemplate = perfilTemplateManager.persist(new PerfilTemplate(localizacao, papel));
		}
		return perfilTemplate;
	}

	private boolean hasPessoaDocumento(UsuarioBean usuarioBean) {
		return usuarioBean.getDataExpedicao() != null || usuarioBean.getIdentidade() != null
				|| usuarioBean.getOrgaoExpedidor() != null;
	}

	private boolean hasEndereco(UsuarioBean usuarioBean) {
		if (usuarioBean.getLogradouro() != null || usuarioBean.getBairro() != null || usuarioBean.getCep() != null
				|| usuarioBean.getComplemento() != null || usuarioBean.getNumeroEndereco() != null
				|| usuarioBean.getCodigoMunicipio() != null)
			return true;

		return false;
	}

	private boolean isValidEndereco(UsuarioBean usuarioBean) {
		if (usuarioBean.getLogradouro() != null && usuarioBean.getCep() != null
				&& usuarioBean.getNumeroEndereco() != null && usuarioBean.getCodigoMunicipio() != null)
			return true;
		return false;
	}

	private boolean isValidMunicipio(UsuarioBean usuarioBean) throws DAOException {
		//String municipio = municipioTceDAO.getNomeMunicipio(usuarioBean.getCodigoMunicipio());
		//return municipio != null && !"".equals(municipio);
		throw new UnsupportedOperationException();
	}

	private void validatePessoaDocumento(UsuarioBean usuarioBean) {
		if (usuarioBean.getDataExpedicao() == null) {
			throw new ValidationException(ME_ATTR_DATAEXPEDICAO_INVALIDO.codigo());
		}
		if (usuarioBean.getIdentidade() == null) {
			throw new ValidationException(ME_ATTR_IDENTIDADE_INVALIDO.codigo());
		}
		if (usuarioBean.getOrgaoExpedidor() == null) {
			throw new ValidationException(ME_ATTR_ORGAOEXPEDIDOR_INVALIDO.codigo());
		}
	}
}