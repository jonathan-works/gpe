package br.com.infox.certificado;

import java.io.IOException;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.DERObject;

public class CertificateUtil {
    public static String getExtensionValue(byte[] derEncodedValue) throws IOException {
        if (derEncodedValue == null) {
            return null;
        }
        
        DERObject object = toDERObject(derEncodedValue);
        
        
        return null;
    }
    
    public static DERObject toDERObject(byte[] data) throws IOException {
        try (ASN1InputStream asn = new ASN1InputStream(data)) {
            return asn.readObject();
        }
    }
}
