package br.com.infox.epp.unidadedecisora;

import java.text.MessageFormat;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.access.entity.Localizacao_;
import br.com.infox.epp.pessoa.entity.PessoaFisica_;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraColegiada;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraColegiadaMonocratica;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraColegiadaMonocratica_;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraMonocratica;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraMonocratica_;

@Stateless
public class UnidadeDecisoraMonocraticaSearch extends PersistenceController {

    public List<UnidadeDecisoraMonocratica> findAtivasForaDaUDC(UnidadeDecisoraColegiada udc) {
        return findAtivasForaDaUDC(udc, null);
    }
    
    public List<UnidadeDecisoraMonocratica> findAtivasForaDaUDC(UnidadeDecisoraColegiada _udc, String query) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<UnidadeDecisoraMonocratica> cq = cb.createQuery(UnidadeDecisoraMonocratica.class);
        
        From<?, UnidadeDecisoraMonocratica> udm = cq.from(UnidadeDecisoraMonocratica.class);
        
        Predicate isAtiva = cb.isTrue(udm.get(UnidadeDecisoraMonocratica_.ativo));
        
        Predicate restrictions = isAtiva;
        Predicate udcDiferente = createExisteRelacaoComUdcPredicate(cq, udm, _udc).not();
        restrictions = cb.and(restrictions, udcDiferente);
        
        if (!StringUtil.isEmpty(query)){
            String formattedQuery = MessageFormat.format("%{0}%", query.toLowerCase());
            Predicate nomeLikeQuery = cb.like(
                cb.lower( udm.get(UnidadeDecisoraMonocratica_.nome)), 
                formattedQuery
            );
            Predicate nomeChefeGabineteLikeQuery = cb.like(
                cb.lower( udm.join(UnidadeDecisoraMonocratica_.chefeGabinete, JoinType.INNER).get(PessoaFisica_.nome) ),
                formattedQuery
            );
            Predicate nomeLocalizacaoLikeQuery = cb.like(
                cb.lower( udm.join(UnidadeDecisoraMonocratica_.localizacao, JoinType.INNER).get(Localizacao_.localizacao) ),
                formattedQuery
            );
            restrictions = cb.and(restrictions, cb.or(
                nomeLikeQuery, nomeChefeGabineteLikeQuery, nomeLocalizacaoLikeQuery
            ));    
        }
        
