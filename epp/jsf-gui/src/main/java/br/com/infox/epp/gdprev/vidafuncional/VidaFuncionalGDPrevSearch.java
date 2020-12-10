package br.com.infox.epp.gdprev.vidafuncional;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ws.rs.core.HttpHeaders;

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
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.system.Parametros;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.BusinessRollbackException;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class VidaFuncionalGDPrevSearch {

    private static final LogProvider LOG = Logging.getLogProvider(VidaFuncionalGDPrevSearch.class);

    private static final String CONSULTA_DOCUMENTOS_PATH = "cuiaba/search";
    private static final String PARAM_PAGINA = "pagina";
    private static final String PARAM_RESULTADOS_POR_PAGINA = "por_pagina";
    private static final String FILTRO_CPF = "cpf";
    private static final String FILTRO_MATRICULA = "matricula";
    private static final String FILTRO_NOME = "nome";

    public VidaFuncionalGDPrevResponseDTO getDocumentos(FiltroVidaFuncionalGDPrev filtros, Integer pagina, Integer resultadosPorPagina) {
        if (filtros.isEmpty()) {
            throw new BusinessRollbackException("É necessário informar o nome, CPF e/ou matrícula do servidor");
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

            try (CloseableHttpResponse response = client.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode < 200 || statusCode > 299) {
                    throw new BusinessRollbackException(String.format("Status de resposta inválido: %s", statusCode));
                }

                return parseDocumentosResponseEntity(response.getEntity());
            }
        } catch (IOException | URISyntaxException e) {
            throw new BusinessRollbackException("Erro na execução da requisição", e);
        }
    }

    private VidaFuncionalGDPrevResponseDTO parseDocumentosResponseEntity(HttpEntity entity) {
        Charset charset = StandardCharsets.UTF_8;
        if (entity.getContentType() != null) {
            try {
                ContentType contentType = ContentType.parse(entity.getContentType().getValue());
                charset = contentType.getCharset();
            } catch (ParseException | UnsupportedCharsetException e) {
                LOG.warn(String.format("Não foi possível extrair o charset do header Content-Type: %s", entity.getContentType().getValue()), e);
            }
        }

        try (InputStreamReader reader = new InputStreamReader(entity.getContent(), charset)) {
            JsonObject jsonObject = createGson().fromJson(reader, JsonObject.class);
            return VidaFuncionalGDPrevResponseDTO.builder()
                    .total(jsonObject.get("total").getAsInt())
                    .pagina(jsonObject.get("pagina").getAsInt())
                    .documentos(parseDocumentos(jsonObject.get("documentos").getAsJsonArray()))
                    .build();
        } catch (UnsupportedOperationException | IOException e) {
            throw new BusinessRollbackException("Erro no processamento da resposta", e);
        }
    }

    private List<DocumentoVidaFuncionalDTO> parseDocumentos(JsonArray jsonArrayDocumentos) {
        List<DocumentoVidaFuncionalDTO> documentos = new ArrayList<>();
        DateFormat dateFormatter = createDateFormatterVidaFuncionalGDPrev();
        for (JsonElement element : jsonArrayDocumentos) {
            JsonObject jsonObject = element.getAsJsonObject();
            try {
                DocumentoVidaFuncionalDTO documento = DocumentoVidaFuncionalDTO.builder()
                        .cpfServidor(jsonObject.get("campo->CPF do Servidor").getAsString())
                        .data(dateFormatter.parse(jsonObject.get("data").getAsString()))
                        .fonte(jsonObject.get("campo->Fonte").getAsString())
                        .id(jsonObject.get("id").getAsLong())
                        .matriculaServidor(jsonObject.get("campo->Matrícula do servidor").getAsString())
                        .descricao(jsonObject.get("temporalidade_real").getAsString())
                        .nomeServidor(jsonObject.get("campo->Nome do servidor").getAsString())
                        .build();
                documentos.add(documento);
            } catch (java.text.ParseException e) {
                throw new BusinessRollbackException(String.format("Data do documento inválida: %s", jsonObject.get("data").getAsString()));
            }
        }
        return documentos;
    }

    private Header createAuthenticationHeader() {
        String token = Parametros.TOKEN_WS_VIDA_FUNCIONAL_GDPREV.getValue();
        if (StringUtil.isEmpty(token)) {
            throw new EppConfigurationException(String.format("O parâmetro %s não está configurado", Parametros.TOKEN_WS_VIDA_FUNCIONAL_GDPREV.getLabel()));
        }
        return new BasicHeader(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token));
    }

    private URI getGDPrevBaseUri() {
        String baseUrl = Parametros.URL_WS_VIDA_FUNCIONAL_GDPREV.getValue();
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
}
