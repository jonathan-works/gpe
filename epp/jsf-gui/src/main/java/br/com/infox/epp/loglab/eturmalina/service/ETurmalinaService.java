package br.com.infox.epp.loglab.eturmalina.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import br.com.infox.epp.loglab.eturmalina.bean.DadosServidorBean;
import br.com.infox.epp.loglab.eturmalina.bean.DadosServidorResponseBean;
import br.com.infox.epp.loglab.eturmalina.ws.WSIntegracaoRH;
import br.com.infox.epp.loglab.eturmalina.ws.WSIntegracaoRHGETDADOSSERVIDOR;
import br.com.infox.epp.loglab.eturmalina.ws.WSIntegracaoRHGETDADOSSERVIDORResponse;
import br.com.infox.epp.loglab.eturmalina.ws.WSIntegracaoRHSoapPort;
import br.com.infox.epp.system.Parametros;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ETurmalinaService implements Serializable{

	private static final long serialVersionUID = 1L;
	
    public List<DadosServidorResponseBean> getDadosServidor(DadosServidorBean dadosServidor){
        WSIntegracaoRHGETDADOSSERVIDOR request = criarDadosServidor(dadosServidor);

        WSIntegracaoRH wsIntegracao = new WSIntegracaoRH();
        WSIntegracaoRHSoapPort service = wsIntegracao.getWSIntegracaoRHSoapPort();
        WSIntegracaoRHGETDADOSSERVIDORResponse response;
        response = service.getdadosservidor(request);
    
        List<DadosServidorResponseBean> dadosResponseList = getServidoresEmExercicio(response);
        
        return dadosResponseList;
    }
    
    private List<DadosServidorResponseBean> getServidoresEmExercicio(WSIntegracaoRHGETDADOSSERVIDORResponse retornoWs) {
        List<DadosServidorResponseBean> servidoresEmExercicioList = new ArrayList<DadosServidorResponseBean>();
        
        try {
            if(retornoWs.getRetorno() != null){
                Gson gson = new Gson();
                List<DadosServidorResponseBean> dadosRetorno = gson.fromJson(retornoWs.getRetorno(), DadosServidorResponseBean.getListType());
                
                for (DadosServidorResponseBean dadosServidorResponse : dadosRetorno) {
                    if(dadosServidorResponse.getStatus().equalsIgnoreCase("EM EXERCÍCIO")
                            || dadosServidorResponse.getStatus().equalsIgnoreCase("EM EXERCICIO")){
                        servidoresEmExercicioList.add(dadosServidorResponse);
                    }
                }
            }
        }catch (JsonSyntaxException e) {
            return servidoresEmExercicioList;
        }
        
        return servidoresEmExercicioList;
    }
    
	private WSIntegracaoRHGETDADOSSERVIDOR criarDadosServidor(DadosServidorBean dadosServidor) {
	    WSIntegracaoRHGETDADOSSERVIDOR request = new WSIntegracaoRHGETDADOSSERVIDOR();
	    
	    request.setUsuario(Parametros.DS_LOGIN_USUARIO_ETURMALINA.getValue());
	    request.setSenha(Parametros.DS_SENHA_USUARIO_ETURMALINA.getValue());
	    request.setCpf(dadosServidor.getCpf());
        request.setMatricula(dadosServidor.getMatricula());
        request.setDatainicio("");
        request.setDatafim("");
	    
       return request;
	}
}