        cq = cq.select(udm).where(restrictions);
        return getEntityManager().createQuery(cq).getResultList();
    }
    
    private Predicate createExisteRelacaoComUdcPredicate(CriteriaQuery<?> query, 
            From<?, UnidadeDecisoraMonocratica> _udm, UnidadeDecisoraColegiada _udc) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        Subquery<Integer> cq = query.subquery(Integer.class);
        
        From<?, UnidadeDecisoraColegiadaMonocratica> udcm = cq.from(UnidadeDecisoraColegiadaMonocratica.class);
        From<?, UnidadeDecisoraColegiada> udc = udcm.join(UnidadeDecisoraColegiadaMonocratica_.unidadeDecisoraColegiada, JoinType.INNER);
        From<?, UnidadeDecisoraMonocratica> udm = udcm.join(UnidadeDecisoraColegiadaMonocratica_.unidadeDecisoraMonocratica, JoinType.INNER);
        
        Predicate udcIgual = cb.equal(udc, _udc);
        Predicate udmIgual = cb.equal(udm, _udm);
        
        Predicate restriction = cb.and(udcIgual, udmIgual);
        
        return cb.exists(cq.select(cb.literal(1)).where(restriction));
    }

    public List<UnidadeDecisoraMonocratica> findAtivasByUDC(UnidadeDecisoraColegiada _udc) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<UnidadeDecisoraMonocratica> cq = cb.createQuery(UnidadeDecisoraMonocratica.class);
        
        From<?, UnidadeDecisoraColegiadaMonocratica> udcm = cq.from(UnidadeDecisoraColegiadaMonocratica.class);
        From<?, UnidadeDecisoraColegiada> udc = udcm.join(UnidadeDecisoraColegiadaMonocratica_.unidadeDecisoraColegiada, JoinType.INNER);
        From<?, UnidadeDecisoraMonocratica> udm = udcm.join(UnidadeDecisoraColegiadaMonocratica_.unidadeDecisoraMonocratica, JoinType.INNER);
        
        Predicate udcIgual = cb.equal(udc, _udc);
        Predicate isAtiva = cb.isTrue(udm.get(UnidadeDecisoraMonocratica_.ativo));
        
        Predicate restrictions = cb.and(udcIgual, isAtiva);
        
        cq = cq.select(udm).where(restrictions);
        return getEntityManager().createQuery(cq).getResultList();
    }

    public UnidadeDecisoraMonocratica getPresidente(UnidadeDecisoraColegiada _udc) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<UnidadeDecisoraMonocratica> cq = cb.createQuery(UnidadeDecisoraMonocratica.class);
        
        From<?, UnidadeDecisoraColegiadaMonocratica> udcm = cq.from(UnidadeDecisoraColegiadaMonocratica.class);
        From<?, UnidadeDecisoraColegiada> udc = udcm.join(UnidadeDecisoraColegiadaMonocratica_.unidadeDecisoraColegiada, JoinType.INNER);
        From<?, UnidadeDecisoraMonocratica> udm = udcm.join(UnidadeDecisoraColegiadaMonocratica_.unidadeDecisoraMonocratica, JoinType.INNER);
        
        Predicate udcIgual = cb.equal(udc, _udc);
        Predicate isPresidente = cb.isTrue(udcm.get(UnidadeDecisoraColegiadaMonocratica_.presidente));
        
        Predicate restrictions = cb.and(udcIgual, isPresidente);
        
        cq = cq.select(udm).where(restrictions);
        try {
            return getEntityManager().createQuery(cq).getSingleResult();
        } catch (NoResultException e){
            return null;
        }
    }

    public List<UnidadeDecisoraMonocratica> findAtivas() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<UnidadeDecisoraMonocratica> cq = cb.createQuery(UnidadeDecisoraMonocratica.class);
        Root<UnidadeDecisoraMonocratica> udm = cq.from(UnidadeDecisoraMonocratica.class);

        cq.select(udm);
        cq.where(cb.isTrue(udm.get(UnidadeDecisoraMonocratica_.ativo)));

        return getEntityManager().createQuery(cq).getResultList();
    }
    
    public List<UnidadeDecisoraMonocratica> findAtivas(String descricao) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<UnidadeDecisoraMonocratica> cq = cb.createQuery(UnidadeDecisoraMonocratica.class);
        Root<UnidadeDecisoraMonocratica> udm = cq.from(UnidadeDecisoraMonocratica.class);

        cq.select(udm);
        Predicate restrictions = cb.isTrue(udm.get(UnidadeDecisoraMonocratica_.ativo));
        if (!StringUtil.isEmpty(descricao)){
            String formattedQuery = MessageFormat.format("%{0}%", descricao.toLowerCase());
            Predicate nomeLikeQuery = cb.like(
                cb.lower( udm.get(UnidadeDecisoraMonocratica_.nome)), 
                formattedQuery
            );
            Predicate nomeChefeGabineteLikeQuery = cb.like(
                cb.lower( udm.join(UnidadeDecisoraMonocratica_.chefeGabinete, JoinType.INNER).get(PessoaFisica_.nome) ),
                formattedQuery
            );
            Predicate nomeLocalizacaoLikeQuery = cb.like(
                cb.lower( udm.join(UnidadeDecisoraMonocratica_.localizacao, JoinType.INNER).get(Localizacao_.localizacao) ),
                formattedQuery
            );
            restrictions = cb.and(restrictions, cb.or(
                nomeLikeQuery, nomeChefeGabineteLikeQuery, nomeLocalizacaoLikeQuery
            ));    
        }
        cq.where(restrictions);

        return getEntityManager().createQuery(cq).getResultList();
    }
}
