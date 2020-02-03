package br.com.infox.epp.certificadoeletronico.builder;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CertificadoDTO {

    private String senha;
    private Date dataInicio;
    private Date dataFim;
    private byte[] cert;
    private byte[] key;
    private Integer idPessoaFisica;
    private Long idCertificadoEletronicoPai;

    public CertificadoDTO(String senha, Date dataInicio, Date dataFim, byte[] cert, byte[] key) {
        super();
        this.senha = senha;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.cert = cert;
        this.key = key;
    }
}
