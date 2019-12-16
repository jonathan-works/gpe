package br.com.infox.epp.loglab.search;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.com.infox.epp.loglab.eturmalina.bean.DadosServidorBean;
import br.com.infox.epp.loglab.eturmalina.bean.DadosServidorResponseBean;
import br.com.infox.epp.loglab.eturmalina.service.ETurmalinaService;
import br.com.infox.epp.loglab.vo.ContribuinteSolicitanteVO;
import br.com.infox.epp.municipio.Estado;
import br.com.infox.epp.municipio.EstadoSearch;

@Stateless
public class ContribuinteSolicitanteSearch {

    @Inject
    private ETurmalinaService eTurmalinaService;
    @Inject
    private EstadoSearch estadoSearch;

    public List<ContribuinteSolicitanteVO> getDadosContribuinteSolicitante(String numeroCpf, String numeroMatricula){
        List<ContribuinteSolicitanteVO> contribuinteSolicitanteList = new ArrayList<ContribuinteSolicitanteVO>();
        DadosServidorBean dadosServidor = new DadosServidorBean(numeroCpf, numeroMatricula);

        List<DadosServidorResponseBean> dadosServidores = eTurmalinaService.getDadosServidor(dadosServidor);
        for (DadosServidorResponseBean dadosServidorResponseBean : dadosServidores) {
            ContribuinteSolicitanteVO contribuinteSolicitante = convertDadosServidorResponse(dadosServidorResponseBean);
            contribuinteSolicitanteList.add(contribuinteSolicitante);
        }

        return contribuinteSolicitanteList;
    }

    private ContribuinteSolicitanteVO convertDadosServidorResponse(DadosServidorResponseBean dadosServidorResponseBean) {
        ContribuinteSolicitanteVO contribuinteSolicitante = new ContribuinteSolicitanteVO();
        contribuinteSolicitante.setCpf(dadosServidorResponseBean.getCpf());
        contribuinteSolicitante.setMatricula(dadosServidorResponseBean.getMatricula());
        contribuinteSolicitante.setNomeCompleto(dadosServidorResponseBean.getNome());
        contribuinteSolicitante.setNumeroRg(dadosServidorResponseBean.getServidorRG());
        contribuinteSolicitante.setEmissorRg(dadosServidorResponseBean.getServidorRGEmissao());
        Estado estado = estadoSearch.retrieveEstadoByCodigo(dadosServidorResponseBean.getServidorRGOrgao());
        contribuinteSolicitante.setIdEstadoRg(estado != null ? estado.getId() : null);
        contribuinteSolicitante.setNomeMae(dadosServidorResponseBean.getServidorFiliacaoMae());
        contribuinteSolicitante.setCidade(null);
        contribuinteSolicitante.setLogradouro(null);
        contribuinteSolicitante.setBairro(null);
        contribuinteSolicitante.setComplemento(null);
        contribuinteSolicitante.setNumero(null);
        contribuinteSolicitante.setCep(null);

        return contribuinteSolicitante;
    }
}
