package br.com.infox.epp.entrega;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ObjectUtils;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;

@Named
@ViewScoped
public class CategoriaEntregaView implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject private CategoriaEntregaService categoriaEntregaService;
    @Inject private CategoriaEntregaItemService categoriaEntregaItemService;

    @Inject private CategoriaEntregaController categoriaEntregaController;
    @Inject private CategoriaEntregaItemController categoriaEntregaItemController;
    @Inject private ModeloEntregaController modeloEntregaController;
    
    private CurrentView currentView;

    @PostConstruct
    public void init() {
        clear();
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
        currentView = CurrentView.CREATE_CATEGORIA;
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

    @ExceptionHandled
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

    @ExceptionHandled
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
        case NONE:
        case CREATE_CATEGORIA:
        default:
            return "categoriaForm.xhtml";
        }
    }

}

enum CurrentView {
    NONE, CREATE_CATEGORIA, EDIT_CATEGORIA, CREATE_ITEM, EDIT_ITEM, CONFIG_ENTREGA_FIELDS
}