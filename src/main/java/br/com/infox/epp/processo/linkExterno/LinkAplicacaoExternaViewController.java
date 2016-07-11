package br.com.infox.epp.processo.linkExterno;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gson.Gson;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.cdi.exception.ExceptionHandled.MethodType;
import br.com.infox.epp.processo.entity.Processo;

@Named
@ViewScoped
public class LinkAplicacaoExternaViewController implements Serializable{

    private static final long serialVersionUID = 1L;
    
    @Inject
    private LinkAplicacaoExternaService service;
    @Inject
    private LinkAplicacaoExternaSearch search;
    
    private LinkAplicacaoExterna entity;
    private Processo processo;

    @PostConstruct
    public void init(){
    }
    
    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    public LinkAplicacaoExterna getEntity() {
        return entity;
    }

    public void setEntity(LinkAplicacaoExterna entity) {
        this.entity = entity;
    }

    public String retrieveUrlWithToken(LinkAplicacaoExterna link){
        return service.appendJWTTokenToUrlQuery(link);
    }
    
    public List<LinkAplicacaoExterna> getLinks(){
        return search.carregarLinksAplicacaoExternaAtivos(getProcesso());
    }
    
    @ExceptionHandled
    public void carregar(Integer id){
        LinkAplicacaoExterna linkAplicacaoExterna = service.findById(id);
        Gson gson = new Gson();
        setEntity(gson.fromJson(gson.toJson(linkAplicacaoExterna), LinkAplicacaoExterna.class));
    }
    
    @ExceptionHandled
    public void criar(){
        setEntity(new LinkAplicacaoExterna());
    }
    
    @ExceptionHandled(MethodType.PERSIST)
    public void salvar(){
        service.salvar(getEntity());
        setEntity(null);
    }
    @ExceptionHandled(MethodType.REMOVE)
    public void remover(){
        service.remover(getEntity());
        setEntity(null);
    }
    @ExceptionHandled(MethodType.REMOVE)
    public void remover(LinkAplicacaoExterna link){
        service.remover(link);
    }
    
}
