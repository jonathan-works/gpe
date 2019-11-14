package br.com.infox.certificado;

import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Map;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.certificado.util.DigitalSignatureUtils;

public abstract class CertJUS implements Certificado {

    protected String nome;
    protected X509Certificate[] certChain;
    protected PrivateKey privateKey;
    
    public CertJUS(X509Certificate[] certChain, PrivateKey privateKey) throws CertificadoException {
        this.certChain = certChain;
        this.privateKey = privateKey;
        parse();
    }
    
    public CertJUS(X509Certificate[] certChain) throws CertificadoException {
        this(certChain, null);
    }
    
    public CertJUS(String certChainBase64) throws CertificadoException {
        this(DigitalSignatureUtils.loadCertFromBase64String(certChainBase64));
    }
    
    @Override
    public String getNome() {
        return nome;
    }

    @Override
    public Date getDataValidadeInicio() {
        return certChain[0].getNotBefore();
    }

    @Override
    public Date getDataValidadeFim() {
        return certChain[0].getNotAfter();
    }

    @Override
    public String getAutoridadeCertificadora() {
        return CertificadoECPF.getCNValue(certChain[0].getIssuerDN().getName());
    }

    @Override
    public PrivateKey getPrivateKey() {
        return privateKey;
    }
    
    @Override
    public X509Certificate[] getCertChain() {
        return certChain;
    }
    
    @Override
    public BigInteger getSerialNumber() {
        return certChain[0].getSerialNumber();
    }
    
    private void parse() throws CertificadoException {
        X509Certificate mainCertificate = this.certChain[0];
        parseSubjectDN(mainCertificate);
        parseSubjectAlternativeNames(mainCertificate);
    }
    
    private void parseSubjectAlternativeNames(X509Certificate mainCertificate) throws CertificadoException {
        Map<ASN1ObjectIdentifier, String> otherNames = CertificateUtil.parseSubjectAlternativeNames(mainCertificate);
        for (ASN1ObjectIdentifier oid : otherNames.keySet()) {
            parseCertificateData(oid, otherNames.get(oid));
        }
    }

    private void parseSubjectDN(X509Certificate mainCertificate) {
        String subjectDN = mainCertificate.getSubjectDN().getName();
        String[] dados = subjectDN.split(", ");
        for (String linha : dados) {
            String[] dado = linha.split("=");
            if (dado[0].equals("CN")) {
                String cn = dado[1];
                this.nome = cn.split(":")[0];
            }
        }
    }
    
    protected abstract void parseCertificateData(ASN1ObjectIdentifier oid, String info) throws CertificadoException;
}
