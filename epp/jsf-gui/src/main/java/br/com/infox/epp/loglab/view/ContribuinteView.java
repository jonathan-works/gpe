package br.com.infox.epp.loglab.view;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.cdi.exception.ExceptionHandled.MethodType;
import br.com.infox.epp.loglab.search.ContribuinteSolicitanteSearch;
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
    @Inject
    private ContribuinteSolicitanteSearch contribuinteSolicitanteSearch;

    @Getter @Setter
    private String numeroCpf;
    @Getter @Setter
    private String numeroMatricula;
    @Getter @Setter
    private List<ContribuinteSolicitanteVO> contribuinteSolicitanteList;  
    
    @Getter @Setter
    private ContribuinteSolicitanteVO contribuinte;

    @PostConstruct
    protected void init() {
        numeroCpf = null;
        numeroMatricula = null;
        contribuinte = null;
        contribuinteSolicitanteList = null;
	}

    public void consultarTurmalina() {
        contribuinteSolicitanteList = contribuinteSolicitanteSearch.getDadosContribuinteSolicitante(numeroCpf, numeroMatricula);
    }

    @ExceptionHandled(MethodType.PERSIST)
    public void gravar() {
    }

    public List<Estado> getEstadosList() {
        List<Estado> estadosList = estadoSearch.findAll();
        return estadosList;
    }
}
