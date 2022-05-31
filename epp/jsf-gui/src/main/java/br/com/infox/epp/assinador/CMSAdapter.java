package br.com.infox.epp.assinador;

import java.security.Security;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.function.Predicate;

import javax.validation.ValidationException;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.AttributeCertificateIssuer;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Store;

import br.com.infox.epp.assinador.assinavel.TipoSignedData;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaException;

public class CMSAdapter {

	/**
	 * Constantes dos dados legados
	 */
    private static final String X509_CERTIFICATE_TYPE = "X.509";
    private static final String CERT_CHAIN_ENCODING = "PkiPath";
    //private static final String DIGITAL_SIGNATURE_ALGORITHM_NAME = "SHA1withRSA";
    //private static final String CERT_CHAIN_VALIDATION_ALGORITHM = "PKIX";
    
    private X509Certificate toX509Certificate(X509CertificateHolder holder) {
    	Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    	try {
			return new JcaX509CertificateConverter().setProvider( "BC" )
					  .getCertificate( holder );
		} catch (CertificateException e) {
			throw new RuntimeException("Erro ao converter certificados", e);
		}
    }
    
    public String getCertChainBase64(List<X509Certificate> certChain) {
		CertificateFactory cf = null;
		try {
			cf = CertificateFactory.getInstance(X509_CERTIFICATE_TYPE);
		} catch (CertificateException e1) {
			throw new RuntimeException("Erro ao instanciar factory de certificados", e1);
		}
		
		byte[] encoded = null;
		try {
			CertPath certPath = cf.generateCertPath(certChain);
			encoded = certPath.getEncoded(CERT_CHAIN_ENCODING);
		} catch (CertificateException e1) {
			throw new RuntimeException("Erro ao criar certPath", e1);
		}
		return Base64.encodeBase64String(encoded);
    }
    
    private List<X509Certificate> getCertChain(Iterator<X509CertificateHolder> signerCertificates, Store<X509CertificateHolder> certStore) {
    	List<X509Certificate> certChain = new ArrayList<>();
    	X509CertificateHolder signerCertificate = signerCertificates.next();
    	certChain.add(toX509Certificate(signerCertificate));
    	X500Name issuer = signerCertificate.getIssuer();
    	while(issuer != null) {
    		@SuppressWarnings("unchecked")
    		X509CertificateHolder issuerCert = (X509CertificateHolder)certStore.getMatches(new AttributeCertificateIssuer(issuer)).iterator().next();
    		certChain.add(toX509Certificate(issuerCert));
    		
    		X500Name lastIssuer = issuer;
    		issuer = issuerCert.getIssuer();
    		if(lastIssuer.equals(issuer)) {
    			issuer = null;
    		}
    	}
    	
    	return certChain;
    }
    
    @SuppressWarnings("unchecked")
	public boolean validarAssinatura(byte[] signedData, TipoSignedData tipoSignedData, byte[] signature) throws AssinaturaException {
		try {
			Map<String, byte[]> hashes = new HashMap<>();
			hashes.put(tipoSignedData.getOid(), signedData);
			
			CMSSignedData cmsSignedData = new CMSSignedData(hashes, signature);
			Store<X509CertificateHolder> certStore = cmsSignedData.getCertificates();
			Iterator<SignerInformation> signers = cmsSignedData.getSignerInfos().getSigners().iterator();

			if(!signers.hasNext()) {
				throw new AssinaturaException("Nenhuma assinatura encontrada");
			}
			
			SignerInformation signerInformation = signers.next();
			Iterator<X509CertificateHolder> signerCertificates = certStore.getMatches(signerInformation.getSID()).iterator();
				
			Certificate cert = new JcaX509CertificateConverter().getCertificate(signerCertificates.next());
			boolean valido = signerInformation.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider("BC").build(cert.getPublicKey()));
			
			return valido;
		} catch (CMSException | CertificateException | OperatorCreationException e) {
			return false;
		}
    }
    
    @SuppressWarnings("unchecked")
	public DadosAssinaturaLegada convert(byte[] signature) {
		try {
			CMSSignedData cmsSignedData = new CMSSignedData(signature);
			
			Store<X509CertificateHolder> certStore = cmsSignedData.getCertificates();
			Iterator<SignerInformation> signers = cmsSignedData.getSignerInfos().getSigners().iterator();
			
			while(signers.hasNext()) {
				SignerInformation signer = signers.next();
				Iterator<X509CertificateHolder> signerCertificates = certStore.getMatches(signer.getSID()).iterator();				
					
					List<X509Certificate> certChain = getCertChain(signerCertificates, certStore);
					String certChainBase64 = getCertChainBase64(certChain);
					String signatureBase64 = Base64.encodeBase64String(signature);
					
					return new DadosAssinaturaLegada(certChain, certChainBase64, signatureBase64);
			}
			
		} catch (CMSException e) {
			throw new ValidationException("Erro na assinatura", e);
		}
		throw new RuntimeException("Nenhum assinante encontrado na assinatura");
	}
	
}
