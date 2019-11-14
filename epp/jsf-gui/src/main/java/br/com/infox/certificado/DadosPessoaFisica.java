package br.com.infox.certificado;

import java.util.Date;

final class DadosPessoaFisica {
    Date dataNascimento;
    String cpf;
    /**
     * PIS, PASEP ou CI (NIS)
     */
    String nis;
    String rg;
    String orgaoExpedidor;
    
    String tituloEleitor;
    String zonaEleitoral;
    String secaoEleitoral;
    String municipioTituloEleitor;
    
    /**
     * Cadastro Específico do INSS (CEI)
     */
    String cei;
}