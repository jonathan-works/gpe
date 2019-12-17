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
    
    @Inject
    private UsuarioSearch usuarioSearch;
    
    public List<ServidorVO> getDadosServidor(String numeroCpf){
        List<ServidorVO> servidorList = new ArrayList<ServidorVO>();
        DadosServidorBean dadosServidor = new DadosServidorBean(numeroCpf);
        
        List<DadosServidorResponseBean> dadosServidores = eTurmalinaService.getDadosServidor(dadosServidor);
        for (DadosServidorResponseBean dadosServidorResponseBean : dadosServidores) {
            ServidorVO servidor = convertDadosServidorResponse(dadosServidorResponseBean);
            
            servidorList.add(servidor);
        }
        
        return servidorList;
    }
    
    public boolean isExisteUsuarioServidor(String numeroCpf){
        return usuarioSearch.isExisteUsuarioByNumeroCpf(numeroCpf);
    }
    
    private ServidorVO convertDadosServidorResponse(DadosServidorResponseBean dadosServidorResponseBean) {
        ServidorVO servidor = new ServidorVO();
        servidor.setCpf(dadosServidorResponseBean.getCpf());
        servidor.setNomeCompleto(dadosServidorResponseBean.getNome());
        servidor.setCargoFuncao(dadosServidorResponseBean.getCargoCarreira());
        servidor.setDepartamento(dadosServidorResponseBean.getLocalTrabalho());
        servidor.setSecretaria(dadosServidorResponseBean.getOrgao());

        return servidor;
    }
}
