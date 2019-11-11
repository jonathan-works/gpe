package br.com.infox.epp.fluxo.definicao;

import java.io.Serializable;

import javax.inject.Named;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.entity.DefinicaoProcesso;

@Named
@ViewScoped
public class DefinicaoProcessoController implements Serializable {
    private static final long serialVersionUID = 1L;

    private DefinicaoProcesso definicaoProcesso;
    
    public DefinicaoProcesso getDefinicaoProcesso() {
        return definicaoProcesso;
    }
    
    public void setDefinicaoProcesso(DefinicaoProcesso definicaoProcesso) {
        this.definicaoProcesso = definicaoProcesso;
    }
}
