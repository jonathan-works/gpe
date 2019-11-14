package br.com.infox.certificado;

import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.x509.extension.X509ExtensionUtil;

import br.com.infox.certificado.exception.CertificadoException;

class CertificateUtil {
    private static final ASN1ObjectIdentifier OID_DADOS_TITULAR_PF = new ASN1ObjectIdentifier("2.16.76.1.3.1");
    private static final ASN1ObjectIdentifier OID_DADOS_ELEITORAIS = new ASN1ObjectIdentifier("2.16.76.1.3.5");
    private static final ASN1ObjectIdentifier OID_DADOS_INSS = new ASN1ObjectIdentifier("2.16.76.1.3.6");

    static DadosPessoaFisica parseDadosPessoaFisica(ASN1ObjectIdentifier oid, String info, DadosPessoaFisica dadosPessoaFisica) throws CertificadoException {
        SimpleDateFormat certJUSDateFormatter = new SimpleDateFormat("ddMMyyyy");
        if (dadosPessoaFisica == null) {
            dadosPessoaFisica = new DadosPessoaFisica();
        }
        if (oid.equals(OID_DADOS_TITULAR_PF)) {
            try {
                dadosPessoaFisica.dataNascimento = certJUSDateFormatter.parse(info.substring(0, 8));
            } catch (ParseException e) {
                throw new CertificadoException(e);
            }
            dadosPessoaFisica.cpf = info.substring(8, 19);
            dadosPessoaFisica.nis = info.substring(19, 30);
            dadosPessoaFisica.rg = info.substring(30, 45);
            if (!dadosPessoaFisica.rg.replace("0", "").isEmpty()) {
                dadosPessoaFisica.orgaoExpedidor = info.substring(45, info.length());
            }
        } else if (oid.equals(OID_DADOS_ELEITORAIS)) {
            dadosPessoaFisica.tituloEleitor = info.substring(0, 12);
            dadosPessoaFisica.zonaEleitoral = info.substring(12, 15);
            dadosPessoaFisica.secaoEleitoral = info.substring(15, 19);
            if (!dadosPessoaFisica.tituloEleitor.replace("0", "").isEmpty()) {
                dadosPessoaFisica.municipioTituloEleitor = info.substring(19);
            }
        } else if (oid.equals(OID_DADOS_INSS)) {
            dadosPessoaFisica.cei = info.substring(0, 12);
        }
        return dadosPessoaFisica;
    }
    
    static Map<ASN1ObjectIdentifier, String> parseSubjectAlternativeNames(X509Certificate mainCertificate) throws CertificadoException {
        Map<ASN1ObjectIdentifier, String> otherNames = new HashMap<>();
        try {
            Collection<?> subjectAltNames = X509ExtensionUtil.getSubjectAlternativeNames(mainCertificate);
            for (Object o : subjectAltNames) {
                if (!(o instanceof List<?>)) {
                    continue;
                }
                List<?> l = (List<?>) o;
                Object otherName = l.get(1);
                if (otherName instanceof DLSequence) {
                    DLSequence seq = (DLSequence) otherName;
                    ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier) seq.getObjectAt(0);
                    ASN1TaggedObject tagged = (ASN1TaggedObject) seq.getObjectAt(1);
                    ASN1Object obj = tagged.getObject();
                    
                    String info = null;
                    if (obj instanceof ASN1OctetString) {
                        info = new String(((ASN1OctetString) obj).getOctets());
                    } else if (obj instanceof DERPrintableString) {
                        info = new String(((DERPrintableString) obj).getOctets());
                    } else if (obj instanceof DERUTF8String) {
                        info = ((DERUTF8String) obj).getString();
                    }
                    
                    if (info == null || info.isEmpty()) {
                        continue;
                    }
                    otherNames.put(oid, info);
                }
            }
        } catch (CertificateParsingException e) {
            throw new CertificadoException(e);
        }
        return otherNames;
    }
}
