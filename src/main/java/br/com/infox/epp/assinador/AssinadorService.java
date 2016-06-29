package br.com.infox.epp.assinador;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.validation.ValidationException;

import org.apache.commons.codec.binary.Base64;

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
import br.com.infox.epp.assinador.AssinadorGroupService.DadosAssinatura;
import br.com.infox.epp.assinador.AssinadorGroupService.StatusToken;
import br.com.infox.epp.assinador.assinavel.AssinavelProvider;
import br.com.infox.epp.certificado.entity.TipoAssinatura;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumentoService;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaException;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;

public class AssinadorService implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public static final String NOME_CACHE_DOCUMENTOS="AssinadorService.listaDocumentos";
	
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
		if(perfilTemplate == null) {
			throw new ValidationException("Perfil não encontrado");
		}
		Localizacao localizacao = localizacaoManager.getLocalizacaoByCodigo(codigoLocalizacao);
		if(localizacao == null) {
			throw new ValidationException("Localização não encontrada");
		}
		UsuarioLogin usuarioLogin = usuarioLoginManager.getUsuarioFetchPessoaFisicaByNrCpf(cpf);
		if(usuarioLogin == null) {
			throw new ValidationException("Nenhum usuário encontrado no sistema correspondente ao CPF do certificado");
		}
		
		UsuarioPerfil retorno = usuarioPerfilManager.getByUsuarioLoginPerfilTemplateLocalizacao(usuarioLogin, perfilTemplate, localizacao);
		if(retorno == null) {
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
		for(DadosAssinatura dadosAssinatura : dadosAssinaturas) {
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
		if(idDocumentoBin != null) {
			DocumentoBin documentoBin = documentoBinManager.find(idDocumentoBin);
			assinarDocumento(documentoBin, usuarioPerfil.getPerfilTemplate().getCodigo(), usuarioPerfil.getLocalizacao().getCodigo(), dadosAssinatura.getAssinatura());
		}
	}
	
	public void validarGrupo(String tokenGrupo, UsuarioLogin usuarioLogin) throws AssinaturaException {
		List<DadosAssinatura> dadosAssinaturas = groupService.getDadosAssinatura(tokenGrupo);
		for(DadosAssinatura dadosAssinatura : dadosAssinaturas) {
			validarAssinatura(dadosAssinatura, usuarioLogin);
		}
	}
	
    protected void validarAssinatura(DadosAssinatura dadosAssinatura, UsuarioLogin usuarioLogin) throws AssinaturaException {
    	if(dadosAssinatura.getStatus() != StatusToken.SUCESSO) {
    		if(dadosAssinatura.getMensagemErro() != null) {
    			throw new AssinaturaException(dadosAssinatura.getMensagemErro());
    		}
    		else {
    			throw new AssinaturaException("Status da assinatura inválido: " + dadosAssinatura.getStatus().toString());
    		}
    	}
    	validarAssinatura(dadosAssinatura.getCertChain(), usuarioLogin, dadosAssinatura.getAssinatura());
    }
	
    public void validarAssinatura(byte[] certChain, UsuarioLogin usuario, byte[] signature) throws AssinaturaException {
    	String certChainBase64 = Base64.encodeBase64String(certChain);
    	try {
			assinaturaDocumentoService.validarCertificado(certChainBase64, usuario);
		} catch (CertificadoException e) {
			throw new AssinaturaException(e);
		}
    }
    
	public void erroProcessamento(String token, UUID uuidAssinavel, String codigo, String mensagem) {
		groupService.erroProcessamento(token, uuidAssinavel, codigo, mensagem);
	}
	
	public void assinarDocumento(UUID uuidDocumento, byte[] signature) {
		UsuarioPerfil usuarioLogado = Authenticator.getUsuarioPerfilAtual();
		assinarDocumento(uuidDocumento, usuarioLogado.getPerfilTemplate().getCodigo(), usuarioLogado.getLocalizacao().getCodigo(), signature);
	}
	
	public void assinarDocumento(UUID uuidDocumento, String codigoPerfil, String codigoLocalizacao, byte[] signature) {
		DocumentoBin documentoBin = documentoBinManager.getByUUID(uuidDocumento);
		if(documentoBin == null) {
			throw new ValidationException("UUID inválido");
		}
		assinarDocumento(documentoBin, codigoPerfil, codigoLocalizacao, signature);
	}
	
	public void assinarDocumento(DocumentoBin documentoBin, String codigoPerfil, String codigoLocalizacao, byte[] signature) {
		DadosAssinaturaLegada dadosAssinaturaLegada = cmsAdapter.convert(signature);
		String certChainBase64 = dadosAssinaturaLegada.getCertChainBase64();
		String signatureBase64 = dadosAssinaturaLegada.getSignature();
		String cpf = getCpf(dadosAssinaturaLegada);
		UsuarioPerfil usuarioPerfil = getUsuarioPerfil(cpf, codigoPerfil, codigoLocalizacao);
		
		try {
			assinaturaDocumentoService.assinarDocumento(documentoBin, usuarioPerfil, certChainBase64, signatureBase64, TipoAssinatura.PKCS7);
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
	
}
