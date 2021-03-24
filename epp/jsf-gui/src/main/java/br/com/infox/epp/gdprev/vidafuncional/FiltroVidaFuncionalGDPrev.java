package br.com.infox.epp.gdprev.vidafuncional;

import java.io.Serializable;

import br.com.infox.core.util.StringUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FiltroVidaFuncionalGDPrev implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nome;
    private String cpf;
    private Integer matricula;
    private String nomeDocumento;

    public boolean isEmpty() {
        return StringUtil.isEmpty(getNome()) && StringUtil.isEmpty(getCpf()) && getMatricula() == null && StringUtil.isEmpty(getNomeDocumento());
    }
}
