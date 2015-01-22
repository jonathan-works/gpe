package br.com.infox.certificado;

import java.util.Date;

public interface CertificadoDadosPessoaFisica extends Certificado {
    String getCPF();
    Date getDataNascimento();
    /**
     * PIS, PASEP ou CI (NIS)
     */
    String getNIS();
    String getRG();
    String getOrgaoExpedidor();
    
    String getTituloEleitor();
    String getZonaEleitoral();
    String getSecaoEleitoral();
    String getMunicipioTituloEleitor();
    
    /**
     * Cadastro Específico do INSS (CEI)
     */
    String getCEI();
}
