package br.com.infox.epp.rest.client;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import br.com.infox.epp.cdi.util.Beans;
import br.com.infox.epp.webservice.log.entity.LogWebserviceClient;
import br.com.infox.epp.webservice.log.manager.LogWebserviceClientManager;
import br.com.infox.ws.request.HttpRequestContext;
import br.com.infox.ws.request.HttpRequestListener;
import br.com.infox.ws.request.HttpResponseContext;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpRequestLogger implements HttpRequestListener {

    private final String codigoServico;
    private Logger logger;
    private LogWebserviceClientManager logWebserviceClientManager;
    private LogWebserviceClient logWebserviceClient;

    public HttpRequestLogger() {
        this("-");
    }

    public HttpRequestLogger(String codigoServico) {
        this(codigoServico, Logger.getLogger(HttpRequestLogger.class.getName()), Beans.getReference(LogWebserviceClientManager.class),
                null);
    }

    @Override
    public void beforeRequest(HttpRequestContext ctx) {
        final Map<String, Object> informacoesAdicionais = new HashMap<>();
        informacoesAdicionais.put("url", ctx.getUrl());
        informacoesAdicionais.put("method", ctx.getMethod());
        informacoesAdicionais.putAll(ctx.getHeaders());

        final String requisicao = Optional.ofNullable(ctx.getContent()).orElse("");
        final String informacoesAdicionaisGson = new Gson().toJson(informacoesAdicionais);
        logWebserviceClient = logWebserviceClientManager.beginLog(codigoServico, requisicao, informacoesAdicionaisGson);
    }

    @Override
    public void afterResponse(HttpResponseContext ctx) {
        handleResult(ctx);
    }

    @Override
    public void onException(HttpResponseContext ctx, Exception e) {
        handleResult(ctx, e);
    }

    private void handleResult(HttpResponseContext ctx, Exception e) {
        JsonObject jsonObject = produceLogResultJson(ctx, e);
        StringBuilder content = new StringBuilder();
        content.append("application/json;");
        content.append(new Gson().toJson(jsonObject));

        logWebserviceClientManager.endLog(logWebserviceClient, content.toString());
        Beans.destroy(logWebserviceClientManager);
        Beans.destroy(logger);
    }

    private JsonObject produceLogResultJson(HttpResponseContext ctx, Exception e) {
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("status", ctx.getStatusCode());
        jsonObject.add("headers", gson.toJsonTree(ctx.getHeaders()));
        jsonObject.addProperty("content", ctx.getContent());
        if (e != null) {
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            jsonObject.addProperty("stacktrace", writer.toString());
        }
        return jsonObject;
    }

    private void handleResult(HttpResponseContext ctx) {
        handleResult(ctx, null);
    }

}