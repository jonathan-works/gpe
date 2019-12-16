package br.com.infox.epp.loglab.search;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.com.infox.epp.loglab.eturmalina.bean.DadosServidorBean;
import br.com.infox.epp.loglab.eturmalina.bean.DadosServidorResponseBean;
import br.com.infox.epp.loglab.eturmalina.service.ETurmalinaService;
import br.com.infox.epp.loglab.vo.ContribuinteSolicitanteVO;

@Stateless
public class ContribuinteSolicitanteSearch {
    
    @Inject
    private ETurmalinaService eTurmalinaService;
    
    public List<ContribuinteSolicitanteVO> getDadosContribuinteSolicitante(String numeroCpf, String numeroMatricula){
        
        List<ContribuinteSolicitanteVO> contribuinteSolicitanteList = new ArrayList<ContribuinteSolicitanteVO>();
        
        DadosServidorBean dadosServidor = new DadosServidorBean(numeroCpf, numeroMatricula);
        
        List<DadosServidorResponseBean> dadosServidores = eTurmalinaService.getDadosServidor(dadosServidor);
        
        for (DadosServidorResponseBean dadosServidorResponseBean : dadosServidores) {
            ContribuinteSolicitanteVO contribuinteSolicitante = new ContribuinteSolicitanteVO();
            
            //TODO: IMPLEMENTAR CONVERSAO
        }
        
        return contribuinteSolicitanteList;
    }
}
