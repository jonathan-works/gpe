package br.com.infox.certificado;

import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Date;

public interface Certificado {
    String getNome();
    Date getDataValidadeInicio();
    Date getDataValidadeFim();
    String getAutoridadeCertificadora();
    X509Certificate[] getCertChain();
    PrivateKey getPrivateKey();
    BigInteger getSerialNumber();
}
