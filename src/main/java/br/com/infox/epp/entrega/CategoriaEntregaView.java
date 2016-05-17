package br.com.infox.epp.entrega;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ObjectUtils;
import org.infinispan.util.SimpleImmutableEntry;
import org.primefaces.event.SelectEvent;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.entrega.entity.CategoriaEntrega;
import br.com.infox.epp.entrega.entity.CategoriaEntregaItem;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Named
@ViewScoped
public class CategoriaEntregaView implements Serializable {

    private static final LogProvider LOG = Logging.getLogProvider(CategoriaEntregaView.class);

    private static final long serialVersionUID = 1L;

    @Inject private CategoriaEntregaService categoriaEntregaService;
    @Inject private CategoriaEntregaItemService categoriaEntregaItemService;
    @Inject private CategoriaEntregaSearch categoriaEntregaSearch;
    @Inject private CategoriaEntregaItemSearch categoriaEntregaItemSearch;

    private CategoriaEntregaItem pai;
    private String itemDescricao;
    private String itemCodigo;
    private String categoriaDescricao;
    private String categoriaCodigo;

    private CurrentView currentView;

    @PostConstruct
    public void init() {
        clear();
    }

    private void clear() {
        this.categoriaCodigo = null;
        this.categoriaDescricao = null;
        this.itemCodigo = null;
        this.itemDescricao = null;
        this.pai = null;
        currentView = CurrentView.CREATE_CATEGORIA;
    }

    public void setItem(CategoriaEntregaItem item) {
        itemCodigo = item.getCodigo();
        itemDescricao = item.getDescricao();
    }

    public void setCategoria(CategoriaEntrega categoria) {
        this.categoriaDescricao = categoria.getDescricao();
        this.categoriaCodigo = categoria.getCodigo();
    }

    public String getCategoriaCodigo() {
        return categoriaCodigo;
    }

    public String getCategoriaDescricao() {
        return categoriaDescricao;
    }

    public void setCategoriaCodigo(String categoriaCodigo) {
        this.categoriaCodigo = categoriaCodigo;
    }

    public void setCategoriaDescricao(String categoriaDescricao) {
        this.categoriaDescricao = categoriaDescricao;
    }

    public String getItemDescricao() {
        return itemDescricao;
    }

    public void setItemDescricao(String itemDescricao) {
        this.itemDescricao = itemDescricao;
    }

    public String getItemCodigo() {
        return itemCodigo;
    }

    public void setItemCodigo(String itemCodigo) {
        this.itemCodigo = itemCodigo;
    }

    private Entry<String, String> getFromPath() {
        Map<String, String> requestParamMap = FacesContext.getCurrentInstance().getExternalContext()
                .getRequestParameterMap();
        String path = ObjectUtils.defaultIfNull(requestParamMap.get("path"), "/");
        String[] codigos = path.substring(1).split("/");
        if (codigos.length == 0) {
            return null;
        }
        if (codigos.length == 1) {
            return new SimpleImmutableEntry<>(null, codigos[0]);
        }
        return new SimpleImmutableEntry<>(codigos[codigos.length - 2], codigos[codigos.length - 1]);
    }

    @ExceptionHandled
    public void criarCategoria() {
        clear();
        currentView = CurrentView.CREATE_CATEGORIA;
        Entry<String, String> fromPath = getFromPath();
        if (fromPath != null) {
            pai = categoriaEntregaItemSearch.getCategoriaEntregaItemByCodigo(fromPath.getValue());
        }
    }

    @ExceptionHandled
    public void removerCategoria() {
        clear();
        Entry<String, String> fromPath = getFromPath();
        if (fromPath != null) {
            categoriaEntregaService.remover(fromPath.getValue());
        }
    }

    @ExceptionHandled
    public void editarCategoria() {
        clear();
        currentView = CurrentView.EDIT_CATEGORIA;
        Entry<String, String> fromPath = getFromPath();
        if (fromPath != null) {
            setCategoria(categoriaEntregaSearch.getCategoriaEntregaByCodigo(fromPath.getValue()));
        }
    }

    @ExceptionHandled
    public void criarItem() {
        clear();
        currentView = CurrentView.CREATE_ITEM;
        Entry<String, String> fromPath = getFromPath();
        if (fromPath != null) {
            setCategoria(categoriaEntregaSearch.getCategoriaEntregaByCodigo(fromPath.getValue()));
            if (fromPath.getKey() != null) {
                try {
                    pai = categoriaEntregaItemSearch.getCategoriaEntregaItemByCodigo(fromPath.getKey());
                } catch (NoResultException e) {
                    LOG.trace(e);
                }
            }
        }
    }

