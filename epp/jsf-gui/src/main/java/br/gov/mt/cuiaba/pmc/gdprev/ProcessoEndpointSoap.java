package br.gov.mt.cuiaba.pmc.gdprev;

import java.util.List;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jws.WebService;
import javax.mail.util.ByteArrayDataSource;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.soap.MTOM;

import br.com.infox.core.util.DateUtil;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;

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
	        List<DocumentoBin> documentos = processoEndpointSearch.getListaDocumentoBinByNrProcesso(numeroDoProcesso);
	
	        byte[] data = processoEndpointService.gerarPDFProcesso(numeroDoProcesso, documentos);
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
