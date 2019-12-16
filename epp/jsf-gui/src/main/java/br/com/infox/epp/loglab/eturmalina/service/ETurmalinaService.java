package br.com.infox.epp.loglab.eturmalina.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.google.gson.Gson;

import br.com.infox.epp.loglab.eturmalina.ws.WSIntegracaoRH;
import br.com.infox.epp.loglab.eturmalina.ws.WSIntegracaoRHGETDADOSSERVIDOR;
import br.com.infox.epp.loglab.eturmalina.ws.WSIntegracaoRHGETDADOSSERVIDORResponse;
import br.com.infox.epp.loglab.eturmalina.ws.WSIntegracaoRHSoapPort;
import br.com.infox.epp.loglab.eturmalina.bean.DadosServidorBean;
import br.com.infox.epp.loglab.eturmalina.bean.DadosServidorResponseBean;
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
	    
	    request.setUsuario(Parametros.DS_LOGIN_USUARIO_ETURMALINA.getValue());
	    request.setSenha(Parametros.DS_SENHA_USUARIO_ETURMALINA.getValue());
	    request.setCpf(dadosServidor.getCpf());
        request.setMatricula(dadosServidor.getMatricula());
        request.setDatainicio("");
        request.setDatafim("");
	    
       return request;
	}

    public void teste() {
        DadosServidorBean dadosServidor = new DadosServidorBean();
        dadosServidor.setMatricula("4883048");
        dadosServidor.setCpf("73533548104");
        
        List<DadosServidorResponseBean> dadosResponseList = getDadosServidor(dadosServidor);
        
        for (DadosServidorResponseBean dadosServidorResponseBean : dadosResponseList) {
            System.out.println("-----------------------------------------------------------");
            System.out.println("Matricula: " + dadosServidorResponseBean.getMatricula());
            System.out.println("Nome: " + dadosServidorResponseBean.getNome());
            System.out.println("Cpf: " + dadosServidorResponseBean.getCpf());
            System.out.println("DataNomeacaoContratacao: " + dadosServidorResponseBean.getDataNomeacaoContratacao());
            System.out.println("DataPosse: " + dadosServidorResponseBean.getDataPosse());
            System.out.println("DataExercicio: " + dadosServidorResponseBean.getDataExercicio());
            System.out.println("Situacao: " + dadosServidorResponseBean.getSituacao());
            System.out.println("Status: " + dadosServidorResponseBean.getStatus());
            System.out.println("Orgao: " + dadosServidorResponseBean.getOrgao());
            System.out.println("LocalTrabalho: " + dadosServidorResponseBean.getLocalTrabalho());
            System.out.println("SubFolha: " + dadosServidorResponseBean.getSubFolha());
            System.out.println("Jornada: " + dadosServidorResponseBean.getJornada());
            System.out.println("OcupacaoCarreira: " + dadosServidorResponseBean.getOcupacaoCarreira());
            System.out.println("CargoCarreira: " + dadosServidorResponseBean.getCargoCarreira());
            System.out.println("OcupacaoComissao: " + dadosServidorResponseBean.getOcupacaoComissao());
            System.out.println("CargoComissao: " + dadosServidorResponseBean.getCargoComissao());
            System.out.println("ServidorFiliacaoPai: " + dadosServidorResponseBean.getServidorFiliacaoPai());
            System.out.println("ServidorFiliacaoMae: " + dadosServidorResponseBean.getServidorFiliacaoMae());
            System.out.println("ServidorRG: " + dadosServidorResponseBean.getServidorRG());
            System.out.println("ServidorDataNascimento: " + dadosServidorResponseBean.getServidorDataNascimento());
            System.out.println("ServidorRGEmissao: " + dadosServidorResponseBean.getServidorRGEmissao());
            System.out.println("ServidorRGOrgao: " + dadosServidorResponseBean.getServidorRGOrgao());
            System.out.println("-----------------------------------------------------------");
        }
    }
}
