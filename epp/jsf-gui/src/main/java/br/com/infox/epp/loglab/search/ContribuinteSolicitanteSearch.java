package br.com.infox.epp.loglab.search;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.com.infox.epp.loglab.eturmalina.bean.DadosServidorBean;
import br.com.infox.epp.loglab.eturmalina.bean.DadosServidorResponseBean;
import br.com.infox.epp.loglab.eturmalina.service.ETurmalinaService;
import br.com.infox.epp.loglab.vo.ContribuinteSolicitanteVO;
import br.com.infox.epp.loglab.vo.EnderecoVO;

@Stateless
public class ContribuinteSolicitanteSearch {
    
    @Inject
    private ETurmalinaService eTurmalinaService;
    
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
        contribuinteSolicitante.setUfRg(dadosServidorResponseBean.getServidorRGOrgao());
        contribuinteSolicitante.setNomeMae(dadosServidorResponseBean.getServidorFiliacaoMae());
        contribuinteSolicitante.setEndereco(new EnderecoVO());
        
        return contribuinteSolicitante;
    }
}
