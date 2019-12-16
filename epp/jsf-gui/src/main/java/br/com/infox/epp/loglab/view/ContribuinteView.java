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
public class ContribuinteView implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private EstadoSearch estadoSearch;

    @Getter
    @Setter
    private ContribuinteSolicitanteVO contribuinteVO;

    @PostConstruct
    protected void init() {
    	this.contribuinteVO = new ContribuinteSolicitanteVO();
	}

    public void consultarTurmalina() {
    }

    @ExceptionHandled(MethodType.PERSIST)
    public void gravar() {
    }

    public List<Estado> getEstadosList() {
        List<Estado> estadosList = estadoSearch.findAll();
        return estadosList;
    }

}
