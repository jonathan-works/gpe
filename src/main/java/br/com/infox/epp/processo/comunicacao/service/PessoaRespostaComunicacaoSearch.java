package br.com.infox.epp.processo.comunicacao.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.pessoa.entity.PessoaFisica_;
import br.com.infox.epp.processo.comunicacao.PessoaRespostaComunicacao;
import br.com.infox.epp.processo.comunicacao.PessoaRespostaComunicacao_;
import br.com.infox.epp.processo.entity.Processo_;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class PessoaRespostaComunicacaoSearch extends PersistenceController {
    
    public List<PessoaRespostaComunicacao> listByComunicacao(Integer idComunicacao) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<PessoaRespostaComunicacao> cq = cb.createQuery(PessoaRespostaComunicacao.class);
        Root<PessoaRespostaComunicacao> pessoaRespostaComunicacao = cq.from(PessoaRespostaComunicacao.class);
        cq.select(pessoaRespostaComunicacao);
        cq.where(
            cb.equal(pessoaRespostaComunicacao.get(PessoaRespostaComunicacao_.comunicacao).get(Processo_.idProcesso), cb.literal(idComunicacao))
        );
        return getEntityManager().createQuery(cq).getResultList();
    }
    
    public PessoaRespostaComunicacao findByComunicacaoAndPessoa(Integer idComunicacao, Integer idPessoa) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<PessoaRespostaComunicacao> cq = cb.createQuery(PessoaRespostaComunicacao.class);
        Root<PessoaRespostaComunicacao> pessoaRespostaComunicacao = cq.from(PessoaRespostaComunicacao.class);
        cq.select(pessoaRespostaComunicacao);
        cq.where(
            cb.equal(pessoaRespostaComunicacao.get(PessoaRespostaComunicacao_.comunicacao).get(Processo_.idProcesso), cb.literal(idComunicacao)),
            cb.equal(pessoaRespostaComunicacao.get(PessoaRespostaComunicacao_.pessoaFisica).get(PessoaFisica_.idPessoa), cb.literal(idPessoa))
        );
        List<PessoaRespostaComunicacao> result = getEntityManager().createQuery(cq).setMaxResults(1).getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

}
