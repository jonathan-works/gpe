package br.com.infox.certificado;

import static br.com.infox.constants.WarningConstants.RAWTYPES;
import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.security.GeneralSecurityException;
import java.security.cert.CertPath;
import java.security.cert.X509Certificate;
import java.util.List;

import org.jboss.seam.util.Base64;

import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.certificado.exception.ValidaDocumentoException;
import br.com.infox.certificado.util.DigitalSignatureUtils;
import br.com.infox.core.util.ArrayUtil;

/**
 * 
 * @author Breno
 */
public class ValidaDocumento {

    /**
     * Creates a new instance of ValidaDocumento
     */

    private byte[] documento = null;
    private String certificado = null;
    private String assinatura = null;
    private X509Certificate mCertificate = null;
    private byte[] mSignature = null;

    // Valores do certificado público
    private Certificado dadosCertificado;

    // Fim dos valores do certificado público

    /**
     * Creates a new instance of testeAssinatura
     * 
     * @throws CertificadoException
     */
    public ValidaDocumento(byte[] documento, String certificado,
            String assinatura) throws CertificadoException {
        this.documento = ArrayUtil.copyOf(documento);
        this.certificado = certificado;
        this.assinatura = assinatura;
        try {
            this.dadosCertificado = CertificadoFactory.createCertificado(DigitalSignatureUtils.loadCertFromBase64String(certificado));
        } catch (Exception e) {
            throw new CertificadoException("Certificado inválido: "
                    + e.getMessage(), e);
        }
    }

    public Certificado getDadosCertificado() {
        return dadosCertificado;
    }

    /**
     * Metodo que executa a verificação da assinatura do documento. Retorna
     * <code>true</code> caso a assinatura seja valida.
     * 
     * @return
     * @throws ValidaDocumentoException
     */
    public boolean verificaAssinaturaDocumento() throws ValidaDocumentoException {
        processReceivedCertificationChain();
        processReceivedSignature();
        return isReceivedSignatureValid();
    }

    public static String removeBR(String texto) {
        String saida = texto.replace("\\015", "");
        saida = saida.replace("\\012", "");
        saida = saida.replace("\n", "");
        saida = saida.replace("\r", "");
        return saida;
    }

    private void processReceivedSignature() throws ValidaDocumentoException {
        String mSignatureBase64Encoded = removeBR(assinatura);
        try {
            mSignature = Base64.decode(mSignatureBase64Encoded);
        } catch (Exception e) {
            throw new ValidaDocumentoException("Assinatura Invalida.", e);
        }
    }

    @SuppressWarnings({ RAWTYPES, UNCHECKED })
    private void processReceivedCertificationChain() throws ValidaDocumentoException {
        String certChainBase64Encoded = removeBR(certificado);
        try {
            CertPath mCertPath = DigitalSignatureUtils.loadCertPathFromBase64String(certChainBase64Encoded);

            List certsInChain = mCertPath.getCertificates();
            X509Certificate[] mCertChain = (X509Certificate[]) certsInChain.toArray(new X509Certificate[certsInChain.size()]);
            mCertificate = mCertChain[0];
        } catch (Exception e) {
            throw new ValidaDocumentoException("Certificado Invalido.", e);
        }
    }

    private boolean isReceivedSignatureValid() throws ValidaDocumentoException {
        try {
            return DigitalSignatureUtils.verifyDocumentSignature(documento, mCertificate, mSignature);
        } catch (GeneralSecurityException e) {
            throw new ValidaDocumentoException("Erro ao verificar a assinatura "
                    + "do documento: " + e.getMessage(), e);
        }
    }

}
