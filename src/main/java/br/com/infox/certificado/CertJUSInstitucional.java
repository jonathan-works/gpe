package br.com.infox.certificado;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Date;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

import br.com.infox.certificado.exception.CertificadoException;

public class CertJUSInstitucional extends CertJUS implements CertificadoDadosPessoaFisica {

    private DadosPessoaFisica dadosPessoaFisica;
    
    public CertJUSInstitucional(String certChainBase64) throws CertificadoException {
        super(certChainBase64);
    }
    
    public CertJUSInstitucional(X509Certificate[] certChain, PrivateKey privateKey) throws CertificadoException {
        super(certChain, privateKey);
    }
    
    public CertJUSInstitucional(X509Certificate[] certChain) throws CertificadoException {
        super(certChain);
    }
    
    @Override
    protected void parseCertificateData(ASN1ObjectIdentifier oid, String info) throws CertificadoException {
        this.dadosPessoaFisica = CertificateUtil.parseDadosPessoaFisica(oid, info, this.dadosPessoaFisica);
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
}
