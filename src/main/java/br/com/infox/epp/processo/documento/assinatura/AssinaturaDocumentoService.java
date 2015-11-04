package br.com.infox.epp.processo.documento.assinatura;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
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
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ClassificacaoDocumentoPapel;
import br.com.infox.epp.documento.manager.ClassificacaoDocumentoPapelManager;
import br.com.infox.epp.documento.type.TipoAssinaturaEnum;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.manager.PessoaFisicaManager;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaException.Motivo;
import br.com.infox.epp.processo.documento.assinatura.entity.RegistroAssinaturaSuficiente;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.AssinaturaDocumentoManager;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoBinarioManager;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.util.ComponentUtil;

@AutoCreate
@Scope(ScopeType.STATELESS)
@Name(AssinaturaDocumentoService.NAME)
@Transactional
@Stateless
public class AssinaturaDocumentoService {

    private static final LogProvider LOG = Logging.getLogProvider(AssinaturaDocumentoService.class);
    public static final String NAME = "assinaturaDocumentoService";

    @In
    private DocumentoManager documentoManager;
    @In
    private DocumentoBinarioManager documentoBinarioManager;
    @In
    private AssinaturaDocumentoManager assinaturaDocumentoManager;
    @In
    private DocumentoBinManager documentoBinManager;
    @In
    private ClassificacaoDocumentoPapelManager classificacaoDocumentoPapelManager;
    @In
    private AssinaturaDocumentoListenerService assinaturaDocumentoListenerService;
    @In
    private PessoaFisicaManager pessoaFisicaManager;
    @In
    private UsuarioLoginManager usuarioLoginManager;

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
        List<ClassificacaoDocumentoPapel> classificacaoDocumentoPapeis = documento.getClassificacaoDocumento()
                .getClassificacaoDocumentoPapelList();
        
        Map<TipoAssinaturaEnum, List<Boolean>> mapAssinaturas = new HashMap<>();
        for (TipoAssinaturaEnum tipoAssinatura : TipoAssinaturaEnum.values()) {
            mapAssinaturas.put(tipoAssinatura, new ArrayList<Boolean>());
        }
        
        for (ClassificacaoDocumentoPapel tipoProcessoDocumentoPapel : classificacaoDocumentoPapeis) {
            final TipoAssinaturaEnum tipoAssinatura = tipoProcessoDocumentoPapel.getTipoAssinatura();
            
            final Papel papel = tipoProcessoDocumentoPapel.getPapel();
            final List<Boolean> assinaturas = mapAssinaturas.get(tipoAssinatura);
            assinaturas.add(isDocumentoAssinado(documento, papel));
        }
        
