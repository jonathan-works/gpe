package br.com.infox.epp.gdprev.vidafuncional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.ws.rs.core.HttpHeaders;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import br.com.infox.core.exception.EppConfigurationException;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.entity.DocumentoBin_;
import br.com.infox.epp.processo.documento.entity.Documento_;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.entity.Pasta_;
import br.com.infox.epp.processo.entity.Processo_;
import br.com.infox.epp.system.Parametros;
import br.com.infox.epp.system.manager.ParametroManager;
import br.com.infox.epp.webservice.log.entity.LogWebserviceClient;
import br.com.infox.epp.webservice.log.manager.LogWebserviceClientManager;
import br.com.infox.epp.ws.messages.CodigosServicos;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.BusinessRollbackException;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class VidaFuncionalGDPrevSearch extends PersistenceController {

    private static final LogProvider LOG = Logging.getLogProvider(VidaFuncionalGDPrevSearch.class);

    private static final String FIELD_DOCUMENTOS = "documentos";
    private static final String FIELD_PAGINA = "pagina";
    private static final String FIELD_TOTAL = "total";
    private static final String FIELD_NOME_SERVIDOR = "campo->Nome do servidor";
    private static final String FIELD_TEMPORALIDADE = "temporalidade_real";
    private static final String FIELD_MATRICULA_SERVIDOR = "campo->Matrícula do servidor";
    private static final String FIELD_ID_DOCUMENTO = "id";
    private static final String FIELD_FONTE = "campo->Fonte";
    private static final String FIELD_DATA = "data";
    private static final String FIELD_CPF_SERVIDOR = "campo->CPF do Servidor";

    private static final String CONSULTA_DOCUMENTOS_PATH = "cuiaba/search/index";
    private static final String DOWNLOAD_PDF_PATH = "geracaopdf/download/";

    private static final String PARAM_PAGINA = FIELD_PAGINA;
    private static final String PARAM_RESULTADOS_POR_PAGINA = "por_pagina";
    private static final String FILTRO_CPF = "cpf";
    private static final String FILTRO_MATRICULA = "matricula";
    private static final String FILTRO_NOME = "nome";

    @Inject
    private ParametroManager parametroManager;
    @Inject
    private LogWebserviceClientManager logWebserviceClientManager;

    public VidaFuncionalGDPrevResponseDTO getDocumentos(FiltroVidaFuncionalGDPrev filtros, Integer pagina, Integer resultadosPorPagina, Integer idProcesso) {
        if (filtros.isEmpty()) {
            throw new BusinessRollbackException(InfoxMessages.getInstance().get("vidaFuncionalGDPrev.erroValidacaoFiltros"));
        }

        try (CloseableHttpClient client = createHttpClient()) {
            URIBuilder uriBuilder = new URIBuilder(getGDPrevBaseUri().resolve(CONSULTA_DOCUMENTOS_PATH))
                    .addParameter(PARAM_PAGINA, pagina.toString())
                    .addParameter(PARAM_RESULTADOS_POR_PAGINA, resultadosPorPagina.toString());

            if (!StringUtil.isEmpty(filtros.getCpf())) {
                uriBuilder.addParameter(FILTRO_CPF, filtros.getCpf());
            }
            if (!StringUtil.isEmpty(filtros.getNome())) {
                uriBuilder.addParameter(FILTRO_NOME, filtros.getNome());
            }
            if (filtros.getMatricula() != null) {
                uriBuilder.addParameter(FILTRO_MATRICULA, filtros.getMatricula().toString());
            }

            HttpUriRequest request = new HttpGet(uriBuilder.build());
            request.addHeader(createAuthenticationHeader());

            LogWebserviceClient logWebserviceClient = beginLog(CodigosServicos.WS_VIDA_FUNCIONAL_GDPREV, request);
            try (CloseableHttpResponse response = client.execute(request)) {
                validarStatusResposta(logWebserviceClient, response);
                return parseDocumentosResponseEntity(response.getEntity(), idProcesso, logWebserviceClient);
            }
        } catch (IOException | URISyntaxException e) {
            throw new BusinessRollbackException("Erro na execução da requisição, por favor tente novamente", e);
        }
    }

    private void validarStatusResposta(LogWebserviceClient logWebserviceClient, CloseableHttpResponse response) throws IOException {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode < 200 || statusCode > 299) {
            String corpoResposta = "";
            if (response.getEntity() != null) {
                Charset charset = getResponseCharset(response.getEntity());
                corpoResposta = IOUtils.toString(response.getEntity().getContent(), charset.name());
            }
            endLog(logWebserviceClient, corpoResposta);
            throw new BusinessRollbackException(String.format("Status de resposta inválido: %s", statusCode));
        }
    }

    private LogWebserviceClient beginLog(String codigoServico, HttpUriRequest request) {
        Map<String, String> informacoesAdicionais = new HashMap<>();
        informacoesAdicionais.put("url", request.getURI().toString());
        informacoesAdicionais.put("method", request.getMethod());
        for (Header header : request.getAllHeaders()) {
            informacoesAdicionais.put(header.getName(), header.getValue());
        }
        return logWebserviceClientManager.beginLog(codigoServico, "", createGson().toJson(informacoesAdicionais));
    }

    private void endLog(LogWebserviceClient logWebserviceClient, String corpoResposta) {
        logWebserviceClientManager.endLog(logWebserviceClient, corpoResposta);
    }

    @Asynchronous
    public Future<byte[]> downloadDocumento(Long id, Consumer<Float> callback) {
        try (CloseableHttpClient client = createHttpClient()) {
            URI requestUri = new URIBuilder(getGDPrevBaseUri().resolve(DOWNLOAD_PDF_PATH).resolve(id.toString())).build();
            HttpUriRequest request = new HttpGet(requestUri);
            request.addHeader(createAuthenticationHeader());

            LogWebserviceClient logWebserviceClient = beginLog(CodigosServicos.WS_VIDA_FUNCIONAL_GDPREV, request);
            try (CloseableHttpResponse response = client.execute(request)) {
                validarStatusResposta(logWebserviceClient, response);
                endLog(logWebserviceClient, "<corpo da resposta omitido por tamanho (PDF)>");

                int tamanhoPdf = Integer.valueOf(response.getFirstHeader(HttpHeaders.CONTENT_LENGTH).getValue());
                ByteArrayOutputStream pdf = new ByteArrayOutputStream(tamanhoPdf);
                byte[] buffer = new byte[8192];
                int lido;
                int totalLido = 0;
                try (InputStream stream = response.getEntity().getContent()) {
                    while ((lido = stream.read(buffer)) != -1) {
                        pdf.write(buffer, 0, lido);
                        totalLido += lido;
                        callback.accept((float) totalLido / tamanhoPdf);
                    }
                }

                return new AsyncResult<>(pdf.toByteArray());
            }
        } catch (IOException | URISyntaxException e) {
            throw new BusinessRollbackException("Erro na execução da requisição, por favor tente novamente", e);
        }
    }

    private VidaFuncionalGDPrevResponseDTO parseDocumentosResponseEntity(HttpEntity entity, Integer idProcesso, LogWebserviceClient logWebserviceClient) {
        Charset charset = getResponseCharset(entity);

        try (InputStreamReader reader = new InputStreamReader(entity.getContent(), charset)) {
            String corpoResposta = IOUtils.toString(reader);
            endLog(logWebserviceClient, corpoResposta);

            JsonObject jsonObject = createGson().fromJson(corpoResposta, JsonObject.class);
            return VidaFuncionalGDPrevResponseDTO.builder()
                    .total(jsonObject.get(FIELD_TOTAL).getAsInt())
                    .pagina(jsonObject.get(FIELD_PAGINA).getAsInt())
                    .documentos(parseDocumentos(jsonObject.get(FIELD_DOCUMENTOS).getAsJsonArray(), idProcesso))
                    .build();
        } catch (UnsupportedOperationException | IOException e) {
            throw new BusinessRollbackException("Erro no processamento da resposta", e);
        }
    }

    private Charset getResponseCharset(HttpEntity entity) {
        Charset charset = StandardCharsets.UTF_8;
        if (entity.getContentType() != null) {
            try {
                ContentType contentType = ContentType.parse(entity.getContentType().getValue());
                charset = contentType.getCharset();
                if (charset == null) {
                    charset = StandardCharsets.UTF_8;
                }
            } catch (ParseException | UnsupportedCharsetException e) {
                LOG.warn(String.format("Não foi possível extrair o charset do header Content-Type: %s", entity.getContentType().getValue()), e);
            }
        }
        return charset;
    }

    private List<DocumentoVidaFuncionalDTO> parseDocumentos(JsonArray jsonArrayDocumentos, Integer idProcesso) {
        List<DocumentoVidaFuncionalDTO> documentos = new ArrayList<>();
        DateFormat dateFormatter = createDateFormatterVidaFuncionalGDPrev();
        for (JsonElement element : jsonArrayDocumentos) {
            JsonObject jsonObject = element.getAsJsonObject();
            try {
                DocumentoVidaFuncionalDTO documento = DocumentoVidaFuncionalDTO.builder()
                        .cpfServidor(jsonObject.get(FIELD_CPF_SERVIDOR).getAsString())
                        .data(dateFormatter.parse(jsonObject.get(FIELD_DATA).getAsString()))
                        .fonte(jsonObject.get(FIELD_FONTE).getAsString())
                        .id(jsonObject.get(FIELD_ID_DOCUMENTO).getAsLong())
                        .matriculaServidor(jsonObject.get(FIELD_MATRICULA_SERVIDOR).getAsString())
                        .descricao(jsonObject.get(FIELD_TEMPORALIDADE).getAsString())
                        .nomeServidor(jsonObject.get(FIELD_NOME_SERVIDOR).getAsString())
                        .baixado(isDocumentoExistenteNoProcesso(jsonObject.get(FIELD_ID_DOCUMENTO).getAsLong(), idProcesso))
                        .build();
                documentos.add(documento);
            } catch (java.text.ParseException e) {
                throw new BusinessRollbackException(String.format("Data do documento inválida: %s", jsonObject.get(FIELD_DATA).getAsString()));
            }
        }
        return documentos;
    }

    private Header createAuthenticationHeader() {
        String token = parametroManager.getValorParametro(Parametros.TOKEN_WS_VIDA_FUNCIONAL_GDPREV.getLabel());
        if (StringUtil.isEmpty(token)) {
            throw new EppConfigurationException(String.format("O parâmetro %s não está configurado", Parametros.TOKEN_WS_VIDA_FUNCIONAL_GDPREV.getLabel()));
        }
        return new BasicHeader(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token));
    }

    private URI getGDPrevBaseUri() {
        String baseUrl = parametroManager.getValorParametro(Parametros.URL_WS_VIDA_FUNCIONAL_GDPREV.getLabel());
        if (StringUtil.isEmpty(baseUrl)) {
            throw new EppConfigurationException(String.format("O parâmetro %s não está configurado", Parametros.URL_WS_VIDA_FUNCIONAL_GDPREV.getLabel()));
        }

        try {
            if (!baseUrl.endsWith("/")) {
                baseUrl += "/";
            }
            return new URI(baseUrl);
        } catch (URISyntaxException e) {
            throw new EppConfigurationException(String.format("O parâmetro %s está configurado incorretamente", Parametros.URL_WS_VIDA_FUNCIONAL_GDPREV.getLabel()));
        }
    }

    private CloseableHttpClient createHttpClient() {
        return HttpClients.createSystem();
    }

    private Gson createGson() {
        return new Gson();
    }

    private DateFormat createDateFormatterVidaFuncionalGDPrev() {
        return new SimpleDateFormat("dd/MM/yyyy");
    }

    private boolean isDocumentoExistenteNoProcesso(Long identificadorGDPrev, Integer idProcesso) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Documento> documento = query.from(Documento.class);
        Join<Documento, Pasta> pasta = documento.join(Documento_.pasta, JoinType.INNER);
        Join<Documento, DocumentoBin> documentoBin = documento.join(Documento_.documentoBin, JoinType.INNER);
        query.select(cb.count(documentoBin));
        query.where(
                cb.equal(pasta.get(Pasta_.processo).get(Processo_.idProcesso), idProcesso),
                cb.equal(documentoBin.get(DocumentoBin_.idDocumentoExterno), gerarIdentificacaoVidaFuncionalGDPrev(identificadorGDPrev))
                );
        return getEntityManager().createQuery(query).getSingleResult() > 0;
    }

    public String gerarIdentificacaoVidaFuncionalGDPrev(Long identificadorGDPrev) {
        return String.format("VidaFuncionalGDPrev:%s", identificadorGDPrev);
    }
}
