package br.com.infox.epp.processo.documento.assinatura;

import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.util.Strings;

import br.com.infox.certificado.Certificado;
import br.com.infox.certificado.CertificadoDadosPessoaFisica;
import br.com.infox.certificado.CertificadoFactory;
import br.com.infox.certificado.ValidaDocumento;
import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.core.manager.GenericManager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.access.manager.CertificateManager;
import br.com.infox.epp.certificado.entity.TipoAssinatura;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ClassificacaoDocumentoPapel;
import br.com.infox.epp.documento.manager.ClassificacaoDocumentoPapelManager;
import br.com.infox.epp.documento.type.TipoAssinaturaEnum;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.manager.PessoaFisicaManager;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaException.Motivo;
import br.com.infox.epp.processo.documento.assinatura.entity.RegistroAssinaturaSuficiente;
import br.com.infox.epp.processo.documento.dao.AssinaturaDocumentoDAO;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoBinarioManager;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.system.util.ParametroUtil;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.util.ComponentUtil;

@Stateless
@AutoCreate
@Scope(ScopeType.STATELESS)
@Name(AssinaturaDocumentoService.NAME)
public class AssinaturaDocumentoService {

    private static final LogProvider LOG = Logging.getLogProvider(AssinaturaDocumentoService.class);
    public static final String NAME = "assinaturaDocumentoService";

    @Inject
    private DocumentoManager documentoManager;
    @Inject
    private DocumentoBinarioManager documentoBinarioManager;
    @Inject
    private DocumentoBinManager documentoBinManager;
    @Inject
    private ClassificacaoDocumentoPapelManager classificacaoDocumentoPapelManager;
    @In
    private AssinaturaDocumentoListenerService assinaturaDocumentoListenerService;
    @Inject
    private PessoaFisicaManager pessoaFisicaManager;
    @Inject
    private AssinaturaDocumentoDAO assinaturaDocumentoDAO;

    public Boolean isDocumentoAssinado(final Documento documento) {
        final DocumentoBin documentoBin = documento.getDocumentoBin();
        return documentoBin != null
                && isSignedAndValid(documentoBin.getAssinaturas());
    }

    private boolean isSignatureValid(AssinaturaDocumento assinatura) {
        boolean result = false;
        try {
            verificaCertificadoUsuarioLogado(assinatura.getCertChain(),
                    assinatura.getUsuario());
            result = true;
        } catch (CertificadoException | AssinaturaException e) {
            LOG.error(e.getMessage(), e);
        }
        return result;
    }

    private boolean isSignedAndValid(final List<AssinaturaDocumento> assinaturas) {
        boolean result = false;
        for (AssinaturaDocumento assinaturaDocumento : assinaturas) {
            if (!(result = isSignatureValid(assinaturaDocumento))) {
                break;
            }
        }
        return result;
    }

    public boolean isDocumentoTotalmenteAssinado(Integer idDoc) {
        Documento documento = documentoManager.find(idDoc);
        return isDocumentoTotalmenteAssinado(documento);
    }

    public boolean isDocumentoTotalmenteAssinado(Documento documento) {
        if (documento.getDocumentoBin().isMinuta()) {
            return false;
        }
        List<ClassificacaoDocumentoPapel> classificacaoDocumentoPapeis = documento.getClassificacaoDocumento().getClassificacaoDocumentoPapelList();
        Map<TipoAssinaturaEnum, List<Boolean>> mapAssinaturas = new HashMap<>();
        for (TipoAssinaturaEnum tipoAssinatura : TipoAssinaturaEnum.values()) {
            mapAssinaturas.put(tipoAssinatura, new ArrayList<Boolean>());
        }
        for (ClassificacaoDocumentoPapel tipoProcessoDocumentoPapel : classificacaoDocumentoPapeis) {
            TipoAssinaturaEnum tipoAssinatura = tipoProcessoDocumentoPapel.getTipoAssinatura();
            Papel papel = tipoProcessoDocumentoPapel.getPapel();
            List<Boolean> assinaturas = mapAssinaturas.get(tipoAssinatura);
            assinaturas.add(isDocumentoAssinado(documento.getDocumentoBin(), papel));
        }
        return isDocumentoTotalmenteAssinado(mapAssinaturas);
    }
    
