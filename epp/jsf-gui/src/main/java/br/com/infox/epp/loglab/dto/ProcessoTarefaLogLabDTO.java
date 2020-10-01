package br.com.infox.epp.loglab.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of="idTaskInstance")
public class ProcessoTarefaLogLabDTO implements Serializable {

    private Long idTaskInstance;
    private Integer idProcesso;
    private String descricao;
    private String assignee;

}
