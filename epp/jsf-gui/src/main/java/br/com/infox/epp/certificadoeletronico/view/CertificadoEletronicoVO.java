package br.com.infox.epp.certificadoeletronico.view;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@EqualsAndHashCode(of="id")
@AllArgsConstructor
@NoArgsConstructor
public class CertificadoEletronicoVO {

    private Long id;
    private Integer idPessoa;
    private String nmPessoa;
    private Date dataInicio;
    private Date dataFim;
    private Date dataCadastro;

}
