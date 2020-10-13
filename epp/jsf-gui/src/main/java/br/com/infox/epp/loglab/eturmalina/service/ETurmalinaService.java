package br.com.infox.epp.loglab.eturmalina.service;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import br.com.infox.core.exception.EppConfigurationException;
import br.com.infox.core.util.ObjectUtil;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.loglab.eturmalina.bean.DadosServidorBean;
import br.com.infox.epp.loglab.eturmalina.bean.DadosServidorResponseBean;
import br.com.infox.epp.loglab.eturmalina.ws.WSIntegracaoRH;
import br.com.infox.epp.loglab.eturmalina.ws.WSIntegracaoRHGETDADOSSERVIDOR;
import br.com.infox.epp.loglab.eturmalina.ws.WSIntegracaoRHGETDADOSSERVIDORMATRICULA;
import br.com.infox.epp.loglab.eturmalina.ws.WSIntegracaoRHGETDADOSSERVIDORMATRICULAResponse;
import br.com.infox.epp.loglab.eturmalina.ws.WSIntegracaoRHGETDADOSSERVIDORResponse;
import br.com.infox.epp.loglab.eturmalina.ws.WSIntegracaoRHSoapPort;
import br.com.infox.epp.system.Parametros;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ETurmalinaService implements Serializable{

	private static final long serialVersionUID = 1L;

    public List<DadosServidorResponseBean> getDadosServidor(DadosServidorBean dadosServidor, Boolean emExercicio) {
        List<DadosServidorResponseBean> dadosResponseList = new ArrayList<>();
        if (StringUtil.isEmpty(dadosServidor.getMatricula()) && !StringUtil.isEmpty(dadosServidor.getMatricula())) {
            WSIntegracaoRHGETDADOSSERVIDORMATRICULAResponse response = getServidorMatriculaResponse(dadosServidor);
            dadosResponseList.add(emExercicio ? getServidorEmExercicio(response) : getServidor(response));
        } else {
            WSIntegracaoRHGETDADOSSERVIDORResponse response = getServidorResponse(dadosServidor);
            dadosResponseList.addAll(emExercicio ? getServidoresEmExercicio(response) : getServidores(response));
        }
        return dadosResponseList;
    }

    private WSIntegracaoRHGETDADOSSERVIDORMATRICULAResponse getServidorMatriculaResponse(DadosServidorBean dadosServidor) {
        WSIntegracaoRHGETDADOSSERVIDORMATRICULA request = (WSIntegracaoRHGETDADOSSERVIDORMATRICULA) criarDadosServidor(dadosServidor,
                WSIntegracaoRHGETDADOSSERVIDORMATRICULA.class);
        WSIntegracaoRHSoapPort service = inicializarServico(dadosServidor);
        WSIntegracaoRHGETDADOSSERVIDORMATRICULAResponse response = service.getdadosservidormatricula(request);
        return response;
    }

    private WSIntegracaoRHGETDADOSSERVIDORResponse getServidorResponse(DadosServidorBean dadosServidor) {
        WSIntegracaoRHGETDADOSSERVIDOR request = (WSIntegracaoRHGETDADOSSERVIDOR) criarDadosServidor(dadosServidor,
                WSIntegracaoRHGETDADOSSERVIDOR.class);
        WSIntegracaoRHSoapPort service = inicializarServico(dadosServidor);
        WSIntegracaoRHGETDADOSSERVIDORResponse response = service.getdadosservidor(request);
        return response;
    }

    private List<DadosServidorResponseBean> getServidores(WSIntegracaoRHGETDADOSSERVIDORResponse retornoWs) {
        List<DadosServidorResponseBean> servidoresList = new ArrayList<DadosServidorResponseBean>();
        try {
            if (!StringUtil.isEmpty(retornoWs.getRetorno())) {
                Gson gson = new Gson();
                List<DadosServidorResponseBean> dadosRetorno = gson.fromJson(retornoWs.getRetorno(), DadosServidorResponseBean.getListType());
                for (DadosServidorResponseBean dadosServidorResponse : dadosRetorno) {
                    if (!StringUtil.isEmpty(dadosServidorResponse.getMatricula())) {
                        DadosServidorBean dadosServidor = new DadosServidorBean(dadosServidorResponse.getCpf(), dadosServidorResponse.getMatricula());
                        WSIntegracaoRHGETDADOSSERVIDORMATRICULAResponse response = getServidorMatriculaResponse(dadosServidor);
                        DadosServidorResponseBean dadosServidorResponseBean = getServidor(response);
                        if (dadosServidorResponseBean != null)
                            servidoresList.add(dadosServidorResponseBean);
                    } else {
                        servidoresList.add(dadosServidorResponse);
                    }
                }
            }
        } catch (JsonSyntaxException e) {
            return servidoresList;
        }
        return servidoresList;
    }

    private List<DadosServidorResponseBean> getServidoresEmExercicio(WSIntegracaoRHGETDADOSSERVIDORResponse retornoWs) {
        List<DadosServidorResponseBean> servidoresEmExercicioList = new ArrayList<DadosServidorResponseBean>();

        List<DadosServidorResponseBean> dadosRetorno = getServidores(retornoWs);
        if (!ObjectUtil.isEmpty(dadosRetorno)) {
            for (DadosServidorResponseBean dadosServidorResponse : dadosRetorno) {
                if (servidorEmExercicio(dadosServidorResponse.getStatus().trim())) {
                    servidoresEmExercicioList.add(dadosServidorResponse);
                }
            }
        }
        return servidoresEmExercicioList;
    }

    private DadosServidorResponseBean getServidor(WSIntegracaoRHGETDADOSSERVIDORMATRICULAResponse retornoWs) {
        DadosServidorResponseBean dadosServidorResponseBean = null;
        try {
            if (!StringUtil.isEmpty(retornoWs.getRetorno())) {
                Gson gson = new Gson();
                List<DadosServidorResponseBean> dadosRetorno = gson.fromJson(retornoWs.getRetorno(), DadosServidorResponseBean.getListType());
                DadosServidorResponseBean dadosServidorResponse = dadosRetorno.stream().findFirst().orElse(null);
                if (dadosServidorResponse != null)
                    dadosServidorResponseBean = dadosServidorResponse;
            }
        } catch (JsonSyntaxException e) {
            return dadosServidorResponseBean;
        }
        return dadosServidorResponseBean;
    }

    private DadosServidorResponseBean getServidorEmExercicio(WSIntegracaoRHGETDADOSSERVIDORMATRICULAResponse retornoWs) {
        DadosServidorResponseBean dadosServidorResponseBean = getServidor(retornoWs);
        if (!ObjectUtil.isEmpty(dadosServidorResponseBean)) {
            if (servidorEmExercicio(dadosServidorResponseBean.getStatus().trim()))
                return dadosServidorResponseBean;
        }
        return dadosServidorResponseBean;
    }

	private <T> Object criarDadosServidor(DadosServidorBean dadosServidor, Class<T> classe) {
	    if (WSIntegracaoRHGETDADOSSERVIDORMATRICULA.class.getName().equals(classe.getName())) {
            WSIntegracaoRHGETDADOSSERVIDORMATRICULA request = new WSIntegracaoRHGETDADOSSERVIDORMATRICULA();
            request.setUsuario(Parametros.DS_LOGIN_USUARIO_ETURMALINA.getValue());
            request.setSenha(Parametros.DS_SENHA_USUARIO_ETURMALINA.getValue());
            request.setMatricula(dadosServidor.getMatricula());
            return request;
	    } else {
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

    public boolean servidorEmExercicio(String status) {
        if (StringUtil.isEmpty(status))
            return Boolean.FALSE;
        return "EM EXERCÍCIO".equalsIgnoreCase(status)  || "EM EXERCICIO".equalsIgnoreCase(status);
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

    private WSIntegracaoRHSoapPort inicializarServico(DadosServidorBean dadosServidor) {
        validarParametros();
        try {
            String parameterUrl = Parametros.DS_URL_SERVICO_ETURMALINA.getValue();
			URL url = new URL(parameterUrl);
            WSIntegracaoRH wsIntegracao = new WSIntegracaoRH(url);
            WSIntegracaoRHSoapPort service = wsIntegracao.getWSIntegracaoRHSoapPort();
            BindingProvider bp = (BindingProvider) service;
    		bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, parameterUrl);
            return service;
        } catch (MalformedURLException m) {
            throw new EppConfigurationException("URL inválida.");
        } catch (WebServiceException we) {
            throw new EppConfigurationException("Falha ao tentar acessar serviço de consulta do e-TURMALINA.");
        }
    }

}
