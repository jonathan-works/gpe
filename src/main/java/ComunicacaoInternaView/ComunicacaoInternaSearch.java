package ComunicacaoInternaView;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.Fluxo_;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo_;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao_;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.Processo_;
import br.com.infox.epp.system.Parametros;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ComunicacaoInternaSearch {
    
    public List<Processo> getComunicacoesInternas(Integer idProcesso) {
        String codigoFluxoComunicacaoInterna = Parametros.CODIGO_FLUXO_COMUNICACAO_INTERNA.getValue();
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Processo> cq = cb.createQuery(Processo.class);
        Root<Processo> from = cq.from(Processo.class);
        Join<Processo, NaturezaCategoriaFluxo> natCatFluxo = from.join(Processo_.naturezaCategoriaFluxo, JoinType.INNER);
        Join<NaturezaCategoriaFluxo, Fluxo> fluxo = natCatFluxo.join(NaturezaCategoriaFluxo_.fluxo, JoinType.INNER);
        cq.select(from);
        cq.where(
                cb.equal(from.get(Processo_.processoRoot).get(Processo_.idProcesso), cb.literal(idProcesso)),
                cb.equal(fluxo.get(Fluxo_.codFluxo), cb.literal(codigoFluxoComunicacaoInterna))
        );
        return getEntityManager().createQuery(cq).getResultList();
    }
    
    public List<ModeloComunicacao> getComunicacoesInternasNaoFinalizadas(Integer idProcesso) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ModeloComunicacao> cq = cb.createQuery(ModeloComunicacao.class);
        Root<ModeloComunicacao> modeloComunicacao = cq.from(ModeloComunicacao.class);
        cq.select(modeloComunicacao);
        cq.where(
                cb.equal(modeloComunicacao.get(ModeloComunicacao_.processo).get(Processo_.idProcesso), cb.literal(idProcesso)),
                cb.isTrue(modeloComunicacao.get(ModeloComunicacao_.finalizada))
        );
        return getEntityManager().createQuery(cq).getResultList();
    }
    
    public EntityManager getEntityManager() {
        return EntityManagerProducer.getEntityManager();
    }

}
