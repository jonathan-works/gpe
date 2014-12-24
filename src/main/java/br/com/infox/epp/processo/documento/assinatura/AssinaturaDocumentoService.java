package br.com.infox.epp.processo.documento.assinatura;

import static br.com.infox.epp.documento.type.TipoAssinaturaEnum.F;
import static br.com.infox.epp.documento.type.TipoAssinaturaEnum.O;
import static br.com.infox.epp.documento.type.TipoAssinaturaEnum.S;

import java.io.Serializable;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.util.Strings;

import br.com.infox.certificado.Certificado;
import br.com.infox.certificado.CertificadoDadosPessoaFisica;
import br.com.infox.certificado.CertificadoFactory;
import br.com.infox.certificado.ValidaDocumento;
import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ClassificacaoDocumentoPapel;
import br.com.infox.epp.documento.manager.ClassificacaoDocumentoPapelManager;
import br.com.infox.epp.documento.type.TipoAssinaturaEnum;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaException.Motivo;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.AssinaturaDocumentoManager;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoBinarioManager;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;

@AutoCreate
@Scope(ScopeType.EVENT)
@Name(AssinaturaDocumentoService.NAME)
public class AssinaturaDocumentoService implements Serializable {

    private static final long serialVersionUID = 1L;
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
        boolean result = true;
        List<ClassificacaoDocumentoPapel> classificacaoDocumentoPapeis = documento
                .getClassificacaoDocumento().getClassificacaoDocumentoPapelList();
        for (ClassificacaoDocumentoPapel tipoProcessoDocumentoPapel : classificacaoDocumentoPapeis) {
            final TipoAssinaturaEnum tipoAssinatura = tipoProcessoDocumentoPapel
                    .getTipoAssinatura();
            if (F.equals(tipoAssinatura)) {
                continue;
            }
            final boolean documentoAssinado = isDocumentoAssinado(
                    documento, tipoProcessoDocumentoPapel.getPapel());
            if (S.equals(tipoAssinatura) && (result = documentoAssinado)
                    || O.equals(tipoAssinatura)
                    && !(result = result && documentoAssinado)) {
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

    public Boolean isDocumentoAssinado(Integer idDoc) {
        Documento documento = documentoManager.find(idDoc);
        return documento != null && isDocumentoAssinado(documento);
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
        if (!usuarioLogado.getPessoaFisica().checkCertChain(certChainBase64Encoded)) {
            throw new AssinaturaException(Motivo.CERTIFICADO_USUARIO_DIFERENTE_CADASTRO);
        }
    }

    public void assinarDocumento(
            final DocumentoBin documentoBin,
            final UsuarioPerfil usuarioPerfilAtual, final String certChain,
            final String signature) throws CertificadoException,
            AssinaturaException, DAOException {
        final UsuarioLogin usuario = usuarioPerfilAtual.getUsuarioLogin();
        verificaCertificadoUsuarioLogado(certChain, usuario);

        final AssinaturaDocumento assinaturaDocumento = new AssinaturaDocumento(
                documentoBin, usuarioPerfilAtual, certChain, signature);
        documentoBin.getAssinaturas().add(assinaturaDocumento);
        documentoBinManager.update(documentoBin);
    }

    public void assinarDocumento(final Documento documento,
            final UsuarioPerfil perfilAtual, final String certChain,
            final String signature) throws CertificadoException,
            AssinaturaException, DAOException {
        this.assinarDocumento(documento.getDocumentoBin(),
                perfilAtual, certChain, signature);
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
    	if (documentoBin == null || documentoBin == null || documentoBin.isMinuta()) {
    		return false;
    	}
    	return classificacaoDocumentoPapelManager.papelPodeAssinarClassificacao(papel, classificacao) && 
    			!isDocumentoAssinado(documentoBin, usuario);
    }
}
