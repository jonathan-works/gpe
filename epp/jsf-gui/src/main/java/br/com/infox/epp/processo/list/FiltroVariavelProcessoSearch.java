package br.com.infox.epp.processo.list;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.fluxo.definicaovariavel.DefinicaoVariavelProcesso;
import br.com.infox.epp.fluxo.definicaovariavel.DefinicaoVariavelProcesso_;
import br.com.infox.seam.exception.BusinessRollbackException;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class FiltroVariavelProcessoSearch extends PersistenceController {

    public List<FiltroVariavelProcessoVO> getFiltros(Integer idFluxo) {
        if(idFluxo == null) {
            return Collections.emptyList();
        }
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<FiltroVariavelProcessoVO> query = cb.createQuery(FiltroVariavelProcessoVO.class);
        Root<DefinicaoVariavelProcesso> definicaoVariavelProcesso = query.from(DefinicaoVariavelProcesso.class);

        query.select(cb.construct(FiltroVariavelProcessoVO.class,
            definicaoVariavelProcesso.get(DefinicaoVariavelProcesso_.id),
            definicaoVariavelProcesso.get(DefinicaoVariavelProcesso_.label),
            definicaoVariavelProcesso.get(DefinicaoVariavelProcesso_.tipoPesquisaProcesso)
        ));

        query.where(
            cb.equal(definicaoVariavelProcesso.get(DefinicaoVariavelProcesso_.fluxo), idFluxo),
            cb.isNotNull(definicaoVariavelProcesso.get(DefinicaoVariavelProcesso_.tipoPesquisaProcesso))
        );

        query.orderBy(cb.asc(definicaoVariavelProcesso.get(DefinicaoVariavelProcesso_.label)));

        return getEntityManager().createQuery(query).getResultList();
    }

    public String getHqlFiltroVariavelProcesso(String hqlBase,
            Long idTipoFiltroVariavelProcesso,
            Object valorFiltroVariavelProcesso,
            Object valorFiltroVariavelProcessoComplemento
    ){
        String resultado = hqlBase;
        if(idTipoFiltroVariavelProcesso != null &&
                (valorFiltroVariavelProcesso != null || valorFiltroVariavelProcessoComplemento != null)){

            String hqlFiltroBase = " and exists(select 1 from ProcessoJbpm pjbpm, %s vi " +
                    " left join vi.processInstance vipi " +
                    " where pjbpm.processo = o and pjbpm.processInstance = vipi " +
                    " and vi.name = '%s' ";

            DefinicaoVariavelProcesso definicaoVariavelProcesso = getEntityManager().find(DefinicaoVariavelProcesso.class, idTipoFiltroVariavelProcesso);
            switch (definicaoVariavelProcesso.getTipoPesquisaProcesso()) {
            case D:
                if(valorFiltroVariavelProcesso == null &&
                        valorFiltroVariavelProcessoComplemento == null) {
                    break;
                }
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                resultado = resultado.concat(String.format(hqlFiltroBase
                    , "DateInstance"
                    , definicaoVariavelProcesso.getNome())
                );

                if(valorFiltroVariavelProcesso != null) {
                    resultado = resultado.concat(String.format(
                        " and vi.value >= cast('%s' as date) "
                        , dateFormat.format(valorFiltroVariavelProcesso))
                    );
                }

                if(valorFiltroVariavelProcessoComplemento != null) {
                    resultado = resultado.concat(String.format(
                        " and vi.value <= cast('%s 23:59:59.9999' as timestamp) "
                        , dateFormat.format(valorFiltroVariavelProcessoComplemento))
                    );
                }
                break;
            case I:
                if(valorFiltroVariavelProcesso == null) {
                    break;
                }
                resultado = resultado.concat(String.format(hqlFiltroBase.concat(" and vi.value = %d ")
                    , "LongInstance"
                    , definicaoVariavelProcesso.getNome()
                    , valorFiltroVariavelProcesso
                ));
                break;
            case N:
                if(valorFiltroVariavelProcesso == null) {
                    break;
                }
                resultado = resultado.concat(String.format(hqlFiltroBase.concat(" and vi.value = %s ")
                    , "DoubleInstance"
                    , definicaoVariavelProcesso.getNome()
                    , valorFiltroVariavelProcesso.toString().replace(",", ".")
                ));
                break;
            case B:
                if(valorFiltroVariavelProcesso == null) {
                    break;
                }
                resultado = resultado.concat(String.format(hqlFiltroBase.concat(" and vi.value = '%s' ")
                    , "StringInstance"
                    , definicaoVariavelProcesso.getNome()
                    , Boolean.valueOf((String)valorFiltroVariavelProcesso) ? "T" : "F"
                ));
                break;
            case T:
                if(StringUtil.isEmpty((String)valorFiltroVariavelProcesso)) {
                    break;
                }
                resultado = resultado.concat(String.format(hqlFiltroBase.concat(" and lower(vi.value) like concat('%%', '%s', '%%') ")
                    , "StringInstance"
                    , definicaoVariavelProcesso.getNome()
                    , ((String)valorFiltroVariavelProcesso).trim().toLowerCase()
                ));
                break;
            default:
                throw new BusinessRollbackException("Não implementado");
            }
            if(!hqlBase.equals(resultado)) {
                resultado = resultado.concat(")");
            }
        }
        return resultado;
    }

}
