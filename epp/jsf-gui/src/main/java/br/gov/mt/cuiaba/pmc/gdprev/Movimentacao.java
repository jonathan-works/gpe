package br.gov.mt.cuiaba.pmc.gdprev;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of="id")
@AllArgsConstructor
public class Movimentacao {

    private Long id;
    private String taskName;
    private Date create;
    private Date start;
    private Date end;
    private Integer idLocalizacao;
    private String dsLocalizacao;
    private Integer idUsuario;
    private String nmUsuario;
    private Integer idPapel;
    private String nmPapel;
}
