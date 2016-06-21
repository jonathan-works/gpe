package br.com.infox.epp.assinador;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.validation.ValidationException;

import br.com.infox.certificado.CertificadoGenerico;
import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.access.manager.LocalizacaoManager;
import br.com.infox.epp.access.manager.PerfilTemplateManager;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.access.manager.UsuarioPerfilManager;
import br.com.infox.epp.certificado.entity.CertificateSignatureGroup;
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

	public CertificateSignatureGroup criarListaDocumentos(List<DocumentoBin> listaDocumentos) {
		return groupService.createNewGroup(listaDocumentos);
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
	
	
	public void assinarDocumento(UUID uuidDocumento, String codigoPerfil, String codigoLocalizacao, byte[] signature) {
		DocumentoBin documentoBin = documentoBinManager.getByUUID(uuidDocumento);
		if(documentoBin == null) {
			throw new ValidationException("UUID inválido");
		}
		
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
	
	
}
