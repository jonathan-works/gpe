package br.com.infox.epp.processo.linkExterno;

import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.http.client.utils.URIBuilder;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.jwt.JWTUtils;
import br.com.infox.seam.exception.BusinessException;

@Stateless
public class LinkAplicacaoExternaService {

    //TODO: Remover inicialização e retirar comentário da injeção quando houver um DaoProvider 
    //@javax.inject.Inject 
    private Dao<LinkAplicacaoExterna, Integer> dao;
    
    @PostConstruct
    public void init(){
        dao = new Dao<LinkAplicacaoExterna, Integer>(LinkAplicacaoExterna.class) {};
    }
    
    public LinkAplicacaoExterna findById(Integer id) {
        return dao.findById(id);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void salvar(LinkAplicacaoExterna entity) {
        entity.setAtivo(Boolean.TRUE);
        dao.persist(entity);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void remover(LinkAplicacaoExterna entity) {
        entity.setAtivo(Boolean.FALSE);
        dao.update(entity);
    }
    
    String appendQueriesToUrl(String urlString, Collection<Entry<String,String>> queries){
        try {
            if (!urlString.matches("^\\w+://.+")){
                urlString = String.format("http://%s", urlString);
            }
            URIBuilder uriBuilder = new URIBuilder(urlString);
            for (Entry<String, String> entry : queries) {
                uriBuilder.addParameter(entry.getKey(), entry.getValue());
            }
            return uriBuilder.build().toString();
        } catch (URISyntaxException e) {
            throw new BusinessException("linkAplicacaoExterna.malformed.url",e);
        }
    }
    
    public String appendJWTTokenToUrlQuery(LinkAplicacaoExterna linkAplicacaoExterna){
        HashMap<String, String> hashMap = new HashMap<String,String>();
        return appendQueriesToUrl(linkAplicacaoExterna.getUrl(), hashMap.entrySet());
    }
    
}
