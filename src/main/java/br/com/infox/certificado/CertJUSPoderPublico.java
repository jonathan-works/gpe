package br.com.infox.certificado;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bouncycastle.asn1.DERObjectIdentifier;

import br.com.infox.certificado.exception.CertificadoException;

public class CertJUSPoderPublico extends CertJUS {

    private Date dataNascimento;
    private SimpleDateFormat certJUSDateFormatter = new SimpleDateFormat("ddMMyyyy");
    
    private static final DERObjectIdentifier OID_DADOS_TITULAR_PF = new DERObjectIdentifier("2.16.76.1.3.1");
    
    public CertJUSPoderPublico(String certChainBase64) throws CertificadoException {
        super(certChainBase64);
    }
    
    public CertJUSPoderPublico(X509Certificate[] certChain, PrivateKey privateKey) throws CertificadoException {
        super(certChain, privateKey);
    }
    
    public CertJUSPoderPublico(X509Certificate[] certChain) throws CertificadoException {
        super(certChain);
    }
    
    @Override
    protected void parseCertificateData(DERObjectIdentifier oid, String info) {
        if (oid.equals(OID_DADOS_TITULAR_PF)) {
            try {
                this.dataNascimento = certJUSDateFormatter.parse(info.substring(0, 8));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            this.cpf = info.substring(8, 19);
        }
    }
    
    public Date getDataNascimento() {
        return dataNascimento;
    }
}