    public boolean isDocumentoTotalmenteAssinado(DocumentoBin documentoBin, ClassificacaoDocumento classificacaoDocumento) {
        List<ClassificacaoDocumentoPapel> classificacaoDocumentoPapeis = classificacaoDocumento.getClassificacaoDocumentoPapelList();
        Map<TipoAssinaturaEnum, List<Boolean>> mapAssinaturas = new HashMap<>();
        for (TipoAssinaturaEnum tipoAssinatura : TipoAssinaturaEnum.values()) {
            mapAssinaturas.put(tipoAssinatura, new ArrayList<Boolean>());
        }
        for (ClassificacaoDocumentoPapel tipoProcessoDocumentoPapel : classificacaoDocumentoPapeis) {
            TipoAssinaturaEnum tipoAssinatura = tipoProcessoDocumentoPapel.getTipoAssinatura();
            Papel papel = tipoProcessoDocumentoPapel.getPapel();
            List<Boolean> assinaturas = mapAssinaturas.get(tipoAssinatura);
            assinaturas.add(isDocumentoAssinado(documentoBin, papel));
        }
        return isDocumentoTotalmenteAssinado(mapAssinaturas);
    }
    
    public boolean isDocumentoTotalmenteAssinado(DocumentoBin documento){
        if (documento.isMinuta()) {
            return false;
        }
    	List<Documento> documentos = documentoManager.getDocumentosFromDocumentoBin(documento);
    	if (!documentos.isEmpty()){
    	    return isDocumentoTotalmenteAssinado(documentos.get(0));
    	}
    	return false;
    }
    
    boolean isDocumentoTotalmenteAssinado(Map<TipoAssinaturaEnum, List<Boolean>> mapAssinaturas) {
        List<Boolean> obrigatorias = mapAssinaturas.get(TipoAssinaturaEnum.O);
        List<Boolean> suficientes = mapAssinaturas.get(TipoAssinaturaEnum.S);
        
        return (obrigatorias.isEmpty() && suficientes.isEmpty())
                || (!obrigatorias.isEmpty() && areAllTrue(obrigatorias))
                || (!suficientes.isEmpty() && isOneTrue(suficientes));
    }

    private boolean isOneTrue(List<Boolean> booleans){
        boolean result = false;
        for (boolean b : booleans) {
            result = b || result;
            if (result){
                break;
            }
        }
        return result;
    }
    
    private boolean areAllTrue(List<Boolean> booleans){
        boolean result = true;
        for (boolean b : booleans) {
            result = b && result;
            if (!result){
                break;
            }
        }
        return result;
    }

    public boolean isDocumentoAssinado(Documento documento, UsuarioPerfil usuarioLocalizacao) {
        boolean result = false;
        for (AssinaturaDocumento assinaturaDocumento : documento.getDocumentoBin().getAssinaturas()) {
            Papel papel = usuarioLocalizacao.getPerfilTemplate().getPapel();
            UsuarioLogin usuario = usuarioLocalizacao.getUsuarioLogin();
            if (result = (assinaturaDocumento.getUsuarioPerfil().getPerfilTemplate().getPapel().equals(papel) && assinaturaDocumento.getUsuario().equals(usuario))) {
                break;
            }
        }
        return result;
    }

    public boolean isDocumentoAssinado(Documento documento, UsuarioLogin usuarioLogin) {
    	return isDocumentoAssinado(documento.getDocumentoBin(), usuarioLogin);
    }
    
    public boolean isDocumentoAssinado(DocumentoBin documentoBin, UsuarioLogin usuarioLogin) {
    	if (documentoBin == null) {
    		return false;
    	}
    	boolean result = false;
        for (AssinaturaDocumento assinaturaDocumento : documentoBin.getAssinaturas()) {
            if (assinaturaDocumento.getUsuario().equals(usuarioLogin)) {
                result = isSignatureValid(assinaturaDocumento);
                break;
            }
        }
        return result;
    }

    public boolean isDocumentoAssinado(DocumentoBin documentoBin, Papel papel) {
        boolean result = false;
        List<AssinaturaDocumento> assinaturas = assinaturaDocumentoDAO.listAssinaturaByDocumentoBin(documentoBin);
        for (AssinaturaDocumento assinaturaDocumento : assinaturas) {
            if (assinaturaDocumento.getUsuarioPerfil().getPerfilTemplate().getPapel().equals(papel)) {
                result = isSignatureValid(assinaturaDocumento);
                break;
            }
        }
        return result;
    }

