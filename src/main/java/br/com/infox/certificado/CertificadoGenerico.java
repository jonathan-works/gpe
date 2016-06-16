package br.com.infox.certificado;

import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Map;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.certificado.util.DigitalSignatureUtils;

public class CertificadoGenerico implements Certificado, CertificadoDadosPessoaFisica {

	private X509Certificate[] certChain;
	private PrivateKey privateKey;
	private DadosPessoaFisica dadosPessoaFisica;
	private String nome;
	
	public CertificadoGenerico(X509Certificate[] certChain, PrivateKey privateKey) throws CertificadoException {
        this.certChain = certChain;
        this.privateKey = privateKey;
        parse();
    }
    
    public CertificadoGenerico(X509Certificate[] certChain) throws CertificadoException {
        this(certChain, null);
    }
    
    public CertificadoGenerico(String certChainBase64) throws CertificadoException {
        this(DigitalSignatureUtils.loadCertFromBase64String(certChainBase64));
    }

    private void parse() throws CertificadoException {
        X509Certificate mainCertificate = this.certChain[0];
        parseSubjectDN(mainCertificate);
        parseSubjectAlternativeNames(mainCertificate);
    }
    
    private void parseSubjectAlternativeNames(X509Certificate mainCertificate) throws CertificadoException {
        Map<ASN1ObjectIdentifier, String> otherNames = CertificateUtil.parseSubjectAlternativeNames(mainCertificate);
        for (ASN1ObjectIdentifier oid : otherNames.keySet()) {
        	this.dadosPessoaFisica = CertificateUtil.parseDadosPessoaFisica(oid, otherNames.get(oid), this.dadosPessoaFisica);
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
    
	@Override
	public String getCPF() {
		return dadosPessoaFisica.cpf;
	}

	@Override
	public Date getDataNascimento() {
		return dadosPessoaFisica.dataNascimento;
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
	public String getNome() {
		return this.nome;
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
	public X509Certificate[] getCertChain() {
		return certChain;
	}

	@Override
	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	@Override
	public BigInteger getSerialNumber() {
		return certChain[0].getSerialNumber();
	}
}
