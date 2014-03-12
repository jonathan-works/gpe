package br.com.infox.certificado;

import java.math.BigInteger;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.util.Strings;

import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.certificado.exception.ValidaDocumentoException;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumentoBin;
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

    private static final LogProvider LOG = Logging.getLogProvider(ValidaDocumentoAction.class);
    @In
    private ProcessoDocumentoManager processoDocumentoManager;
    @In
    private DocumentoBinManager documentoBinManager;

    /**
     * @deprecated
     * */
    @Deprecated
    public void validaDocumento(ProcessoDocumento documento) {
        this.documento = documento;
        ProcessoDocumentoBin bin = documento.getProcessoDocumentoBin();
        validaDocumento(bin, bin.getCertChain(), bin.getSignature());
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
        byte[] data = null;

        if (Strings.isEmpty(bin.getCertChain())
                || Strings.isEmpty(bin.getSignature())) {
            FacesMessages.instance().add(StatusMessage.Severity.ERROR, "O documento não está assinado");
            return;
        }

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
            FacesMessages.instance().add(StatusMessage.Severity.ERROR, "Documento inválido");
            return;
        }
        try {
            ValidaDocumento validaDocumento = new ValidaDocumento(data, certChain, signature);
            setValido(validaDocumento.verificaAssinaturaDocumento());
            setDadosCertificado(validaDocumento.getDadosCertificado());
        } catch (ValidaDocumentoException | CertificadoException e) {
            LOG.error(".validaDocumento(bin, certChain, signature)", e);
            FacesMessages.instance().add(StatusMessage.Severity.ERROR, e.getMessage());
        }
    }

    public void validaDocumentoId(Integer idDocumento) {
        if (idDocumento == null) {
            FacesMessages.instance().add(StatusMessage.Severity.ERROR, "Id do documento não informado");
            return;
        }
        ProcessoDocumento processoDocumento = processoDocumentoManager.find(idDocumento);
        if (processoDocumento == null) {
            FacesMessages.instance().add(StatusMessage.Severity.ERROR, "Documento não encontrado.");
            return;
        }
        validaDocumento(processoDocumento);
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
        return dadosCertificado == null ? null : dadosCertificado.getNomeCertificadora();
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
}
