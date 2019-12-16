package br.com.infox.epp.loglab.search;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.com.infox.epp.loglab.eturmalina.bean.DadosServidorBean;
import br.com.infox.epp.loglab.eturmalina.bean.DadosServidorResponseBean;
import br.com.infox.epp.loglab.eturmalina.service.ETurmalinaService;
import br.com.infox.epp.loglab.vo.ServidorVO;

@Stateless
public class ServidorSearch {
    
    @Inject
    private ETurmalinaService eTurmalinaService;
    
    public List<ServidorVO> getDadosContribuinteSolicitante(String numeroCpf){
        
        List<ServidorVO> servidorList = new ArrayList<ServidorVO>();
        
        DadosServidorBean dadosServidor = new DadosServidorBean(numeroCpf);
        
        List<DadosServidorResponseBean> dadosServidores = eTurmalinaService.getDadosServidor(dadosServidor);
        
        for (DadosServidorResponseBean dadosServidorResponseBean : dadosServidores) {
            ServidorVO servidor = new ServidorVO();
            
            //TODO: IMPLEMENTAR CONVERSAO
        }
        
        return servidorList;
    }
}
