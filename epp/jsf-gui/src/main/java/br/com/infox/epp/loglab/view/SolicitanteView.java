package br.com.infox.epp.loglab.view;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.cdi.exception.ExceptionHandled.MethodType;
import br.com.infox.epp.loglab.contribuinte.type.ContribuinteEnum;
import br.com.infox.epp.loglab.search.ContribuinteSolicitanteSearch;
import br.com.infox.epp.loglab.service.ContribuinteSolicitanteService;
import br.com.infox.epp.loglab.vo.ContribuinteSolicitanteVO;
import br.com.infox.epp.municipio.Estado;
import br.com.infox.epp.municipio.EstadoSearch;
import br.com.infox.jsf.util.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class SolicitanteView implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ContribuinteSolicitanteService contribuinteSolicitanteService;
    @Inject
    private ContribuinteSolicitanteSearch contribuinteSolicitanteSearch;
    @Inject
    private EstadoSearch estadoSearch;

    @Getter
    @Setter
    private ContribuinteSolicitanteVO solicitanteVO;
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
            contribuinteSolicitanteList = contribuinteSolicitanteSearch.getDadosContribuinteSolicitante(numeroCpf, numeroMatricula, ContribuinteEnum.SO);
            JsfUtil.instance().execute("PF('listaContribuintesDialog').show();");
        }
    }

    public void novo() {
        limpar();
    }

    @ExceptionHandled(MethodType.PERSIST)
    public void gravar() {
        preencherTipoContribuinte();
        alterarEstado();
        contribuinteSolicitanteService.gravar(solicitanteVO);
    }

    @ExceptionHandled(MethodType.UPDATE)
    public void atualizar() {
        preencherTipoContribuinte();
        alterarEstado();
        contribuinteSolicitanteService.gravar(solicitanteVO);
    }

    public List<Estado> getEstadosList() {
        List<Estado> estadosList = estadoSearch.findAll();
        return estadosList;
    }

    private void preencherTipoContribuinte() {
        if (solicitanteVO == null) return;

        if (solicitanteVO.getTipoContribuinte() == null) {
            solicitanteVO.setTipoContribuinte(ContribuinteEnum.SO);
        }
    }

    private void alterarEstado() {
        if(estado != null) {
            solicitanteVO.setIdEstadoRg(estado.getId());
        } else {
            solicitanteVO.setIdEstadoRg(null);
        }
    }

    public void limpar() {
        solicitanteVO = null;
        estado = null;
        numeroCpf = null;
        numeroMatricula = null;
        contribuinteSolicitanteList = null;
    }

}
