package br.com.infox.certificado;

import java.math.BigInteger;
import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.certificado.exception.ValidaDocumentoException;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumento;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumentoService;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumentoBin;
import br.com.infox.epp.processo.documento.manager.AssinaturaDocumentoManager;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.ProcessoDocumentoManager;
import br.com.infox.seam.util.ComponentUtil;

@Name(ValidaDocumentoAction.NAME)
public class ValidaDocumentoAction {

    public static final String NAME = "validaDocumentoAction";
    private ProcessoDocumento documento;
    private ProcessoDocumentoBin processoDocumentoBin;
    private Boolean valido;
    private Certificado dadosCertificado;

    private static final LogProvider LOG = Logging
            .getLogProvider(ValidaDocumentoAction.class);
    @In
    public ProcessoDocumentoManager processoDocumentoManager;
    @In
    private DocumentoBinManager documentoBinManager;
    @In
    private AssinaturaDocumentoService assinaturaDocumentoService;
    @In
    private AssinaturaDocumentoManager assinaturaDocumentoManager;

    /**
     * @deprecated
     * */
    @Deprecated
    public void validaDocumento(ProcessoDocumento documento) {
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

    public void validaDocumentoId(Integer idDocumento) {
        try {
            this.documento = assinaturaDocumentoService
                    .validaDocumentoId(idDocumento);
        } catch (IllegalArgumentException e) {
            FacesMessages.instance().add(Severity.ERROR, e.getMessage());
        }
    }

    public ProcessoDocumento getDocumento() {
        return documento;
    }

    public void setDocumento(ProcessoDocumento documento) {
        this.documento = documento;
    }

    public void setValido(Boolean valido) {
        this.valido = valido;
    }

    public Boolean getValido() {
        return valido;
    }

    public List<AssinaturaDocumento> getListAssinaturaDocumento() {
        return assinaturaDocumentoManager
                .listAssinaturaDocumentoByProcessoDocumento(documento);
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
        return dadosCertificado == null ? null : dadosCertificado
                .getNomeCertificadora();
    }

    public String getNome() {
        return dadosCertificado == null ? null : dadosCertificado.getNome();
    }

    public BigInteger getSerialNumber() {
        return dadosCertificado == null ? null : dadosCertificado
                .getSerialNumber();
    }

    public static ValidaDocumentoAction instance() {
        return ComponentUtil.getComponent(NAME);
    }
}
