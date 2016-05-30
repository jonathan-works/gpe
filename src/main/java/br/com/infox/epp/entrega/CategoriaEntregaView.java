package br.com.infox.epp.entrega;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ObjectUtils;

import br.com.infox.core.token.AccessToken;
import br.com.infox.core.token.AccessTokenAuthenticationInterceptor;
import br.com.infox.core.token.AccessTokenManager;
import br.com.infox.core.token.TokenRequester;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.cdi.exception.ExceptionHandled.MethodType;

@Named
@ViewScoped
public class CategoriaEntregaView implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject private CategoriaEntregaService categoriaEntregaService;
    @Inject private CategoriaEntregaItemService categoriaEntregaItemService;
    @Inject private RestricaoCategoriaEntregaItemController restricaoCategoriaEntregaItemController;

    @Inject private CategoriaEntregaController categoriaEntregaController;
    @Inject private CategoriaEntregaItemController categoriaEntregaItemController;
    @Inject private ModeloEntregaController modeloEntregaController;
    @Inject private AccessTokenManager accessTokenManager;
    
    private CurrentView currentView;

    private AccessToken accessToken;

    @PostConstruct
    @ExceptionHandled
    public void init() {
        clear();
        createToken();
    }

    @PreDestroy
    @ExceptionHandled
    public void destroy(){
        accessTokenManager.remove(accessToken);
    }
    
    private void createToken() {
        AccessToken accessToken = new AccessToken();
        accessToken.setToken(UUID.randomUUID());
        accessToken.setTokenRequester(TokenRequester.UNSPECIFIED);
        accessTokenManager.persist(accessToken);
        this.accessToken = accessToken;
    }
    
    public String getAccessTokenName() {
        return AccessTokenAuthenticationInterceptor.NOME_TOKEN_HEADER_HTTP;
    }
    
    public String getAccessTokenValue() {
        if (accessToken == null){
            return "";
        }
        return accessToken.getToken().toString();
    }

    public RestricaoCategoriaEntregaItemController getRestricaoCategoriaEntregaItemController() {
        return restricaoCategoriaEntregaItemController;
    }
    
    public ModeloEntregaController getModeloEntregaController() {
        return modeloEntregaController;
    }
    
    public CategoriaEntregaController getCategoriaEntregaController() {
        return categoriaEntregaController;
    }
    
    public CategoriaEntregaItemController getCategoriaEntregaItemController() {
        return categoriaEntregaItemController;
    }
    
    public void clear() {
        currentView = CurrentView.NONE;
        modeloEntregaController.clear();
    }

    private String[] getPath() {
        Map<String, String> requestParamMap = FacesContext.getCurrentInstance().getExternalContext()
                .getRequestParameterMap();
        String path = ObjectUtils.defaultIfNull(requestParamMap.get("path"), "/");
        String[] codigos = path.substring(1).split("/");
        if (codigos.length == 0) {
            return new String[]{};
        }
        return codigos;
    }

    @ExceptionHandled
    public void configurarModeloEntrega(){
        clear();
        currentView=CurrentView.CONFIG_ENTREGA_FIELDS;
        modeloEntregaController.iniciarConfiguracao(getPath());
    }
    
    @ExceptionHandled
    public void criarCategoria() {
        clear();
        currentView = CurrentView.CREATE_CATEGORIA;
        String[] path = getPath();
        if (path.length > 0) {
            categoriaEntregaController.criar(path[path.length-1]);
        }
    }

    @ExceptionHandled(MethodType.REMOVE)
    public void removerCategoria() {
        clear();
        String[] path = getPath();
        if (path.length > 0) {
            categoriaEntregaService.remover(path[path.length-1]);
        }
    }

    @ExceptionHandled
    public void editarCategoria() {
        clear();
        currentView = CurrentView.EDIT_CATEGORIA;
        String[] path = getPath();
        if (path.length > 0) {
            categoriaEntregaController.editar(path[path.length-1]);
        }
    }

    @ExceptionHandled
    public void criarItem() {
        clear();
        currentView = CurrentView.CREATE_ITEM;
        String[] path = getPath();
        if (path.length > 0) {
            String codigoPai = path.length>1?path[path.length-2]:null;
            String codigoCategoria=path[path.length-1];
            categoriaEntregaItemController.iniciaModoCriar(codigoCategoria, codigoPai);
        }
    }

    @ExceptionHandled(MethodType.REMOVE)
    public void removerItem() {
        clear();
        String[] path = getPath();
        if (path != null) {
            String codigoPai = path.length>1?path[path.length-2]:null;
            String codigoItem=path[path.length-1];
            categoriaEntregaItemService.remover(codigoItem, codigoPai);
        }
    }

    @ExceptionHandled
    public void editarItem() {
        clear();
        this.currentView = CurrentView.EDIT_ITEM;
        String[] path = getPath();
        if (path != null) {
            categoriaEntregaItemController.iniciaModoEditar(path[path.length-1]);
        }
    }
    
    @ExceptionHandled
    public void configRestricaoItem(){
        clear();
        this.currentView = CurrentView.CONFIG_RESTRICAO_ITEM;
        String[] path = getPath();
        if (path != null) {
            restricaoCategoriaEntregaItemController.init(path[path.length-1]);
        }
    }

/*
    public List<CategoriaEntrega> completeCategoria(String query) {
        String codigoItemPai = pai != null ? pai.getCodigo() : null;
        return categoriaEntregaSearch.getCategoriasFilhasComDescricao(codigoItemPai, query);
    }

    public List<CategoriaEntregaItem> completeItem(String query) {
        return categoriaEntregaItemSearch.getCategoriaEntregaItemByCodigoCategoriaAndDescricaoLike(categoriaCodigo, query);
    }

    public void onCategoriaSelect(SelectEvent event) {
        CategoriaEntrega categoria = categoriaEntregaSearch.getCategoriaEntregaByCodigo((String) event.getObject());
        itemCodigo = categoria.getCodigo();
        itemDescricao = categoria.getDescricao();
    }

    public void onItemSelect(SelectEvent event) {
        CategoriaEntregaItem item = categoriaEntregaItemSearch
                .getCategoriaEntregaItemByCodigo((String) event.getObject());
        itemCodigo = item.getCodigo();
        itemDescricao = item.getDescricao();
    }
*/
    static String generateCodigo(String value) {
        byte[] bytes = new BigInteger(value.getBytes()).add(BigInteger.valueOf(System.currentTimeMillis()))
                .toString(Character.MAX_RADIX).getBytes();
        String codigo = Base64.encodeBase64URLSafeString(bytes);
        if (codigo.length() > 30) {
            codigo = codigo.substring(codigo.length() - 30);
        }
        return codigo;
    }

    public String getView() {
        switch (currentView) {
        case CONFIG_ENTREGA_FIELDS:
            return "configEntregaForm.xhtml";
        case CREATE_ITEM:
        case EDIT_ITEM:
            return "categoriaItemForm.xhtml";
        case EDIT_CATEGORIA:
        case CREATE_CATEGORIA:
            return "categoriaForm.xhtml";
        case CONFIG_RESTRICAO_ITEM:
            return "restricaoCategoriaItemForm.xhtml";
        default:
            return "";
        }
    }

}

enum CurrentView {
    NONE, CREATE_CATEGORIA, EDIT_CATEGORIA, CREATE_ITEM, EDIT_ITEM, CONFIG_RESTRICAO_ITEM, CONFIG_ENTREGA_FIELDS
}