package br.com.infox.epp.gdprev.vidafuncional;

import java.io.Serializable;
import java.util.Date;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@EqualsAndHashCode(of = "id")
public class FuncionarioVidaFuncionalDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long matricula;
    private String matriculaServidor;
    private String cpfServidor;
    private String nomeServidor;
    private Date dataExercicio;
    @Setter
    private boolean baixado;
}
