package br.com.infox.epp.loglab.view;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.loglab.search.ContribuinteSolicitanteSearch;
import br.com.infox.epp.loglab.vo.ContribuinteSolicitanteVO;
import br.com.infox.epp.municipio.Estado;
import br.com.infox.epp.municipio.EstadoSearch;
import br.com.infox.jsf.util.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class ContribuinteView implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ContribuinteSolicitanteSearch contribuinteSolicitanteSearch;
    @Inject
    private EstadoSearch estadoSearch;

    @Getter
    @Setter
    private ContribuinteSolicitanteVO contribuinteVO;
    @Getter
    @Setter
    private Estado estado;
    @Getter
    @Setter
    private String numeroCpf;
    @Getter
    @Setter
    private String numeroMatricula;
    @Getter
    @Setter
    private List<ContribuinteSolicitanteVO> contribuinteSolicitanteList;

    @PostConstruct
    protected void init() {
    	limpar();
	}

    public void consultarTurmalina() {
    	if (numeroCpf != null) {
    		contribuinteSolicitanteList = contribuinteSolicitanteSearch.getDadosContribuinteSolicitante(numeroCpf, numeroMatricula);
            JsfUtil.instance().execute("PF('listaContribuintesDialog').show();");
    	}
    }

    public List<Estado> getEstadosList() {
        List<Estado> estadosList = estadoSearch.findAll();
        return estadosList;
    }

    public void limpar() {
    	contribuinteVO = null;
    	estado = null;
    	numeroCpf = null;
    	numeroMatricula = null;
    	contribuinteSolicitanteList = null;
    }

}