    @ExceptionHandled
    public void removerItem() {
        clear();
        Entry<String, String> fromPath = getFromPath();
        if (fromPath != null) {
            String codigoItem = fromPath.getValue();
            String codigoPai = fromPath.getKey();
            categoriaEntregaItemService.remover(codigoItem, codigoPai);
        }
    }

    @ExceptionHandled
    public void editarItem() {
        clear();
        this.currentView = CurrentView.EDIT_ITEM;
        Entry<String, String> fromPath = getFromPath();
        if (fromPath != null) {
            if (fromPath.getKey() != null) {
                try {
                    pai = categoriaEntregaItemSearch.getCategoriaEntregaItemByCodigo(fromPath.getKey());
                } catch (NoResultException e) {
                    LOG.trace(e);
                }
            }
            CategoriaEntregaItem item = categoriaEntregaItemSearch.getCategoriaEntregaItemByCodigo(fromPath.getValue());
            setItem(item);
            setCategoria(item.getCategoriaEntrega());
        }
    }

    public List<CategoriaEntrega> completeCategoria(String query) {
        String codigoItemPai = pai != null ? pai.getCodigo() : null;
        return categoriaEntregaSearch.getCategoriasFilhasComDescricao(codigoItemPai, query);
    }

    public List<CategoriaEntregaItem> completeItem(String query) {
        return categoriaEntregaItemSearch.getCategoriaEntregaItemByCodigoCategoriaAndDescricaoLike(categoriaCodigo, query);
    }

    public CategoriaEntregaItem getPai() {
        return pai;
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

    private String generateCodigo(String value) {
        byte[] bytes = new BigInteger(value.getBytes()).add(BigInteger.valueOf(System.currentTimeMillis()))
                .toString(Character.MAX_RADIX).getBytes();
        String codigo = Base64.encodeBase64URLSafeString(bytes);
        if (codigo.length() > 30) {
            codigo = codigo.substring(codigo.length() - 30);
        }
        return codigo;
    }

    @ExceptionHandled
    public void salvarCategoria() {
        CategoriaEntrega categoria = new CategoriaEntrega();
        String codigoItemPai = pai == null ? null : pai.getCodigo();
        if (CurrentView.CREATE_CATEGORIA.equals(currentView)){
            if (categoriaCodigo == null){
                categoriaCodigo = generateCodigo(categoriaDescricao);
            }
            categoria.setCodigo(categoriaCodigo);
            categoria.setDescricao(categoriaDescricao);
            categoriaEntregaService.novaCategoria(categoria, codigoItemPai);
        } else if (CurrentView.EDIT_CATEGORIA.equals(currentView)){
            categoriaEntregaService.atualizar(categoriaCodigo, categoriaDescricao);
        }
        clear();
    }

    private CategoriaEntregaItem prepareItemForSave(){
        CategoriaEntregaItem item = new CategoriaEntregaItem();
        try {
            item = categoriaEntregaItemSearch.getCategoriaEntregaItemByCodigo(itemCodigo);
        } catch (NoResultException e) {
        }catch(EJBException e){
            if ( e.getCause() != null && e.getCause() instanceof NoResultException) {
                LOG.trace("New Item");
            } else {
                throw e;
            }
        }
        return item;
    }
    
    @ExceptionHandled
    public void salvarItem() {
        CategoriaEntregaItem item = prepareItemForSave();
        String codigoItemPai = pai == null ? null : pai.getCodigo();
        if (CurrentView.CREATE_ITEM.equals(currentView)){
            if (itemCodigo == null){
                itemCodigo = generateCodigo(itemDescricao);
            }
            
            item.setCodigo(itemCodigo);
            item.setDescricao(itemDescricao);
            if (item.getId()==null){
                categoriaEntregaItemService.novo(item, codigoItemPai, categoriaCodigo);
            } else {
                categoriaEntregaItemService.relacionarItens(codigoItemPai, item.getCodigo());
            }
        } else if (CurrentView.EDIT_ITEM.equals(currentView)){
            categoriaEntregaItemService.atualizar(itemCodigo, itemDescricao);
        }
        clear();
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