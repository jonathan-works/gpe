package br.com.infox.epp.entrega.rest;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import br.com.infox.core.token.AccessTokenAuthentication;
import br.com.infox.core.token.TokenRequester;

@AccessTokenAuthentication(TokenRequester.UNSPECIFIED)
public class ModeloEntregaRestImpl implements ModeloEntregaRest {

    @Inject
    private ModeloEntregaRestService modeloEntregaRestService;
    
    private static final String DATE_FORMAT="yyyy-MM-dd";
    @Override
    public List<Categoria> getCategoria(String codigoLocalizacao, String data) {
        return getCategoriasResponse(null,codigoLocalizacao, data);
    }

    @Override
    public List<Categoria> getCategoria(String codigoItemPai, String codigoLocalizacao, String data) {
        return getCategoriasResponse(codigoItemPai, codigoLocalizacao, data);
    }

    private List<Categoria> getCategoriasResponse(String codigoItemPai, String codigoLocalizacao, String data) {
        try {
            Date date = new SimpleDateFormat(DATE_FORMAT).parse(data);
            return modeloEntregaRestService.getCategoriasFilhas(codigoItemPai, codigoLocalizacao, date);
        } catch (ParseException e) {
            JsonObject obj = new JsonObject();
            obj.addProperty("errorMessage", MessageFormat.format("Date should be in \"{0}\" format", DATE_FORMAT));
            String responseEntity = new Gson().toJson(obj);
            throw new WebApplicationException(Response.status(400).entity(responseEntity).build());
        }
    }

}
