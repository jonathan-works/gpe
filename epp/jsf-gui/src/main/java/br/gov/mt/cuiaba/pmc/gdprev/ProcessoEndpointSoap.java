package br.gov.mt.cuiaba.pmc.gdprev;

import java.util.List;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jws.WebService;
import javax.mail.util.ByteArrayDataSource;
import javax.ws.rs.core.Response.Status;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.soap.MTOM;

import br.com.infox.core.util.DateUtil;

@WebService(name = "ProcessoEndpoint")
public class ProcessoEndpointSoap implements ProcessoEndpoint {

    @Resource
    private WebServiceContext wsContext;

    @Inject
    private ProcessoEndpointSearch processoEndpointSearch;

    @Inject
    private ProcessoEndpointService processoEndpointService;

    @Override
    @MTOM(enabled = true, threshold = 10240)
    public Documento recuperarProcessoEmDocumento(String username, String password, String numeroDoProcesso) {
    	try {
    		processoEndpointService.autenticar(username, password);
            ProcessoDTO processoDTO = processoEndpointSearch.getProcessoDTOByNrProcesso(numeroDoProcesso);
            if (processoDTO == null) {
                throw new WebServiceException(Status.NOT_FOUND.getStatusCode(), "HTTP" + Status.NOT_FOUND.getStatusCode(),
                        "Processo não encontrado");
            }
            List<MovimentacaoGroup> groups = processoEndpointSearch
                    .getListaMovimentacaoGroupByIdProcesso(processoDTO.getId());

            byte[] data = processoEndpointService.gerarPDFProcesso(processoDTO, groups);
            DataSource ds = new ByteArrayDataSource(data, "application/pdf");
            DataHandler dataHandler = new DataHandler(ds);
            return new Documento(dataHandler);
    	} catch(WebServiceException e) {
    		throw e;
    	} catch(Throwable e) {
    		final Status status = Status.INTERNAL_SERVER_ERROR;
			throw new WebServiceException(status.getStatusCode(), "HTTP"+status.getStatusCode(), "Erro Inesperado");
    	}
    }

    @Override
    public Processos consultarProcessos(String username, String password, String dataAlteracao) {
    	try {
	        processoEndpointService.autenticar(username, password);
	        List<Processo> processos = processoEndpointSearch.getListaProcesso(DateUtil.parseDate(dataAlteracao, "dd/MM/yyyy"));
	        return new Processos(processos);
    	} catch(WebServiceException e) {
    		throw e;
    	} catch(Throwable e) {
    		final Status status = Status.INTERNAL_SERVER_ERROR;
			throw new WebServiceException(status.getStatusCode(), "HTTP"+status.getStatusCode(), "Erro Inesperado");
    	}
    }

}
