package br.com.infox.epp.processo.documento.assinatura;

import static br.com.infox.epp.documento.type.TipoAssinaturaEnum.F;
import static br.com.infox.epp.documento.type.TipoAssinaturaEnum.O;
import static br.com.infox.epp.documento.type.TipoAssinaturaEnum.S;

import java.io.Serializable;
import java.util.Date;
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
import br.com.infox.certificado.ValidaDocumento;
import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.UsuarioLocalizacao;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.documento.entity.TipoProcessoDocumentoPapel;
import br.com.infox.epp.documento.type.TipoAssinaturaEnum;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaException.Motivo;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.ProcessoDocumentoManager;

@Name(AssinaturaDocumentoService.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class AssinaturaDocumentoService implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final LogProvider LOG = Logging
            .getLogProvider(AssinaturaDocumentoService.class);
    public static final String NAME = "assinaturaDocumentoService";

    @In
    private ProcessoDocumentoManager processoDocumentoManager;
    @In
    private DocumentoBinManager documentoBinManager;

    public Boolean isDocumentoAssinado(final ProcessoDocumento processoDocumento) {
        final ProcessoDocumentoBin processoDocumentoBin = processoDocumento
                .getProcessoDocumentoBin();
        return processoDocumentoBin != null
                && isSignedAndValid(processoDocumentoBin.getAssinaturas());
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
        ProcessoDocumento processoDocumento = processoDocumentoManager.find(idDoc);
        return isDocumentoTotalmenteAssinado(processoDocumento);
    }
    
    public boolean isDocumentoTotalmenteAssinado(ProcessoDocumento processoDocumento) {
        boolean result = true;
        List<TipoProcessoDocumentoPapel> tipoProcessoDocumentoPapeis = processoDocumento
                .getTipoProcessoDocumento().getTipoProcessoDocumentoPapeis();
        for (TipoProcessoDocumentoPapel tipoProcessoDocumentoPapel : tipoProcessoDocumentoPapeis) {
            final TipoAssinaturaEnum tipoAssinatura = tipoProcessoDocumentoPapel.getTipoAssinatura();
            if (F.equals(tipoAssinatura)) {
                continue;
            }
            final boolean documentoAssinado = isDocumentoAssinado(processoDocumento, tipoProcessoDocumentoPapel.getPapel());
            if (S.equals(tipoAssinatura) && (result = documentoAssinado) 
                    || O.equals(tipoAssinatura) && !(result = result && documentoAssinado)) {
                break;
            }
        }
        return result;
    }

    public boolean isDocumentoAssinado(ProcessoDocumento processoDocumento,
            UsuarioLocalizacao usuarioLocalizacao) {
        boolean result = false;
        for (AssinaturaDocumento assinaturaDocumento : processoDocumento
                .getProcessoDocumentoBin().getAssinaturas()) {
            Papel papel = usuarioLocalizacao.getPapel();
            UsuarioLogin usuario = usuarioLocalizacao.getUsuario();
            if (assinaturaDocumento.getPapel().equals(papel)
                    || assinaturaDocumento.getUsuario().equals(usuario)) {
                result = isSignatureValid(assinaturaDocumento);
                break;
            }
        }
        return result;
    }

    public boolean isDocumentoAssinado(ProcessoDocumento processoDocumento, UsuarioLogin usuarioLogin) {
        boolean result = false;
        for (AssinaturaDocumento assinaturaDocumento : processoDocumento
                .getProcessoDocumentoBin().getAssinaturas()) {
            if (assinaturaDocumento.getUsuario().equals(usuarioLogin)) {
                result = isSignatureValid(assinaturaDocumento);
                break;
            }
        }
        return result;
    }

    public boolean isDocumentoAssinado(ProcessoDocumento processoDocumento, Papel papel) {
        boolean result = false;
        for (AssinaturaDocumento assinaturaDocumento : processoDocumento
                .getProcessoDocumentoBin().getAssinaturas()) {
            if (assinaturaDocumento.getPapel().equals(papel)) {
                result = isSignatureValid(assinaturaDocumento);
                break;
            }
        }
        return result;
    }

    public Boolean isDocumentoAssinado(Integer idDoc) {
        ProcessoDocumento processoDocumento = processoDocumentoManager
                .find(idDoc);
        return processoDocumento != null
                && isDocumentoAssinado(processoDocumento);
    }

    public void verificaCertificadoUsuarioLogado(String certChainBase64Encoded,
            UsuarioLogin usuarioLogado) throws CertificadoException,
            AssinaturaException {
        if (Strings.isEmpty(certChainBase64Encoded)) {
            throw new AssinaturaException(Motivo.SEM_CERTIFICADO);
        }
        if (usuarioLogado.getPessoaFisica() == null) {
            throw new AssinaturaException(Motivo.USUARIO_SEM_PESSOA_FISICA);
        }
        if (Strings.isEmpty(usuarioLogado.getPessoaFisica().getCertChain())) {
            final Certificado certificado = new Certificado(
                    certChainBase64Encoded);
            final String cpfCertificado = certificado.getCn().split(":")[1];
            if (cpfCertificado.equals(usuarioLogado.getPessoaFisica().getCpf()
                    .replace(".", "").replace("-", ""))) {
                usuarioLogado.getPessoaFisica().setCertChain(
                        certChainBase64Encoded);
            } else {
                throw new AssinaturaException(
                        Motivo.CADASTRO_USUARIO_NAO_ASSINADO);
            }
        }
        if (!usuarioLogado.getPessoaFisica().checkCertChain(
                certChainBase64Encoded)) {
            throw new AssinaturaException(
                    Motivo.CERTIFICADO_USUARIO_DIFERENTE_CADASTRO);
        }
    }

    public void assinarDocumento(final ProcessoDocumento processoDocumento,
            final UsuarioLocalizacao perfilAtual, final String certChain,
            final String signature) throws CertificadoException,
            AssinaturaException {
        final UsuarioLogin usuario = perfilAtual.getUsuario();
        verificaCertificadoUsuarioLogado(certChain, usuario);

        final String nomeUsuario = usuario.getNomeUsuario();
        final Papel papel = perfilAtual.getPapel();
        final ProcessoDocumentoBin processoDocumentoBin = processoDocumento
                .getProcessoDocumentoBin();

        final AssinaturaDocumento assinaturaDocumento = new AssinaturaDocumento();
        assinaturaDocumento.setProcessoDocumentoBin(processoDocumentoBin);
        assinaturaDocumento.setDataAssinatura(new Date());
        assinaturaDocumento.setCertChain(certChain);
        assinaturaDocumento.setSignature(signature);
        assinaturaDocumento.setNomeLocalizacao(perfilAtual.getLocalizacao()
                .getLocalizacao());
        assinaturaDocumento.setNomePapel(papel.getNome());
        assinaturaDocumento.setNomeUsuario(nomeUsuario);
        assinaturaDocumento.setUsuario(usuario);
        assinaturaDocumento.setPapel(papel);

        processoDocumentoBin.getAssinaturas().add(assinaturaDocumento);
    }

    public boolean isDocumentoAssinado(Integer idDocumento, UsuarioLocalizacao perfil) {
        ProcessoDocumento processoDocumento = processoDocumentoManager.find(idDocumento);
        return processoDocumento != null && isDocumentoAssinado(processoDocumento, perfil);
    }

    public ValidaDocumento validaDocumento(ProcessoDocumentoBin bin, String certChain, String signature) throws CertificadoException {
        byte[] data = null;
        if (!bin.isBinario()) {
            data = ValidaDocumento.removeBR(bin.getModeloDocumento()).getBytes();
        } else {
            try {
                data = documentoBinManager.getData(bin.getIdProcessoDocumentoBin());
            } catch (Exception e) {
                throw new IllegalArgumentException("Erro ao obter os dados do binário", e);
            }
        }
        if (data == null) {
            throw new IllegalArgumentException("Documento inválido");
        }
        return new ValidaDocumento(data, certChain, signature);
    }

    public ProcessoDocumento validaDocumentoId(Integer idDocumento) {
        if (idDocumento == null) {
            throw new IllegalArgumentException("Id do documento não informado");
        }
        ProcessoDocumento processoDocumento = processoDocumentoManager.find(idDocumento);
        if (processoDocumento == null) {
            throw new IllegalArgumentException("Documento não encontrado.");
        }
    
        return processoDocumento;
    }
}
