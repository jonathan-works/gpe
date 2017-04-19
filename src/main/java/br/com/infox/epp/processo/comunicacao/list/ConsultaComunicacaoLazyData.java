package br.com.infox.epp.processo.comunicacao.list;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.primefaces.model.SortOrder;

import br.com.infox.core.list.AbstractLazyData;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Localizacao_;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao_;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao_;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacao;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacao_;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.Processo_;

public class ConsultaComunicacaoLazyData extends AbstractLazyData<ModeloComunicacao> {

    private static final long serialVersionUID = 1L;
    public static final String FILTER_PROCESSO = "processo";
    public static final String FILTER_TIPO_COMUNICACAO = "tipoComunicacao";
    public static final String FILTER_EXPEDIDA = "expedida";
        
    @Override
    public TypedQuery<Long> createQueryCount() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<ModeloComunicacao> from = cq.from(ModeloComunicacao.class);
        cq.select(cb.count(from));
        cq.where(
            cb.isTrue(from.get(ModeloComunicacao_.finalizada)),
            cb.equal(from.get(ModeloComunicacao_.localizacaoResponsavelAssinatura).get(Localizacao_.idLocalizacao), cb.literal(getIdLocalizacao())),
            cb.or(
                cb.isNull(from.get(ModeloComunicacao_.perfilResponsavelAssinatura)),
                cb.equal(from.get(ModeloComunicacao_.perfilResponsavelAssinatura), cb.literal(getIdPerfilTemplate()))
            )
        );
        appendDynamicFilter(cb, cq, from);
        cq.orderBy(cb.asc(from.get(ModeloComunicacao_.id)));
        return getEntityManager().createQuery(cq);
    }

    @Override
    public TypedQuery<ModeloComunicacao> createQuery(String sortField, SortOrder sortOrder) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ModeloComunicacao> cq = cb.createQuery(ModeloComunicacao.class);
        Root<ModeloComunicacao> modeloComunicao = cq.from(ModeloComunicacao.class);
        cq.select(modeloComunicao);
        cq.where(
            cb.isTrue(modeloComunicao.get(ModeloComunicacao_.finalizada)),
            cb.equal(modeloComunicao.get(ModeloComunicacao_.localizacaoResponsavelAssinatura).get(Localizacao_.idLocalizacao), cb.literal(getIdLocalizacao())),
            cb.or(
                cb.isNull(modeloComunicao.get(ModeloComunicacao_.perfilResponsavelAssinatura)),
                cb.equal(modeloComunicao.get(ModeloComunicacao_.perfilResponsavelAssinatura), cb.literal(getIdPerfilTemplate()))
            )
        );
        appendDynamicFilter(cb, cq, modeloComunicao);
        cq.orderBy(cb.asc(modeloComunicao.get(ModeloComunicacao_.id)));
        return getEntityManager().createQuery(cq);
    }

    private void appendDynamicFilter(CriteriaBuilder cb, CriteriaQuery<?> cq, Root<ModeloComunicacao> modeloComunicacao) {
        if(isFilterEnabled(FILTER_EXPEDIDA) && getExpedida()) {
            cq.where (
                cq.getRestriction(),
                cb.exists(createSubqueryExpedida(cb, cq, modeloComunicacao)).not()
            );
        } else {
            cq.where (
                cq.getRestriction(),
                cb.exists(createSubqueryExpedida(cb, cq, modeloComunicacao))
            );
        }

        if(isFilterEnabled(FILTER_PROCESSO)) {
            Join<ModeloComunicacao, Processo> processo = modeloComunicacao.join(ModeloComunicacao_.processo, JoinType.INNER);
            cq.where(
                   cq.getRestriction(),
                   cb.like(processo.get(Processo_.numeroProcesso), cb.literal("%" + getNumeroProcesso() + "%"))
               );
        }
        
        if(isFilterEnabled(FILTER_TIPO_COMUNICACAO)){
            cq.where(
                cq.getRestriction(),
                cb.equal(modeloComunicacao.get(ModeloComunicacao_.tipoComunicacao).get(TipoComunicacao_.id), cb.literal(getTipoComunicacao().getId()))
            );
        }
        
    }
    
    private Subquery<Integer> createSubqueryExpedida(CriteriaBuilder cb, CriteriaQuery<?> cq, Path<ModeloComunicacao> modeloComunicacao) {
        Subquery<Integer> subquery = cq.subquery(Integer.class);
        Root<DestinatarioModeloComunicacao> from = subquery.from(DestinatarioModeloComunicacao.class);
        subquery.where(
                    cb.equal(from.get(DestinatarioModeloComunicacao_.modeloComunicacao), modeloComunicacao),
                    cb.isFalse(from.get(DestinatarioModeloComunicacao_.expedido))
                );
        subquery.select(cb.literal(1));
        return subquery;
    }
    
    @Override
    public ModeloComunicacao getRowData(String rowKey) {
        for (ModeloComunicacao modelo : getResultList()) {
            if (modelo.getId().toString().equals(rowKey)) {
                return modelo;
            }   
        }
        return null;
    }
    
    @Override
    public Object getRowKey(ModeloComunicacao object) {
        return object.getId().toString();
    }
    
    public void setNumeroProcesso(String numeroProcesso) {
        addFilter(FILTER_PROCESSO, numeroProcesso);
    }
    
    public String getNumeroProcesso() {
        return getFilter(FILTER_PROCESSO, String.class);
    }
    
    public void setTipoComunicacao(TipoComunicacao tipoComunicacao) {
        addFilter(FILTER_TIPO_COMUNICACAO, tipoComunicacao);
    }
    
    public TipoComunicacao getTipoComunicacao() {
        return getFilter(FILTER_TIPO_COMUNICACAO, TipoComunicacao.class);
    }
    
    public void setExpedida(Boolean expedida) {
        addFilter(FILTER_EXPEDIDA, expedida);
    }
    
    public Boolean getExpedida() {
        return getFilter(FILTER_EXPEDIDA, Boolean.class);
    }
    
    private Integer getIdLocalizacao() {
        return Authenticator.getLocalizacaoAtual().getIdLocalizacao();
    }
    
    private Integer getIdPerfilTemplate() {
        return Authenticator.getUsuarioPerfilAtual().getPerfilTemplate().getId();
    }
    
    
   
}
