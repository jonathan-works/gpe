package br.com.infox.epp.loglab.eturmalina.service;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.ws.WebServiceException;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import br.com.infox.core.exception.EppConfigurationException;
import br.com.infox.core.util.StringUtil;
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

    public List<DadosServidorResponseBean> getDadosServidor(DadosServidorBean dadosServidor) {
    	validarParametros();
        try {
            WSIntegracaoRHGETDADOSSERVIDOR request = criarDadosServidor(dadosServidor);
            URL url = new URL(Parametros.DS_URL_SERVICO_ETURMALINA.getValue());
            WSIntegracaoRH wsIntegracao = new WSIntegracaoRH(url);
            WSIntegracaoRHSoapPort service = wsIntegracao.getWSIntegracaoRHSoapPort();
            WSIntegracaoRHGETDADOSSERVIDORResponse response;
            response = service.getdadosservidor(request);

            List<DadosServidorResponseBean> dadosResponseList = getServidoresEmExercicio(response);
            return dadosResponseList;
		} catch (MalformedURLException m) {
            throw new EppConfigurationException("URL inválida.");
		} catch (WebServiceException we) {
            throw new EppConfigurationException("Falha ao tentar acessar serviço de consulta do e-TURMALINA.");
		}
    }

    private List<DadosServidorResponseBean> getServidoresEmExercicio(WSIntegracaoRHGETDADOSSERVIDORResponse retornoWs) {
        List<DadosServidorResponseBean> servidoresEmExercicioList = new ArrayList<DadosServidorResponseBean>();
        try {
            if (retornoWs.getRetorno() != null){
                Gson gson = new Gson();
                List<DadosServidorResponseBean> dadosRetorno = gson.fromJson(retornoWs.getRetorno(), DadosServidorResponseBean.getListType());

                for (DadosServidorResponseBean dadosServidorResponse : dadosRetorno) {
                    if (dadosServidorResponse.getStatus().equalsIgnoreCase("EM EXERCÍCIO")
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

	private void validarParametros( ) {
		String msg = "O parâmetro '%s' não foi preenchido.";
    	if(StringUtil.isEmpty(Parametros.DS_LOGIN_USUARIO_ETURMALINA.getValue())) {
    		throw new EppConfigurationException(String.format(msg, Parametros.DS_LOGIN_USUARIO_ETURMALINA.getParametroDefinition().getNome()));
    	}

    	if(StringUtil.isEmpty(Parametros.DS_SENHA_USUARIO_ETURMALINA.getValue())) {
    		throw new EppConfigurationException(String.format(msg, Parametros.DS_SENHA_USUARIO_ETURMALINA.getParametroDefinition().getNome()));
    	}

    	if(StringUtil.isEmpty(Parametros.DS_URL_SERVICO_ETURMALINA.getValue())) {
    		throw new EppConfigurationException(String.format(msg, Parametros.DS_URL_SERVICO_ETURMALINA.getParametroDefinition().getNome()));
    	}
	}

}
