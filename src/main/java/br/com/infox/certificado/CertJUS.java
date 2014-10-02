package br.com.infox.certificado;

import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.x509.extension.X509ExtensionUtil;

import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.certificado.util.DigitalSignatureUtils;

public abstract class CertJUS implements Certificado {

    protected String nome;
    protected String cpf;
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
    public String getCPF() {
        return cpf;
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
        try {
            Collection<?> subjectAltNames = X509ExtensionUtil.getSubjectAlternativeNames(mainCertificate);
            for (Object o : subjectAltNames) {
                if (!(o instanceof List<?>)) {
                    continue;
                }
                List<?> l = (List<?>) o;
                Object otherName = l.get(1);
                if (otherName instanceof DERSequence) {
                    DERSequence seq = (DERSequence) otherName;
                    DERObjectIdentifier oid = (DERObjectIdentifier) seq.getObjectAt(0);
                    DERTaggedObject tagged = (DERTaggedObject) seq.getObjectAt(1);
                    DERObject obj = tagged.getObject();
                    
                    String info = null;
                    if (obj instanceof DEROctetString) {
                        info = new String(((DEROctetString) obj).getOctets());
                    } else if (obj instanceof DERPrintableString) {
                        info = new String(((DERPrintableString) obj).getOctets());
                    } else if (obj instanceof DERUTF8String) {
                        info = ((DERUTF8String) obj).getString();
                    }
                    
                    if (info == null || info.isEmpty()) {
                        continue;
                    }
                    parseCertificateData(oid, info);
                } 
            }
        } catch (CertificateParsingException e) {
            throw new CertificadoException(e);
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
    
    protected abstract void parseCertificateData(DERObjectIdentifier oid, String info);
}
