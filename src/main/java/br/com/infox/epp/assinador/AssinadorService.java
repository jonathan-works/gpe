package br.com.infox.epp.assinador;

import java.io.Serializable;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertPathValidatorException.BasicReason;
import java.security.cert.CertPathValidatorException.Reason;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.validation.ValidationException;

import br.com.infox.certificado.CertificadoGenerico;
import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.access.manager.LocalizacaoManager;
import br.com.infox.epp.access.manager.PerfilTemplateManager;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.access.manager.UsuarioPerfilManager;
import br.com.infox.epp.assinador.AssinadorGroupService.StatusToken;
import br.com.infox.epp.assinador.assinavel.AssinavelDocumentoBinProvider;
import br.com.infox.epp.assinador.assinavel.AssinavelGenericoProvider;
import br.com.infox.epp.assinador.assinavel.AssinavelProvider;
import br.com.infox.epp.assinador.assinavel.AssinavelSource;
import br.com.infox.epp.certificado.entity.TipoAssinatura;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumentoService;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaException;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.system.util.ParametroUtil;

public class AssinadorService implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String NOME_CACHE_DOCUMENTOS = "AssinadorService.listaDocumentos";

	@Inject
	private AssinadorGroupService groupService;
	@Inject
	private CMSAdapter cmsAdapter;
	@Inject
	private DocumentoBinManager documentoBinManager;
	@Inject
	private UsuarioLoginManager usuarioLoginManager;
	@Inject
	private PerfilTemplateManager perfilTemplateManager;
	@Inject
	private LocalizacaoManager localizacaoManager;
	@Inject
	private UsuarioPerfilManager usuarioPerfilManager;
	@Inject
	private AssinaturaDocumentoService assinaturaDocumentoService;
	@Inject
	private CertificateValidator certificateValidator;

	@Inject
	private Logger logger;

	public String criarListaAssinaveis(AssinavelProvider assinavelProvider) {
		return groupService.createNewGroupWithAssinavelProvider(assinavelProvider);
	}

	public DadosAssinaturaLegada getDadosAssinaturaLegada(byte[] signature) {
		return cmsAdapter.convert(signature);
	}

	private String getCpf(DadosAssinaturaLegada dadosAssinaturaLegada) {
		CertificadoGenerico certificadoGenerico = null;
		try {
			certificadoGenerico = new CertificadoGenerico(dadosAssinaturaLegada.getCertChainBase64());
		} catch (CertificadoException e) {
			throw new ValidationException("Erro ao carregar certificado", e);
		}
		String cpf = certificadoGenerico.getCPF();
		return cpf;
	}

	private UsuarioPerfil getUsuarioPerfil(String cpf, String codigoPerfil, String codigoLocalizacao) {
		PerfilTemplate perfilTemplate = perfilTemplateManager.getPerfilTemplateByCodigo(codigoPerfil);
		if (perfilTemplate == null) {
			throw new ValidationException("Perfil não encontrado");
		}
		Localizacao localizacao = localizacaoManager.getLocalizacaoByCodigo(codigoLocalizacao);
		if (localizacao == null) {
			throw new ValidationException("Localização não encontrada");
		}
		UsuarioLogin usuarioLogin = usuarioLoginManager.getUsuarioFetchPessoaFisicaByNrCpf(cpf);
		if (usuarioLogin == null) {
			throw new ValidationException("Nenhum usuário encontrado no sistema correspondente ao CPF do certificado");
		}

		UsuarioPerfil retorno = usuarioPerfilManager.getByUsuarioLoginPerfilTemplateLocalizacao(usuarioLogin,
				perfilTemplate, localizacao);
		if (retorno == null) {
			throw new ValidationException("UsuarioPerfil não encontrado");
		}
		return retorno;
	}

	public void apagarGrupo(String token) {
		groupService.apagarGrupo(token);
	}

	public void cancelar(String tokenGrupo) {
		groupService.cancelar(tokenGrupo);
	}

	public void setAssinaturaAssinavel(String tokenGrupo, UUID uuidAssinavel, byte[] signature) {
		DadosAssinaturaLegada dadosAssinaturaLegada = cmsAdapter.convert(signature);
		groupService.atualizarAssinaturaTemporaria(tokenGrupo, uuidAssinavel, dadosAssinaturaLegada);
	}

	public List<DadosAssinatura> getDadosAssinatura(String tokenGrupo) {
		return groupService.getDadosAssinatura(tokenGrupo);
	}

	public void assinar(String tokenGrupo, UsuarioPerfil usuarioPerfil) throws AssinaturaException {
		List<DadosAssinatura> dadosAssinaturas = groupService.getDadosAssinatura(tokenGrupo);
		for (DadosAssinatura dadosAssinatura : dadosAssinaturas) {
			assinar(dadosAssinatura, usuarioPerfil);
		}
	}

	public void assinar(String tokenGrupo, UUID uuidAssinavel, UsuarioPerfil usuarioPerfil) throws AssinaturaException {
		DadosAssinatura dadosAssinatura = groupService.getDadosAssinatura(tokenGrupo, uuidAssinavel);
		assinar(dadosAssinatura, usuarioPerfil);
	}

	protected void assinar(DadosAssinatura dadosAssinatura, UsuarioPerfil usuarioPerfil) throws AssinaturaException {
		validarAssinatura(dadosAssinatura, usuarioPerfil.getUsuarioLogin());
		Integer idDocumentoBin = dadosAssinatura.getIdDocumentoBin();
		if (idDocumentoBin != null) {
			DocumentoBin documentoBin = documentoBinManager.find(idDocumentoBin);
			assinarDocumento(documentoBin, usuarioPerfil.getPerfilTemplate().getCodigo(),
					usuarioPerfil.getLocalizacao().getCodigo(), dadosAssinatura.getAssinatura());
		}
	}

	public void validarGrupo(String tokenGrupo, UsuarioLogin usuarioLogin) throws AssinaturaException {
		List<DadosAssinatura> dadosAssinaturas = groupService.getDadosAssinatura(tokenGrupo);
		for (DadosAssinatura dadosAssinatura : dadosAssinaturas) {
			validarAssinatura(dadosAssinatura, usuarioLogin);
		}
	}

	public void validarAssinatura(DadosAssinatura dadosAssinatura, UsuarioLogin usuarioLogin)
			throws AssinaturaException {
		if (dadosAssinatura.getStatus() != StatusToken.SUCESSO) {
			if (dadosAssinatura.getMensagemErro() != null) {
				throw new AssinaturaException(dadosAssinatura.getMensagemErro());
			} else {
				throw new AssinaturaException(
						"Status da assinatura inválido: " + dadosAssinatura.getStatus().toString());
			}
		}
		validarAssinatura(dadosAssinatura.getSignedData(), dadosAssinatura.getAssinatura(), usuarioLogin);
	}

	public boolean validarDadosAssinadosByText(List<DadosAssinatura> dadosAssinatura, List<String> textList) {
		return validarDadosAssinados(dadosAssinatura, new AssinavelGenericoProvider(textList));
	}

	public boolean validarDadosAssinadosByData(List<DadosAssinatura> dadosAssinatura, List<byte[]> dataList) {
		return validarDadosAssinados(dadosAssinatura, new AssinavelGenericoProvider(dataList.toArray(new byte[][] {})));
	}

	public boolean validarDadosAssinados(List<DadosAssinatura> dadosAssinatura, List<DocumentoBin> documentoBinList) {
		return validarDadosAssinados(dadosAssinatura, new AssinavelDocumentoBinProvider(documentoBinList));
	}

	/**
	 * Valida se os dados assinados foram os dados representados pelo
	 * assinavelProvider
	 */
	public boolean validarDadosAssinados(List<DadosAssinatura> dadosAssinatura, AssinavelProvider assinavelProvider) {
		List<AssinavelSource> assinaveis = assinavelProvider.getAssinaveis();
		if (dadosAssinatura.size() != assinaveis.size()) {
			return false;
		}
		Iterator<DadosAssinatura> itDadosAssinatura = dadosAssinatura.iterator();
		Iterator<AssinavelSource> itAssinaveis = assinaveis.iterator();

		while (itDadosAssinatura.hasNext()) {
			DadosAssinatura signed = itDadosAssinatura.next();
			AssinavelSource sourceToSign = itAssinaveis.next();

			byte[] dataToSign = sourceToSign.dataToSign(signed.getTipoSignedData());

			if (!Arrays.equals(dataToSign, signed.getSignedData())) {
				return false;
			}
		}

		return true;
	}

	public void validarAssinatura(byte[] signedData, byte[] signature, UsuarioLogin usuario)
			throws AssinaturaException {
		DadosAssinaturaLegada dadosAssinaturaLegada = getDadosAssinaturaLegada(signature);
		String certChainBase64 = dadosAssinaturaLegada.getCertChainBase64();
		try {
			assinaturaDocumentoService.verificaCertificadoUsuarioLogado(certChainBase64, usuario);
			validarCertificado(dadosAssinaturaLegada.getCertChain());
		} catch (CertificadoException e) {
			throw new AssinaturaException(e);
		}
	}
	
	public void validarAssinaturas(List<DadosAssinatura> dadosAssinaturaList, UsuarioLogin usuarioLogin) throws AssinaturaException {
		for(DadosAssinatura dadosAssinatura : dadosAssinaturaList) {
			validarAssinatura(dadosAssinatura, usuarioLogin);
		}
	}

	public void validarCertificado(List<X509Certificate> x509Certificates) throws CertificadoException {
		try {
			certificateValidator.validarCertificado(x509Certificates);
		} catch (CertPathValidatorException e) {
			Reason reason = e.getReason();
			if (reason == BasicReason.EXPIRED) {
				logger.log(Level.SEVERE, "Certificado expirado", e);
				if (ParametroUtil.isProducao()) {
					throw new CertificadoException("Certificado expirado. ");
				}
			} else if (reason == BasicReason.NOT_YET_VALID) {
				logger.log(Level.SEVERE, "Certificado ainda não válido", e);
				if (ParametroUtil.isProducao()) {
					throw new CertificadoException("O certificado ainda não está válido. ");
				}
			} else {
				logger.log(Level.SEVERE, "Erro ao verificar a validade do certificado", e);
				if (ParametroUtil.isProducao()) {
					throw new CertificadoException(e.getMessage());
				}
			}
		}
	}

	public void erroProcessamento(String token, UUID uuidAssinavel, String codigo, String mensagem) {
		groupService.erroProcessamento(token, uuidAssinavel, codigo, mensagem);
	}

	public void assinarDocumento(UUID uuidDocumento, byte[] signature) {
		UsuarioPerfil usuarioLogado = Authenticator.getUsuarioPerfilAtual();
		assinarDocumento(uuidDocumento, usuarioLogado.getPerfilTemplate().getCodigo(),
				usuarioLogado.getLocalizacao().getCodigo(), signature);
	}

	public void assinarDocumento(UUID uuidDocumento, String codigoPerfil, String codigoLocalizacao, byte[] signature) {
		DocumentoBin documentoBin = documentoBinManager.getByUUID(uuidDocumento);
		if (documentoBin == null) {
			throw new ValidationException("UUID inválido");
		}
		assinarDocumento(documentoBin, codigoPerfil, codigoLocalizacao, signature);
	}

	public void assinarDocumento(DocumentoBin documentoBin, String codigoPerfil, String codigoLocalizacao,
			byte[] signature) {
		DadosAssinaturaLegada dadosAssinaturaLegada = cmsAdapter.convert(signature);
		String certChainBase64 = dadosAssinaturaLegada.getCertChainBase64();
		String signatureBase64 = dadosAssinaturaLegada.getSignature();
		String cpf = getCpf(dadosAssinaturaLegada);
		UsuarioPerfil usuarioPerfil = getUsuarioPerfil(cpf, codigoPerfil, codigoLocalizacao);

		try {
			assinaturaDocumentoService.assinarDocumento(documentoBin, usuarioPerfil, certChainBase64, signatureBase64,
					TipoAssinatura.PKCS7);
		} catch (DAOException | CertificadoException | AssinaturaException e) {
			throw new RuntimeException("Erro ao assinar documento", e);
		}
	}

	public List<UUID> listarAssinaveis(String tokenGrupo) {
		return groupService.getAssinaveis(tokenGrupo);
	}

	public byte[] getSha256(String tokenGrupo, UUID uuidAssinavel) {
		return groupService.getSha256(tokenGrupo, uuidAssinavel);
	}

	public StatusToken getStatus(String token) {
		return groupService.getStatus(token);
	}

	public void validarNovoToken(String token) {
		groupService.validarNovoToken(token);
	}

	public void validarToken(String token) {
		groupService.validarToken(token);
	}

}
