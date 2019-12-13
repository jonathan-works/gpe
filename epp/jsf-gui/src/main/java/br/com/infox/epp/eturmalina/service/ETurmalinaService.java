package br.com.infox.epp.eturmalina.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.google.gson.Gson;

import br.com.infox.epp.eturmalina.bean.DadosServidorBean;
import br.com.infox.epp.eturmalina.bean.DadosServidorResponseBean;
import br.com.infox.epp.eturmalina.ws.WSIntegracaoRH;
import br.com.infox.epp.eturmalina.ws.WSIntegracaoRHGETDADOSSERVIDOR;
import br.com.infox.epp.eturmalina.ws.WSIntegracaoRHGETDADOSSERVIDORResponse;
import br.com.infox.epp.eturmalina.ws.WSIntegracaoRHSoapPort;
import br.com.infox.epp.system.Parametros;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ETurmalinaService implements Serializable{

	private static final long serialVersionUID = 1L;
	
    public List<DadosServidorResponseBean> getDadosServidor(DadosServidorBean dadosServidor){
        Gson gson = new Gson();
        
        List<DadosServidorResponseBean> dadosResponseList = new ArrayList<DadosServidorResponseBean>();
        
        WSIntegracaoRHGETDADOSSERVIDOR request = criarDadosServidor(dadosServidor);

        WSIntegracaoRH wsIntegracao = new WSIntegracaoRH();
        WSIntegracaoRHSoapPort service = wsIntegracao.getWSIntegracaoRHSoapPort();
        WSIntegracaoRHGETDADOSSERVIDORResponse response;
        response = service.getdadosservidor(request);
    
        if(response.getRetorno() != null){
            dadosResponseList = gson.fromJson(response.getRetorno(), DadosServidorResponseBean.getListType());
        }
        
        return dadosResponseList;
    }
    
	private WSIntegracaoRHGETDADOSSERVIDOR criarDadosServidor(DadosServidorBean dadosServidor) {
	    WSIntegracaoRHGETDADOSSERVIDOR request = new WSIntegracaoRHGETDADOSSERVIDOR();
	    
	    request.setUsuario(Parametros.DS_LOGIN_USUARIO_ETURMALINA.getLabel());
	    request.setSenha(Parametros.DS_SENHA_USUARIO_ETURMALINA.getLabel());
	    request.setCpf(dadosServidor.getCpf());
	    request.setMatricula(dadosServidor.getMatricula());
	    
       return request;
	}
}