    public void verificaCertificadoUsuarioLogado(String certChainBase64Encoded,
            UsuarioLogin usuarioLogado) throws CertificadoException, AssinaturaException {
        if (Strings.isEmpty(certChainBase64Encoded)) {
            throw new AssinaturaException(Motivo.SEM_CERTIFICADO);
        }
        if (usuarioLogado.getPessoaFisica() == null) {
            throw new AssinaturaException(Motivo.USUARIO_SEM_PESSOA_FISICA);
        }
        if (Strings.isEmpty(usuarioLogado.getPessoaFisica().getCertChain())) {
            final Certificado certificado = CertificadoFactory.createCertificado(certChainBase64Encoded); 
            if (!(certificado instanceof CertificadoDadosPessoaFisica)) {
                throw new CertificadoException("Este certificado não é de pessoa física");
            }
            final String cpfCertificado = ((CertificadoDadosPessoaFisica) certificado).getCPF();
            if (cpfCertificado.equals(usuarioLogado.getPessoaFisica().getCpf()
                    .replace(".", "").replace("-", ""))) {
                usuarioLogado.getPessoaFisica().setCertChain(certChainBase64Encoded);
            } else {
                throw new AssinaturaException(Motivo.CADASTRO_USUARIO_NAO_ASSINADO);
            }
        }
        PessoaFisica pessoaFisicaCertificado = getPessoaFisicaFromCertChain(certChainBase64Encoded);
        if (!usuarioLogado.getPessoaFisica().equals(pessoaFisicaCertificado)) {
            throw new AssinaturaException(Motivo.CPF_CERTIFICADO_DIFERENTE_USUARIO);
        }
        if (!pessoaFisicaCertificado.checkCertChain(certChainBase64Encoded)) {
            throw new AssinaturaException(Motivo.CERTIFICADO_USUARIO_DIFERENTE_CADASTRO);
        }
    }
    
    private PessoaFisica getPessoaFisicaFromCertChain(String certChain) throws CertificadoException {
        Certificado c = CertificadoFactory.createCertificado(certChain);
        String cpf = new StringBuilder(((CertificadoDadosPessoaFisica) c).getCPF()).insert(9, '-').insert(6, '.').insert(3, '.').toString();
        if (cpf != null) {
            return pessoaFisicaManager.getByCpf(cpf);
        }
        return null;
    }

