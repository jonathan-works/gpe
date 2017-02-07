package br.com.infox.epp.assinador;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.validation.ValidationException;

import br.com.infox.certificado.CertificadoGenerico;
import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.access.manager.LocalizacaoManager;
import br.com.infox.epp.access.manager.PerfilTemplateManager;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.access.manager.UsuarioPerfilManager;
import br.com.infox.assinador.rest.api.StatusToken;
import br.com.infox.epp.assinador.assinavel.AssinavelDocumentoBinProvider;
import br.com.infox.epp.assinador.assinavel.AssinavelGenericoProvider;
import br.com.infox.epp.assinador.assinavel.AssinavelProvider;
import br.com.infox.epp.assinador.assinavel.AssinavelSource;
import br.com.infox.epp.assinador.assinavel.TipoSignedData;
import br.com.infox.epp.certificado.entity.TipoAssinatura;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumentoService;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaException;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;

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
	private InfoxMessages infoxMessages;

	
	
	@Inject
	private ValidadorAssinatura validadorAssinatura;

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

	public List<DadosAssinatura> getDadosAssinatura(String tokenGrupo) throws AssinaturaException {
		List<DadosAssinatura> retorno = groupService.getDadosAssinatura(tokenGrupo);
		
		for(DadosAssinatura dados : retorno) {
			validarStatus(dados);			
		}
		
		return retorno;
	}

	public void assinar(String tokenGrupo, UsuarioPerfil usuarioPerfil) throws AssinaturaException {
		List<DadosAssinatura> dadosAssinaturas = groupService.getDadosAssinatura(tokenGrupo);
		assinar(dadosAssinaturas, usuarioPerfil);
	}

        public void assinar(List<DadosAssinatura> dadosAssinaturas, UsuarioPerfil usuarioPerfil) throws AssinaturaException {
            for (DadosAssinatura dadosAssinatura : dadosAssinaturas) {
                assinar(dadosAssinatura, usuarioPerfil);
            }
        }

	public void assinar(String tokenGrupo, UUID uuidAssinavel, UsuarioPerfil usuarioPerfil) throws AssinaturaException {
		DadosAssinatura dadosAssinatura = groupService.getDadosAssinatura(tokenGrupo, uuidAssinavel);
		assinar(dadosAssinatura, usuarioPerfil);
	}

	public void assinar(DadosAssinatura dadosAssinatura, UsuarioPerfil usuarioPerfil) throws AssinaturaException {
		UUID uuidDocumentoBin = dadosAssinatura.getUuidDocumentoBin();
		if (uuidDocumentoBin != null) {
			DocumentoBin documentoBin = documentoBinManager.getByUUID(uuidDocumentoBin);
			if(documentoBin == null) {
				throw new RuntimeException("Documento com UUID " + uuidDocumentoBin + " não encontrado no banco de dados");
			}
			
			try {
	                        assinaturaDocumentoService.assinarDocumento(documentoBin, usuarioPerfil, dadosAssinatura.getCertChainBase64(), dadosAssinatura.getAssinaturaBase64(),
	                                        TipoAssinatura.PKCS7, dadosAssinatura.getSignedData(), dadosAssinatura.getTipoSignedData());
	                } catch (DAOException | CertificadoException | AssinaturaException e) {
	                        throw new RuntimeException("Erro ao assinar documento", e);
	                }
		}
		else {
			validarAssinatura(dadosAssinatura, usuarioPerfil.getUsuarioLogin().getPessoaFisica());			
		}
	}

	public void validarGrupo(String tokenGrupo, PessoaFisica pessoaFisica) throws AssinaturaException {
		List<DadosAssinatura> dadosAssinaturas = groupService.getDadosAssinatura(tokenGrupo);
		for (DadosAssinatura dadosAssinatura : dadosAssinaturas) {
			validarAssinatura(dadosAssinatura, pessoaFisica);
		}
	}

	private void validarStatus(DadosAssinatura dadosAssinatura) throws AssinaturaException {
		if (!StatusToken.SUCESSO.equals(dadosAssinatura.getStatus())) {
			if (dadosAssinatura.getMensagemErro() != null) {
				throw new AssinaturaException(dadosAssinatura.getMensagemErro());
			} 
			else if (StatusToken.EXPIRADO.equals(dadosAssinatura.getStatus())) {
		        throw new AssinaturaException(infoxMessages.get("assinatura.error.hasExpired"));
		    }
			else {
				throw new AssinaturaException(
						"Status da assinatura inválido: " + dadosAssinatura.getStatus().toString());
			}
		}		
	}
	
	public void validarAssinatura(DadosAssinatura dadosAssinatura, PessoaFisica pessoaFisica)
			throws AssinaturaException {
		validarStatus(dadosAssinatura);
		validadorAssinatura.validarAssinatura(dadosAssinatura.getSignedData(), dadosAssinatura.getTipoSignedData(), dadosAssinatura.getAssinatura(), pessoaFisica);
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

	public void validarAssinaturas(List<DadosAssinatura> dadosAssinaturaList, PessoaFisica pessoaFisica) throws AssinaturaException {
		for(DadosAssinatura dadosAssinatura : dadosAssinaturaList) {
			validarAssinatura(dadosAssinatura, pessoaFisica);
		}
	}

	public void erroProcessamento(String token, UUID uuidAssinavel, String codigo, String mensagem) {
		groupService.erroProcessamento(token, uuidAssinavel, codigo, mensagem);
	}
	/**
	 * @deprecated Método não funciona com a assinatura em modo homologação 
	 */
	@Deprecated
	public void assinarDocumento(DocumentoBin documentoBin, String codigoPerfil, String codigoLocalizacao,
			byte[] signature, byte[] signedData, TipoSignedData tipoSignedData) {
		DadosAssinaturaLegada dadosAssinaturaLegada = cmsAdapter.convert(signature);
		String certChainBase64 = dadosAssinaturaLegada.getCertChainBase64();
		String signatureBase64 = dadosAssinaturaLegada.getSignature();
		String cpf = getCpf(dadosAssinaturaLegada);
		UsuarioPerfil usuarioPerfil = getUsuarioPerfil(cpf, codigoPerfil, codigoLocalizacao);

		try {
			assinaturaDocumentoService.assinarDocumento(documentoBin, usuarioPerfil, certChainBase64, signatureBase64,
					TipoAssinatura.PKCS7, signedData, tipoSignedData);
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
	
	/**
	 * Valida e assina todos os assináveis agrupados por um token além de apagar o grupo depois de assiná-lo
	 * @param tokenGrupo
	 */
	public void assinarToken(String tokenGrupo, UsuarioPerfil usuarioPerfil) throws AssinaturaException {
		List<DadosAssinatura> dadosAssinaturaList = getDadosAssinatura(tokenGrupo);
		assinar(dadosAssinaturaList, usuarioPerfil);
		apagarGrupo(tokenGrupo);
	}

}
