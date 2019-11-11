package br.com.infox.epp.processo.documento.action;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.entity.PastaCompartilhamento;
import br.com.infox.epp.processo.documento.entity.PastaCompartilhamento_;
import br.com.infox.epp.processo.documento.entity.Pasta_;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.Processo_;

@Stateless
public class PastaCompartilhamentoSearch {

    private EntityManager getEntityManager() {
        return EntityManagerProducer.getEntityManager();
    }

    /**
     * Retorna true caso uma pasta esteja compartilhada com algum processo
     */
    public Boolean possuiCompartilhamento(Pasta pasta) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
        Root<PastaCompartilhamento> pc = cq.from(PastaCompartilhamento.class);
        cq.select(cb.literal(1));
        cq.where(cb.equal(pc.get(PastaCompartilhamento_.pasta), pasta),
                cb.isTrue(pc.get(PastaCompartilhamento_.ativo)));
        try {
            Integer result = getEntityManager().createQuery(cq).getSingleResult();
            return Integer.valueOf(1).equals(result);
        } catch (NoResultException nre) {
            return false;
        }
    }

    /**
     * Retorna true caso a pasta do parâmetro esteja compartilhada com o processo do parâmetro
     */
    public Boolean possuiCompartilhamento(Pasta pasta, Processo processoAlvo) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
        Root<PastaCompartilhamento> pc = cq.from(PastaCompartilhamento.class);
        cq.select(cb.literal(1));
        cq.where(cb.equal(pc.get(PastaCompartilhamento_.pasta), pasta),
                cb.equal(pc.get(PastaCompartilhamento_.processoAlvo), processoAlvo),
                cb.isTrue(pc.get(PastaCompartilhamento_.ativo)));
        try {
            Integer result = getEntityManager().createQuery(cq).getSingleResult();
            return Integer.valueOf(1).equals(result);
        } catch (NoResultException nre) {
            return false;
        }
    }

    /**
     * Retorna o compartilhamento de uma pasta com um processo, caso exista
     */
    public PastaCompartilhamento getByPastaProcesso(Pasta pasta, Processo processoAlvo) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<PastaCompartilhamento> cq = cb.createQuery(PastaCompartilhamento.class);
        Root<PastaCompartilhamento> pc = cq.from(PastaCompartilhamento.class);
        cq.select(pc);
        cq.where(cb.equal(pc.get(PastaCompartilhamento_.pasta), pasta),
                cb.equal(pc.get(PastaCompartilhamento_.processoAlvo), processoAlvo));
        try {
            return getEntityManager().createQuery(cq).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Retorna os compartilhamentos de uma pasta
     */
    public List<PastaCompartilhamento> listByPasta(Pasta pasta) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<PastaCompartilhamento> cq = cb.createQuery(PastaCompartilhamento.class);
        Root<PastaCompartilhamento> pc = cq.from(PastaCompartilhamento.class);
        cq.select(pc);
        cq.where(cb.equal(pc.get(PastaCompartilhamento_.pasta), pasta));
        return getEntityManager().createQuery(cq).getResultList();
    }

    /**
     * Retorna as pastas que estão compartilhadas com o processo do parâmetro
     */
    public List<Pasta> listPastasCompartilhadas(Processo processoAlvo) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Pasta> cq = cb.createQuery(Pasta.class);
        Root<PastaCompartilhamento> pc = cq.from(PastaCompartilhamento.class);
        Join<PastaCompartilhamento, Pasta> pasta = pc.join(PastaCompartilhamento_.pasta, JoinType.INNER);
        Join<PastaCompartilhamento, Processo> procAlvo = pc.join(PastaCompartilhamento_.processoAlvo, JoinType.INNER);
        cq.select(pasta);
        cq.where(cb.equal(procAlvo.get(Processo_.idProcesso), processoAlvo.getIdProcesso()),
                cb.isTrue(pc.get(PastaCompartilhamento_.ativo)));
        cq.orderBy(cb.asc(procAlvo.get(Processo_.numeroProcesso)),
                cb.asc(pasta.get(Pasta_.nome)));
        return getEntityManager().createQuery(cq).getResultList();
    }
}
