package br.com.infox.epp.entrega;

import java.io.Serializable;

import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.entrega.entity.CategoriaEntrega;
import br.com.infox.epp.entrega.entity.CategoriaEntregaItem;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

public class CategoriaEntregaItemController implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final LogProvider LOG = Logging.getLogProvider(CategoriaEntregaView.class);

    @Inject private CategoriaEntregaItemService categoriaEntregaItemService;
    @Inject private CategoriaEntregaItemSearch categoriaEntregaItemSearch;
    @Inject private CategoriaEntregaSearch categoriaEntregaSearch;
    @Inject private InfoxMessages messages;

    private CategoriaEntrega categoria;
    private CategoriaEntregaItem pai;
    private String descricao;
    private String codigo;

    private Modo modo;

    private void clear() {
        this.codigo = null;
        this.descricao = null;
        this.pai = null;
    }

    private CategoriaEntregaItem prepareItemForSave() {
        CategoriaEntregaItem item = new CategoriaEntregaItem();
        try {
            item = categoriaEntregaItemSearch.getCategoriaEntregaItemByCodigo(codigo);
        } catch (NoResultException e) {
            LOG.trace("New Item");
        } catch (EJBException e) {
            if (e.getCause() != null && e.getCause() instanceof NoResultException) {
                LOG.trace("New Item");
            } else {
                throw e;
            }
        }
        return item;
    }

    public CategoriaEntregaItem getPai() {
        return pai;
    }

    public void iniciaModoCriar(String codigoCategoria, String codigoPai) {
        clear();
        this.modo = Modo.CRIAR;
        this.categoria = categoriaEntregaSearch.getCategoriaEntregaByCodigo(codigoCategoria);
        if (codigoPai != null) {
            try {
                pai = categoriaEntregaItemSearch.getCategoriaEntregaItemByCodigo(codigoPai);
            } catch (NoResultException e) {
                LOG.trace(e);
            }
        }
    }

    public void iniciaModoEditar(String codigoItem) {
        clear();
        this.modo = Modo.EDITAR;
        if (codigoItem != null) {
            setItem(categoriaEntregaItemSearch.getCategoriaEntregaItemByCodigo(codigoItem));
        }
    }

    private void setItem(CategoriaEntregaItem item) {
        this.codigo = item.getCodigo();
        this.descricao = item.getDescricao();
        this.categoria = item.getCategoriaEntrega();
    }

    @ExceptionHandled
    public void salvarItem() {
        CategoriaEntregaItem item = prepareItemForSave();
        String codigoItemPai = pai == null ? null : pai.getCodigo();
        String resultMessageKey="";
        if (Modo.CRIAR.equals(modo)) {
            if (codigo == null) {
                codigo = CategoriaEntregaView.generateCodigo(descricao);
            }

            item.setCodigo(codigo);
            item.setDescricao(descricao);
            if (item.getId() == null) {
                categoriaEntregaItemService.novo(item, codigoItemPai, categoria.getCodigo());
            } else {
                categoriaEntregaItemService.relacionarItens(codigoItemPai, codigo);
            }
            resultMessageKey="entity_created";
        } else if (Modo.EDITAR.equals(modo)) {
            categoriaEntregaItemService.atualizar(codigo, descricao);
            resultMessageKey="entity_updated";
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(messages.get(resultMessageKey)));
        clear();
    }

    public CategoriaEntrega getCategoria() {
        return categoria;
    }
    
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    private static enum Modo {
        CRIAR, EDITAR
    }

}
