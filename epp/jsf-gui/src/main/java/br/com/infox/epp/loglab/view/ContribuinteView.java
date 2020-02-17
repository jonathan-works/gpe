package br.com.infox.epp.loglab.view;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.faces.FacesMessages;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.loglab.contribuinte.type.ContribuinteEnum;
import br.com.infox.epp.loglab.search.ContribuinteSolicitanteSearch;
import br.com.infox.epp.loglab.service.ContribuinteService;
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

    @Inject
    private ContribuinteService contribuinteService;

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

    @ExceptionHandled
    public void criarSolicitacaoCadastro() {
        preencherTipoContribuinte();
        alterarEstado();
        contribuinteService.criarSinalContribuinte(contribuinteVO);
        limpar();
        FacesMessages.instance().add("Solicitação de Cadastro criada com sucesso");
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

    private void preencherTipoContribuinte() {
        if (contribuinteVO == null) return;

        if (contribuinteVO.getTipoContribuinte() == null) {
            contribuinteVO.setTipoContribuinte(ContribuinteEnum.CO);
        }
    }

    private void alterarEstado() {
        if(estado != null) {
            contribuinteVO.setIdEstadoRg(estado.getId());
        } else {
            contribuinteVO.setIdEstadoRg(null);
        }
    }
}
