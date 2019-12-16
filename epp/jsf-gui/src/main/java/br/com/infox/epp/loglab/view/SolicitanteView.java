package br.com.infox.epp.loglab.view;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.cdi.exception.ExceptionHandled.MethodType;
import br.com.infox.epp.loglab.vo.ContribuinteSolicitanteVO;
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
    private ContribuinteSolicitanteVO solicitanteVO;

    @PostConstruct
    protected void init() {
    	this.solicitanteVO = new ContribuinteSolicitanteVO();
	}

    public void consultarTurmalina() {
    }

    public void novo() {
        this.solicitanteVO = new ContribuinteSolicitanteVO();
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
