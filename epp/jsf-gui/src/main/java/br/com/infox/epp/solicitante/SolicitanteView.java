package br.com.infox.epp.solicitante;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.cdi.exception.ExceptionHandled.MethodType;
import br.com.infox.epp.municipio.Estado;
import br.com.infox.epp.municipio.EstadoSearch;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class SolicitanteView implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private EstadoSearch estadoSearch;

    @Getter
    @Setter
    private SolicitanteVO solicitanteVO;

    @PostConstruct
    protected void init() {
    	this.solicitanteVO = new SolicitanteVO();
	}

    public void consultarTurmalina() {
    }

    public void novo() {
        this.solicitanteVO = new SolicitanteVO();
    }

    @ExceptionHandled(MethodType.PERSIST)
    public void gravar() {
    }

    @ExceptionHandled(MethodType.UPDATE)
    public void atualizar() {
    }

    public List<Estado> getEstadosList() {
        List<Estado> estadosList = estadoSearch.findAll();
        return estadosList;
    }

}
