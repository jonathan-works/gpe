package br.com.infox.epp.processo.linkExterno;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.epp.processo.entity.Processo;

@Stateless
public class LinkAplicacaoExternaSearch {

    //TODO: Remover inicialização e retirar comentário da injeção quando houver um DaoProvider 
    //@javax.inject.Inject 
    private Dao<LinkAplicacaoExterna, Integer> dao;
    
    @PostConstruct
    public void init(){
        dao = new Dao<LinkAplicacaoExterna, Integer>(LinkAplicacaoExterna.class) {};
    }
    
    public List<LinkAplicacaoExterna> carregarLinksAplicacaoExternaAtivos(Processo processo){
        CriteriaBuilder cb = dao.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<LinkAplicacaoExterna> cq = cb.createQuery(LinkAplicacaoExterna.class);
        
        Root<LinkAplicacaoExterna> link = cq.from(LinkAplicacaoExterna.class);
        Join<?,Processo> proc = link.join(LinkAplicacaoExterna_.processo);
        Predicate processoIgual = cb.equal(proc, processo);
        Predicate isAtivo = cb.isTrue(link.get(LinkAplicacaoExterna_.ativo));
        
        Predicate restrictions = cb.and(isAtivo, processoIgual);
        
        Order order = cb.asc(link.get(LinkAplicacaoExterna_.descricao));
        
        cq=cq.select(link).where(restrictions).orderBy(order);
        
        return dao.getEntityManager().createQuery(cq).getResultList();
    }
    
}
