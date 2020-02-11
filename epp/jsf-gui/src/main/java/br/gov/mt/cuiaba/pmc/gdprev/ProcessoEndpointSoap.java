package br.gov.mt.cuiaba.pmc.gdprev;

import java.util.List;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jws.WebService;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.soap.MTOM;

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
        processoEndpointService.autenticar(username, password);
        List<DocumentoBin> documentos = processoEndpointSearch.getListaDocumentoBinByNrProcesso(numeroDoProcesso);

        byte[] data = processoEndpointService.gerarPDFProcesso(numeroDoProcesso, documentos);
        DataSource ds = new ByteArrayDataSource(data, "application/pdf");
        DataHandler dataHandler = new DataHandler(ds);
        return new Documento(dataHandler);
    }

    @Override
    public Processos consultarProcessos(String username, String password, String dataAlteracao) {
        processoEndpointService.autenticar(username, password);
        List<Processo> processos = processoEndpointSearch.getListaProcesso();
        return new Processos(processos);
    }

}
