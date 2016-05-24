package br.com.infox.epp.entrega;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.entrega.entity.CategoriaEntregaItem;
import br.com.infox.epp.localizacao.LocalizacaoSearch;


public class RestricaoCategoriaEntregaItemController implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Inject
    private LocalizacaoSearch localizacaoSearch;
    @Inject
    private CategoriaEntregaItemSearch categoriaEntregaItemSearch;
    @Inject private ModeloEntregaService modeloEntregaService;
    
    private CategoriaEntregaItem categoriaEntregaItem;
    
    private List<Localizacao> restricoes;
    
    public void clear(){
        restricoes = new ArrayList<>();
    }
    
    public void init(String codigoItem){
        clear();
        this.categoriaEntregaItem = categoriaEntregaItemSearch.getCategoriaEntregaItemByCodigo(codigoItem);
        restricoes = categoriaEntregaItem.getRestricoes();
    }
    
    public CategoriaEntregaItem getCategoriaEntregaItem() {
        return categoriaEntregaItem;
    }

    public List<Localizacao> completeLocalizacao(String query){
        List<Localizacao> localizacoes = new ArrayList<>(localizacaoSearch.getLocalizacoesExternasWithDescricaoLike(Authenticator.getLocalizacaoAtual(), query));
        localizacoes.removeAll(getRestricoes());
        return localizacoes;
    }
    
    public void adicionar(Localizacao localizacao){
        restricoes.add(localizacao);
    }

    public void remover(Localizacao localizacao){
        restricoes.remove(localizacao);
    }
    
    public List<Localizacao> getRestricoes() {
        return restricoes;
    }
    
    public void gravar(){
        String codigo = getCategoriaEntregaItem().getCodigo();
        modeloEntregaService.salvarRestricoesLocalizacao(codigo, getRestricoes());
        clear();
        init(codigo);
    }
    
}