	public void assinarDocumento(DocumentoBin documentoBin, UsuarioPerfil usuarioPerfilAtual, final String certChain,
			String signature, TipoAssinatura tipoAssinatura) throws CertificadoException, AssinaturaException, DAOException {
		UsuarioLogin usuario = usuarioPerfilAtual.getUsuarioLogin();
		verificaCertificadoUsuarioLogado(certChain, usuario);
		checkValidadeCertificado(certChain);

		AssinaturaDocumento assinaturaDocumento = new AssinaturaDocumento(documentoBin, usuarioPerfilAtual, certChain, signature, tipoAssinatura);
		List<Documento> documentosNaoSuficientementeAssinados = documentoBinManager.getDocumentosNaoSuficientementeAssinados(documentoBin);
		documentoBin.getAssinaturas().add(assinaturaDocumento);
		assinaturaDocumentoDAO.persist(assinaturaDocumento);
		
		if (documentoBin.isMinuta()){
			documentoBin.setMinuta(Boolean.FALSE);
		}
		if (isDocumentoTotalmenteAssinado(documentoBin)){
			documentoBinManager.setDocumentoSuficientementeAssinado(documentoBin, usuarioPerfilAtual);
		}
		documentoBinManager.update(documentoBin);
		try {
			for (Documento documento : documentosNaoSuficientementeAssinados) {
				if (isDocumentoTotalmenteAssinado(documento)) {
					assinaturaDocumentoListenerService.dispatch(documento);
				}
			}
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

    public void assinarDocumento(final Documento documento,
            final UsuarioPerfil perfilAtual, final String certChain,
            final String signature) throws CertificadoException,
            AssinaturaException, DAOException {
        assinarDocumento(documento.getDocumentoBin(), perfilAtual, certChain, signature, TipoAssinatura.MD5_ASSINADO);
    }
    
    public void assinarGravarDocumento(Documento documento,
            final UsuarioPerfil perfilAtual, final String certChain,
            final String signature) throws DAOException, CertificadoException, AssinaturaException {
    	documento = documentoManager.gravarDocumentoNoProcesso(documento);
    	assinarDocumento(documento.getDocumentoBin(), perfilAtual, certChain, signature, TipoAssinatura.MD5_ASSINADO);
    }

    public boolean isDocumentoAssinado(Integer idDocumento, PerfilTemplate perfilTemplate) {
        Documento documento = documentoManager.find(idDocumento);
        return documento != null && isDocumentoAssinado(documento.getDocumentoBin(), perfilTemplate.getPapel());
    }

    public boolean isDocumentoAssinado(Integer idDocumento, UsuarioPerfil perfil) {
        Documento documento = documentoManager.find(idDocumento);
        return documento != null && isDocumentoAssinado(documento, perfil);
    }

    public ValidaDocumento validaDocumento(DocumentoBin bin,
            String certChain, String signature) throws CertificadoException {
        byte[] data = null;
        if (!bin.isBinario()) {
            data = ValidaDocumento.removeBR(bin.getModeloDocumento()).getBytes();
        } else {
            try {
                data = documentoBinarioManager.getData(bin.getId());
            } catch (Exception e) {
                throw new IllegalArgumentException("Erro ao obter os dados do binário", e);
            }
        }
        if (data == null) {
            throw new IllegalArgumentException("Documento inválido");
        }
        return new ValidaDocumento(data, certChain, signature);
    }

    public Documento validaDocumentoId(Integer idDocumento) {
        if (idDocumento == null) {
            throw new IllegalArgumentException("Id do documento não informado");
        }
        Documento documento = documentoManager.find(idDocumento);
        if (documento == null) {
            throw new IllegalArgumentException("Documento não encontrado.");
        }

        return documento;
    }

    public boolean isDocumentoAssinado(Integer idDocumento, UsuarioLogin usuarioLogin) {
        Documento documento = documentoManager.find(idDocumento);
        return documento != null && isDocumentoAssinado(documento, usuarioLogin);
    }
    
    public boolean isDocumentoAssinado(DocumentoBin documentoBin, Papel papel, UsuarioLogin usuario) {
        boolean result = false;
        for (AssinaturaDocumento assinaturaDocumento : documentoBin.getAssinaturas()) {
            if (assinaturaDocumento.getUsuarioPerfil().getPerfilTemplate().getPapel().equals(papel) && 
            		assinaturaDocumento.getUsuarioPerfil().getUsuarioLogin().equals(usuario)) {
                result = isSignatureValid(assinaturaDocumento);
                break;
            }
        }
        return result;
    }
    
    public boolean podeRenderizarApplet(Papel papel, ClassificacaoDocumento classificacao, Integer idDocumento, UsuarioLogin usuario) {
    	Documento documento = documentoManager.find(idDocumento);
    	if (documento == null) {
    		return false;
    	}
    	return podeRenderizarApplet(papel, classificacao, documento.getDocumentoBin(), usuario);
    }
    
    public boolean podeRenderizarApplet(Papel papel, ClassificacaoDocumento classificacao, DocumentoBin documentoBin, UsuarioLogin usuario) {
    	if (documentoBin == null || (documentoBin != null && documentoBin.isMinuta())) {
    		return false;
    	}
    	return classificacaoDocumentoPapelManager.papelPodeAssinarClassificacao(papel, classificacao) && 
    			!isDocumentoAssinado(documentoBin, papel, usuario);
    }
    
    public boolean precisaAssinatura(ClassificacaoDocumento classificacaoDocumento){
    	if(classificacaoDocumento != null){
	    	List<ClassificacaoDocumentoPapel> classificacaoDocumentoPapelList = classificacaoDocumento.getClassificacaoDocumentoPapelList();
	    	for (ClassificacaoDocumentoPapel cdp : classificacaoDocumentoPapelList) {
				if (cdp.getTipoAssinatura().equals(TipoAssinaturaEnum.O)
					|| cdp.getTipoAssinatura().equals(TipoAssinaturaEnum.S)){
					return true;
				}
	    	}
	    	return false;
    	}
    	return true;
    }
    
    private void checkValidadeCertificado(String certChain) throws CertificadoException {
        try {
            CertificateManager.instance().verificaCertificado(certChain);
        } catch (CertificateExpiredException e) {
        	LOG.error("Certificado expirado", e);
            if (ParametroUtil.isProducao()) {
    			throw new CertificadoException("Certificado expirado. ");
            }
        } catch (CertificateNotYetValidException e) {
        	LOG.error("Certificado ainda não válido", e);
            if (ParametroUtil.isProducao()) {
    			throw new CertificadoException("O certificado ainda não está válido. ");
            }
		} catch (CertificateException e) {
            LOG.error("Erro ao verificar a validade do certificado", e);
            if (ParametroUtil.isProducao()) {
    			throw new CertificadoException(e.getMessage());
            }
        }
    }
}
