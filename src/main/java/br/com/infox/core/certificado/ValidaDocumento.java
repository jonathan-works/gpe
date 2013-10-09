/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.

 Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; versão 2 da Licença.
 Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 
 Consulte a GNU GPL para mais detalhes.
 Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.core.certificado;

import java.security.GeneralSecurityException;
import java.security.cert.CertPath;
import java.security.cert.X509Certificate;
import java.util.List;

import org.jboss.seam.util.Base64;

import br.com.infox.core.certificado.util.DigitalSignatureUtils;
import br.com.itx.util.ArrayUtil;

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


	/** Creates a new instance of testeAssinatura 
	 * @throws CertificadoException */
	public ValidaDocumento(byte[] documento, String certificado, String assinatura) throws CertificadoException {
		this.documento = ArrayUtil.copyOf(documento);
		this.certificado = certificado;
		this.assinatura = assinatura;
		try {
			this.dadosCertificado = new Certificado(DigitalSignatureUtils.loadCertFromBase64String(certificado));
		} catch (Exception e) {
			throw new CertificadoException("Certificado inválido: " + e.getMessage(), e);
		} 
	}
	
	public Certificado getDadosCertificado() {
		return dadosCertificado;
	}

	/**
	 * Metodo que executa a verificação da assinatura do documento. Retorna <code>true</code>
	 * caso a assinatura seja valida.
	 * @return
	 * @throws ValidaDocumentoException
	 */
	public boolean verificaAssinaturaDocumento() throws ValidaDocumentoException {
		processReceivedCertificationChain();
		processReceivedSignature();
		return isReceivedSignatureValid();
	}

	public static String removeBR(String texto) {
		String saida = texto.replace("\\015","");
		saida = saida.replace("\\012","");
		saida = saida.replace("\n","");
		saida = saida.replace("\r","");
		return saida;
	}

	private void processReceivedSignature()
	throws ValidaDocumentoException {
		String mSignatureBase64Encoded = removeBR(assinatura);
		try {
			mSignature = Base64.decode(mSignatureBase64Encoded);
		} catch (Exception e) {
			throw new ValidaDocumentoException("Assinatura Invalida.", e);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void processReceivedCertificationChain()
	throws ValidaDocumentoException {
		String certChainBase64Encoded = removeBR(certificado);
		try {
			CertPath mCertPath = DigitalSignatureUtils.loadCertPathFromBase64String(
					certChainBase64Encoded);
			
			List certsInChain = mCertPath.getCertificates();
			X509Certificate[] mCertChain = (X509Certificate[]) certsInChain.toArray(new X509Certificate[certsInChain.size()]);
			mCertificate = mCertChain[0];
		}
		catch (Exception e) {
			throw new ValidaDocumentoException("Certificado Invalido.", e);
		}
	}

	private boolean isReceivedSignatureValid() throws ValidaDocumentoException {
		try {
            boolean signatureValid = DigitalSignatureUtils.verifyDocumentSignature(
                    documento, mCertificate, mSignature);
			return signatureValid;
		} catch (GeneralSecurityException e) {
			throw new ValidaDocumentoException("Erro ao verificar a assinatura " +
					"do documento: " + e.getMessage(), e);
		}
	}

	public static class ValidaDocumentoException extends Exception {
		private static final long serialVersionUID = 1L;

		public ValidaDocumentoException(String message, Throwable cause) {
			super(message, cause);
		}

		public ValidaDocumentoException(String message) {
			super(message);
		}
	}

}