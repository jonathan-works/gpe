package br.com.infox.epp.processo.comunicacao.meioexpedicao;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import br.com.infox.core.persistence.PersistenceController;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class MeioExpedicaoSearch extends PersistenceController {

    public static final String CODIGO_MEIO_SISTEMA = "SI";
    public static final String CODIGO_MEIO_IMPRESSAO = "IM";
    public static final String CODIGO_MEIO_DIARIO_OFICIAL = "DO";
    
    public List<MeioExpedicao> getMeiosExpedicaoAtivos() {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<MeioExpedicao> query = cb.createQuery(MeioExpedicao.class);
        Root<MeioExpedicao> meioExpedicao = query.from(MeioExpedicao.class);
        query.where(cb.isTrue(meioExpedicao.get(MeioExpedicao_.ativo)));
        query.orderBy(cb.asc(meioExpedicao.get(MeioExpedicao_.descricao)));
        return entityManager.createQuery(query).getResultList();
    }
    
    public List<MeioExpedicao> getMeiosExpedicaoAtivosNaoEletronicos() {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<MeioExpedicao> query = cb.createQuery(MeioExpedicao.class);
        Root<MeioExpedicao> meioExpedicao = query.from(MeioExpedicao.class);
        query.where(cb.isTrue(meioExpedicao.get(MeioExpedicao_.ativo)), cb.isFalse(meioExpedicao.get(MeioExpedicao_.eletronico)));
        query.orderBy(cb.asc(meioExpedicao.get(MeioExpedicao_.descricao)));
        return entityManager.createQuery(query).getResultList();
    }
    
    public MeioExpedicao getMeioExpedicaoSistema() {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<MeioExpedicao> query = cb.createQuery(MeioExpedicao.class);
        Root<MeioExpedicao> meioExpedicao = query.from(MeioExpedicao.class);
        query.where(cb.equal(meioExpedicao.get(MeioExpedicao_.codigo), CODIGO_MEIO_SISTEMA));
        return entityManager.createQuery(query).getSingleResult();
    }
    
    public MeioExpedicao getMeioExpedicaoImpressao() {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<MeioExpedicao> query = cb.createQuery(MeioExpedicao.class);
        Root<MeioExpedicao> meioExpedicao = query.from(MeioExpedicao.class);
        query.where(cb.equal(meioExpedicao.get(MeioExpedicao_.codigo), CODIGO_MEIO_IMPRESSAO));
        return entityManager.createQuery(query).getSingleResult();
    }
    
    public MeioExpedicao getMeioExpedicaoDiarioOficial() {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<MeioExpedicao> query = cb.createQuery(MeioExpedicao.class);
        Root<MeioExpedicao> meioExpedicao = query.from(MeioExpedicao.class);
        query.where(cb.equal(meioExpedicao.get(MeioExpedicao_.codigo), CODIGO_MEIO_DIARIO_OFICIAL));
        return entityManager.createQuery(query).getSingleResult();
    }
}
