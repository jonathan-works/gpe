package br.com.infox.epp.entrega;

import java.io.Serializable;

import javax.inject.Inject;

import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.entrega.entity.CategoriaEntrega;
import br.com.infox.epp.entrega.entity.CategoriaEntregaItem;

public class CategoriaEntregaController implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private CategoriaEntregaItem pai;
    
    private String codigo;
    private String descricao;
    
    @Inject private CategoriaEntregaService categoriaEntregaService;
    @Inject private CategoriaEntregaSearch categoriaEntregaSearch;
    @Inject private CategoriaEntregaItemSearch categoriaEntregaItemSearch;
    
    private Modo modo;
    
    private void clear() {
        this.codigo = null;
        this.descricao = null;
        this.pai = null;
        modo = Modo.CRIAR;
    }
    @ExceptionHandled
    public void salvarCategoria() {
        CategoriaEntrega categoria = new CategoriaEntrega();
        String codigoItemPai = pai == null ? null : pai.getCodigo();
        if (Modo.CRIAR.equals(modo)){
            if (codigo == null){
                codigo = CategoriaEntregaView.generateCodigo(descricao);
            }
            categoria.setCodigo(codigo);
            categoria.setDescricao(descricao);
            categoriaEntregaService.novaCategoria(categoria, codigoItemPai);
        } else if (Modo.EDITAR.equals(modo)){
            categoriaEntregaService.atualizar(codigo, descricao);
        }
        clear();
    }

    private void setCategoria(CategoriaEntrega categoria) {
        this.codigo=categoria.getCodigo();
        this.descricao=categoria.getDescricao();
    }

    public void editar(String codigoCategoria){
        clear();
        this.modo=Modo.EDITAR;
        setCategoria(categoriaEntregaSearch.getCategoriaEntregaByCodigo(codigoCategoria));
    }
    
    public void criar(String codigoPai){
        clear();
        this.modo=Modo.CRIAR;
        if (codigoPai != null){
            this.pai = categoriaEntregaItemSearch.getCategoriaEntregaItemByCodigo(codigoPai);
        }
    }
    
    public String getCodigo() {
        return codigo;
    }
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    public CategoriaEntregaItem getPai() {
        return pai;
    }

    private static enum Modo {
        CRIAR,EDITAR
    }
}
