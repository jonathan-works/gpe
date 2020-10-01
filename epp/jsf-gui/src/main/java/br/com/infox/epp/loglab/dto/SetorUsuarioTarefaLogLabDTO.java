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
@EqualsAndHashCode
public class SetorUsuarioTarefaLogLabDTO implements Serializable {

    private String swimlane;
    private String usuario;

}
