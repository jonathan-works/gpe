package br.com.infox.epp.processo.linkExterno;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gson.Gson;

import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.cdi.exception.ExceptionHandled.MethodType;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.jwt.JWT;
import br.com.infox.jwt.claims.InfoxPrivateClaims;
import br.com.infox.jwt.claims.JWTClaim;

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
        List<Entry<JWTClaim, Object>> claims = new ArrayList<>();
        UsuarioLogin usuarioLogado = Authenticator.getUsuarioLogado();
        claims.add(JWT.claim(InfoxPrivateClaims.LOGIN, usuarioLogado.getLogin()));
        if (usuarioLogado.getPessoaFisica() != null) {
            claims.add(JWT.claim(InfoxPrivateClaims.CPF,usuarioLogado.getPessoaFisica().getCpf().replaceAll("\\D", "")));
        }
        return service.appendJWTTokenToUrlQuery(link, claims);
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
        LinkAplicacaoExterna link = new LinkAplicacaoExterna();
        link.setProcesso(getProcesso());
        setEntity(link);
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
