package br.com.infox.epp.tarefaexterna.view;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of="id")
public class ClassificacaoDocumentoVO implements Serializable{
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String codigo;
    private String descricao;

}