        return isDocumentoTotalmenteAssinado(mapAssinaturas);
    }
    
    public boolean isDocumentoTotalmenteAssinado(DocumentoBin documento){
        if (documento.isMinuta()) {
            return false;
        }
    	List<Documento> documentos = documentoManager.getDocumentosFromDocumentoBin(documento);
    	if (documentos.size() > 0){
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
            if (result = (assinaturaDocumento.getUsuarioPerfil().getPerfilTemplate().getPapel()
                    .equals(papel) || assinaturaDocumento.getUsuario().equals(
                    usuario))
                    && isSignatureValid(assinaturaDocumento)) {
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

    public boolean isDocumentoAssinado(Documento documento, Papel papel) {
        boolean result = false;
        for (AssinaturaDocumento assinaturaDocumento : documento.getDocumentoBin().getAssinaturas()) {
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
        UsuarioLogin usuarioCertificado = getUsuarioLoginFromCertChain(certChainBase64Encoded);
        if (!usuarioLogado.getPessoaFisica().getCpf().equals(usuarioCertificado.getPessoaFisica().getCpf())) {
            throw new AssinaturaException(Motivo.CPF_CERTIFICADO_DIFERENTE_USUARIO);
        }
        if (!usuarioLogado.getPessoaFisica().checkCertChain(certChainBase64Encoded)) {
            throw new AssinaturaException(Motivo.CERTIFICADO_USUARIO_DIFERENTE_CADASTRO);
        }
    }
    
    private UsuarioLogin getUsuarioLoginFromCertChain(String certChain) throws CertificadoException {
        Certificado c = CertificadoFactory.createCertificado(certChain);
        String cpf = new StringBuilder(((CertificadoDadosPessoaFisica) c).getCPF()).insert(9, '-').insert(6, '.').insert(3, '.').toString();
        if (cpf != null) {
            PessoaFisica pessoaFisica = pessoaFisicaManager.getByCpf(cpf);
            if (pessoaFisica != null) {
                return usuarioLoginManager.getUsuarioLoginByPessoaFisica(pessoaFisica);
            }
        }
        return null;
    }

	public void assinarDocumento(final DocumentoBin documentoBin,
			final UsuarioPerfil usuarioPerfilAtual, final String certChain,
			final String signature) throws CertificadoException,
			AssinaturaException, DAOException {
		final UsuarioLogin usuario = usuarioPerfilAtual.getUsuarioLogin();
		verificaCertificadoUsuarioLogado(certChain, usuario);

		final AssinaturaDocumento assinaturaDocumento = new AssinaturaDocumento(documentoBin, usuarioPerfilAtual, certChain, signature);
		List<Documento> documentosNaoSuficientementeAssinados = documentoBinManager.getDocumentosNaoSuficientementeAssinados(documentoBin);
		documentoBin.getAssinaturas().add(assinaturaDocumento);
		
		if (documentoBin.isMinuta()){
			documentoBin.setMinuta(Boolean.FALSE);
		}
		if (isDocumentoTotalmenteAssinado(documentoBin)){
			setDocumentoSuficientementeAssinado(documentoBin, usuarioPerfilAtual);
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

	public void setDocumentoSuficientementeAssinado(final DocumentoBin documentoBin,
			final UsuarioPerfil usuarioPerfilAtual) throws DAOException {
		documentoBin.setSuficientementeAssinado(Boolean.TRUE);
		documentoBin.setDataSuficientementeAssinado(new Date());
		List<RegistroAssinaturaSuficiente> registrosAssinaturaSuficiente = documentoBin.getRegistrosAssinaturaSuficiente();
		List<Documento> documentoList = documentoManager.getDocumentosFromDocumentoBin(documentoBin);
		GenericManager genericManager = ComponentUtil.getComponent(GenericManager.NAME);
        if (!(documentoList == null || documentoList.isEmpty()) && usuarioPerfilAtual != null) {
            Documento documento = documentoList.get(0);
            for (ClassificacaoDocumentoPapel classificacaoDocumentoPapel : documento.getClassificacaoDocumento().getClassificacaoDocumentoPapelList()) {
                RegistroAssinaturaSuficiente registroAssinaturaSuficiente = new RegistroAssinaturaSuficiente();
                registroAssinaturaSuficiente.setDocumentoBin(documentoBin);
                registroAssinaturaSuficiente.setPapel(usuarioPerfilAtual.getPerfilTemplate().getPapel().getNome());
                registroAssinaturaSuficiente.setTipoAssinatura(classificacaoDocumentoPapel.getTipoAssinatura());
                registrosAssinaturaSuficiente.add(registroAssinaturaSuficiente);
                genericManager.persist(registroAssinaturaSuficiente);
            }
        }
        documentoBinManager.update(documentoBin);
	}
    
    public void assinarDocumento(final Documento documento,
            final UsuarioPerfil perfilAtual, final String certChain,
            final String signature) throws CertificadoException,
            AssinaturaException, DAOException {
        assinarDocumento(documento.getDocumentoBin(), perfilAtual, certChain, signature);
    }

    public boolean isDocumentoAssinado(Integer idDocumento, PerfilTemplate perfilTemplate) {
        Documento documento = documentoManager.find(idDocumento);
        return documento != null && isDocumentoAssinado(documento, perfilTemplate.getPapel());
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
    			!isDocumentoAssinado(documentoBin, usuario);
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
}
