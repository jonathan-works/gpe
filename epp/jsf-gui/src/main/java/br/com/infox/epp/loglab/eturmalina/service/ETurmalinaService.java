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
        print("4039888", "00156347105");
        print("4038994", "85550620120");
        print("4007282", "01293453188");
        print("4038919", "37796682115");
        print("2557833", "28390709104");
        print("4008072", "02488723118");
        print("4857268", "65421221172");
        print("4855334", "63197871120");
        print("4040906", "25801325115");
        print("4032096", "79554970153");
        print("4855391", "02370114177");
        print("4045867", "03878630107");
        print("2575513", "54527252100");
        print("2965047", "79278108120");
        print("4038365", "69400970153");
        print("4877721", "27869113972");
        print("4039008", "76794652820");
        print("4028699", "76794652820");
        print("4040686", "53641353149");
        print("2979414", "04825802168");
        print("2575719", "54435609134");
        print("2576842", "29277043172");
        print("2577959", "35379081134");
        print("4038453", "00951750160");
        print("4854152", "72556005149");
        print("4041661", "72556005149");
        print("4044801", "00187675163");
        print("4856382", "73087572187");
        print("2968727", "62097636187");
        print("4036708", "03634169192");
        print("2576835", "54476690149");
        print("4047574", "04610423120");
        print("4039255", "01477101110");
        print("4882006", "16185595168");
        print("2586231", "31836038100");
        print("2576846", "47472502120");
        print("2021186", "20737947187");
        print("4027992", "51341620182");
        print("4029479", "47432594104");
        print("4048697", "46062394172");
        print("2973986", "32952104115");
        print("2975570", "20742533115");
        print("4036129", "68943822120");
        print("4036204", "00094100152");
        print("4036199", "48794805172");
        print("4036205", "70127360115");
        print("4036707", "12750435854");
        print("4848461", "32605455149");
        print("4021858", "01683657152");
        print("4045105", "39360687120");
    }

    public void print(String matricula, String cpf) {

        DadosServidorBean dadosServidor = new DadosServidorBean();
        dadosServidor.setMatricula(matricula);
        dadosServidor.setCpf(cpf);

        System.out.println("***********************************************************");
        System.out.println("Matricula: " + matricula);
        System.out.println("Cpf: " + cpf);
        
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
        System.out.println("***********************************************************");
        
    }
}
