package br.com.infox.certificado;

import java.math.BigInteger;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.certificado.exception.ValidaDocumentoException;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumento;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumentoService;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaException;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumentoBin;
import br.com.infox.epp.processo.documento.manager.AssinaturaDocumentoManager;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.seam.util.ComponentUtil;

@Name(ValidaDocumentoAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class ValidaDocumentoAction {

    public static final String NAME = "validaDocumentoAction";
    private Documento documento;
    private ProcessoDocumentoBin processoDocumentoBin;
    private Boolean valido;
    private Certificado dadosCertificado;
    private List<AssinaturaDocumento> listAssinaturaDocumento;
    
    private static final LogProvider LOG = Logging
            .getLogProvider(ValidaDocumentoAction.class);
    @In
    public DocumentoManager processoDocumentoManager;
    @In
    private DocumentoBinManager documentoBinManager;
    @In
    private AssinaturaDocumentoService assinaturaDocumentoService;
    @In
    private AssinaturaDocumentoManager assinaturaDocumentoManager;
    private Integer idProcessoDocumento;
    private String signature;
    private String certChain;
    

    /**
     * @deprecated
     * */
    @Deprecated
    public void validaDocumento(Documento documento) {
        this.documento = documento;
        ProcessoDocumentoBin bin = documento.getProcessoDocumentoBin();
        // TODO ASSINATURA
        // validaDocumento(bin, bin.getCertChain(), bin.getSignature());
    }

    /**
     * Valida a assinatura de um ProcessoDocumento. Quando o documento é do tipo
     * modelo as quebras de linha são retiradas.
     * 
     * @param id
     */
    public void validaDocumento(ProcessoDocumentoBin bin, String certChain,
            String signature) {
        processoDocumentoBin = bin;
        setValido(false);
        setDadosCertificado(null);
        try {
            ValidaDocumento validaDocumento = assinaturaDocumentoService
                    .validaDocumento(bin, certChain, signature);
            setValido(validaDocumento.verificaAssinaturaDocumento());
            setDadosCertificado(validaDocumento.getDadosCertificado());
        } catch (ValidaDocumentoException | CertificadoException
                | IllegalArgumentException e) {
            LOG.error(".validaDocumento(bin, certChain, signature)", e);
            FacesMessages.instance().add(StatusMessage.Severity.ERROR,
                    e.getMessage());
        }
    }
    
    public boolean isAssinadoPor(final UsuarioLogin usuarioLogin) {
        boolean result = false;
        final List<AssinaturaDocumento> assinaturas = getListAssinaturaDocumento();
        if (assinaturas != null) {
            for (final AssinaturaDocumento assinatura : assinaturas) {
                if (result = assinatura.getUsuario().equals(usuarioLogin)) {
                    break;
                }
            }
        }
        return result;
    }
    
    public void assinaDocumento(UsuarioPerfil usuarioPerfil) {
        if (this.processoDocumentoBin != null && !isAssinadoPor(usuarioPerfil.getUsuarioLogin())) {
            try {
                assinaturaDocumentoService.assinarDocumento(processoDocumentoBin, usuarioPerfil, certChain, signature);
                
                listAssinaturaDocumento=null;
            } catch (CertificadoException | AssinaturaException | DAOException e) {
                LOG.error("assinaDocumento(String, String, UsuarioPerfil)", e);
                FacesMessages.instance().add(Severity.ERROR, e.getMessage());
            }
        }
    }
    
    public void validaDocumentoId(Integer idDocumento) {
        try {
            this.documento = assinaturaDocumentoService.validaDocumentoId(idDocumento);
            this.processoDocumentoBin = this.documento.getProcessoDocumentoBin();
            refresh();
        } catch (IllegalArgumentException e) {
            FacesMessages.instance().add(Severity.ERROR, e.getMessage());
        }
    }

    public Documento getDocumento() {
        return documento;
    }

    public void setDocumento(Documento documento) {
        this.documento = documento;
    }

    public void setValido(Boolean valido) {
        this.valido = valido;
    }

    public Boolean getValido() {
        return valido;
    }

    public List<AssinaturaDocumento> getListAssinaturaDocumento() {
        if (listAssinaturaDocumento == null) {
            refresh();
        }
        return listAssinaturaDocumento;
    }

    private void refresh() {
        listAssinaturaDocumento = assinaturaDocumentoManager.listAssinaturaDocumentoByProcessoDocumento(documento);
    }

    public void setDadosCertificado(Certificado dadosCertificado) {
        this.dadosCertificado = dadosCertificado;
    }

    public Certificado getDadosCertificado() {
        return dadosCertificado;
    }

    public ProcessoDocumentoBin getProcessoDocumentoBin() {
        return processoDocumentoBin;
    }

    public String getNomeCertificadora() {
        return dadosCertificado == null ? null : dadosCertificado.getAutoridadeCertificadora();
    }

    public String getNome() {
        return dadosCertificado == null ? null : dadosCertificado.getNome();
    }

    public BigInteger getSerialNumber() {
        return dadosCertificado == null ? null : dadosCertificado.getSerialNumber();
    }

    public static ValidaDocumentoAction instance() {
        return ComponentUtil.getComponent(NAME);
    }

    public String getCertChain() {
        return certChain;
    }

    public void setCertChain(String certChain) {
        this.certChain = certChain;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Integer getIdProcessoDocumento() {
        return idProcessoDocumento;
    }

    public void setIdProcessoDocumento(Integer idProcessoDocumento) {
        this.idProcessoDocumento = idProcessoDocumento;
        validaDocumentoId(idProcessoDocumento);
    }
}
