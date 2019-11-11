package br.com.infox.certificado;

import java.math.BigInteger;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.certificado.util.DigitalSignatureUtils;
import br.com.infox.core.util.ArrayUtil;

public class CertificadoECPF implements Certificado, CertificadoDadosPessoaFisica {

    private static final int HEX_NUMBER = 16;

    private String c = null;
    private String cn = null;
    private String email = null;
    private String ou1 = null;
    private String ou2 = null;
    private String ou3 = null;
    private String ou4 = null;
    private String ou5 = null;
    private String o = null;
    private X509Certificate[] certChain;
    private X509Certificate mainCertificate;
    private PrivateKey privateKey;
    private Date dataValidadeInicio;
    private Date dataValidadeFim;
    private BigInteger serialNumber;
    private String nomeCertificadora;
    private DadosPessoaFisica dadosPessoaFisica;

    public CertificadoECPF(X509Certificate[] certChain, PrivateKey privateKey) throws CertificadoException {
        this.certChain = Arrays.copyOf(certChain, certChain.length);
        this.mainCertificate = (X509Certificate) this.certChain[0];
        this.privateKey = privateKey;
        processSubject();
    }

    public CertificadoECPF(X509Certificate[] certChain) throws CertificadoException {
        this(certChain, null);
    }

    public CertificadoECPF(String certChainBase64) throws CertificadoException {
        this(DigitalSignatureUtils.loadCertFromBase64String(certChainBase64), null);
    }

    public String getC() {
        return c;
    }

    public String getCn() {
        return cn;
    }

    public String getNome() {
        return cn.split(":")[0];
    }

    public String getDocumentoIdentificador() {
        if (cn.indexOf(':') > 0) {
            return cn.split(":")[1];
        }
        return null;
    }

    public String getEmail() {
        return email;
    }

    public String getOu1() {
        return ou1;
    }

    public String getOu2() {
        return ou2;
    }

    public String getOu3() {
        return ou3;
    }

    public String getOu4() {
        return ou4;
    }

    public String getOu5() {
        return ou5;
    }

    public String getO() {
        return o;
    }

    public Date getDataValidadeInicio() {
        return dataValidadeInicio;
    }

    public Date getDataValidadeFim() {
        return dataValidadeFim;
    }

    public BigInteger getSerialNumber() {
        return serialNumber;
    }

    public String getSerialNumberHex() {
        return serialNumber.toString(HEX_NUMBER);
    }

    public final void setSerialNumber(BigInteger serialNumber) {
        this.serialNumber = serialNumber;
    }

    public static String getCNValue(String cn) {
        String cnToken = "CN=";
        String nomeCertificadora = cn.substring(cn.indexOf(cnToken)
                + cnToken.length());
        nomeCertificadora = nomeCertificadora.substring(0, nomeCertificadora.indexOf(','));
        return nomeCertificadora;
    }

    private void processSubject() throws CertificadoException {
        Principal dados = mainCertificate.getSubjectDN();

        String dadosEmissor = mainCertificate.getIssuerDN().getName();
        try {
            nomeCertificadora = getCNValue(dadosEmissor);
        } catch (RuntimeException e) {
            throw new CertificadoException("Erro ao obter o nome da unidade certificadora.", e);
        }

        Map<String, String> map = gerarMapDadosCertificado(dados.getName());

        dataValidadeFim = mainCertificate.getNotAfter();
        dataValidadeInicio = mainCertificate.getNotBefore();
        setSerialNumber(mainCertificate.getSerialNumber());

        // Recupera o C
        c = map.get("C");

        // Recupera o CN
        cn = map.get("CN");

        // Recupera o e-mail
        email = map.get("EMAILADDRESS");

        // Recupera o OU
        ou1 = getValue(map, ("OU1"));
        ou2 = getValue(map, ("OU2"));
        ou3 = getValue(map, ("OU3"));
        ou4 = getValue(map, ("OU4"));
        ou5 = getValue(map, ("OU5"));

        // Recupera o O
        o = map.get("O");
        
        Map<ASN1ObjectIdentifier, String> otherNames = CertificateUtil.parseSubjectAlternativeNames(mainCertificate);
        for (ASN1ObjectIdentifier oid : otherNames.keySet()) {
            this.dadosPessoaFisica = CertificateUtil.parseDadosPessoaFisica(oid, otherNames.get(oid), this.dadosPessoaFisica);
        }
    }

    private String getValue(Map<String, String> map, String key) {
        String value = map.get(key);
        return value == null ? "" : value;
    }

    private Map<String, String> gerarMapDadosCertificado(String subjectDN) {
        String[] dados = subjectDN.split(", ");
        Map<String, String> map = new HashMap<String, String>();
        int i = 1;
        for (String linha : dados) {
            final String[] split = linha.split("=");
            String key = split[0];
            String value = split[1];
            if ("OU".equals(key)) {
                key += i++;
            }
            map.put(key, value);
        }
        return map;
    }

    @Override
    public String toString() {
        return cn;
    }

    public String getNomeCertificadora() {
        return nomeCertificadora;
    }

    public X509Certificate[] getCertChain() {
        return ArrayUtil.copyOf(certChain);
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public X509Certificate getMainCertificate() {
        return mainCertificate;
    }

    public boolean isValidoParaSistema(List<String> acceptedCaList) {
        for (String name : acceptedCaList) {
            if (name.equals(nomeCertificadora)) {
                return true;
            }
        }
        return false;
    }

    public Date getDataNascimento() {
        return dadosPessoaFisica.dataNascimento;
    }

    @Override
    public String getCPF() {
        return dadosPessoaFisica.cpf;
    }

    @Override
    public String getNIS() {
        return dadosPessoaFisica.nis;
    }

    @Override
    public String getRG() {
        return dadosPessoaFisica.rg;
    }

    @Override
    public String getOrgaoExpedidor() {
        return dadosPessoaFisica.orgaoExpedidor; 
    }

    @Override
    public String getTituloEleitor() {
        return dadosPessoaFisica.tituloEleitor;
    }

    @Override
    public String getZonaEleitoral() {
        return dadosPessoaFisica.zonaEleitoral;
    }

    @Override
    public String getSecaoEleitoral() {
        return dadosPessoaFisica.secaoEleitoral;
    }

    @Override
    public String getMunicipioTituloEleitor() {
        return dadosPessoaFisica.municipioTituloEleitor;
    }

    @Override
    public String getCEI() {
        return dadosPessoaFisica.cei;
    }

    @Override
    public String getAutoridadeCertificadora() {
        return getNomeCertificadora();
    }
}
