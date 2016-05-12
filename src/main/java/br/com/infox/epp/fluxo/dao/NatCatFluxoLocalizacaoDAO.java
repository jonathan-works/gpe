package br.com.infox.epp.fluxo.dao;

import static br.com.infox.epp.fluxo.query.NatCatFluxoLocalizacaoQuery.COUNT_NCF_LOCALIZACAO_BY_LOC_NCF;
import static br.com.infox.epp.fluxo.query.NatCatFluxoLocalizacaoQuery.DELETE_BY_NAT_CAT_FLUXO_AND_LOCALIZACAO;
import static br.com.infox.epp.fluxo.query.NatCatFluxoLocalizacaoQuery.GET_NAT_CAT_FLUXO_LOCALIZACAO_BY_LOC_NCF;
import static br.com.infox.epp.fluxo.query.NatCatFluxoLocalizacaoQuery.LIST_BY_NAT_CAT_FLUXO;
import static br.com.infox.epp.fluxo.query.NatCatFluxoLocalizacaoQuery.QUERY_PARAM_LOCALIZACAO;
import static br.com.infox.epp.fluxo.query.NatCatFluxoLocalizacaoQuery.QUERY_PARAM_NAT_CAT_FLUXO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Localizacao_;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.Papel_;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.CategoriaItem;
import br.com.infox.epp.fluxo.entity.CategoriaItem_;
import br.com.infox.epp.fluxo.entity.Categoria_;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.FluxoPapel;
import br.com.infox.epp.fluxo.entity.FluxoPapel_;
import br.com.infox.epp.fluxo.entity.Fluxo_;
import br.com.infox.epp.fluxo.entity.Item;
import br.com.infox.epp.fluxo.entity.NatCatFluxoLocalizacao;
import br.com.infox.epp.fluxo.entity.NatCatFluxoLocalizacao_;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo_;
import br.com.infox.epp.processo.iniciar.NaturezaCategoriaFluxoItem;

@Stateless
@AutoCreate
@Name(NatCatFluxoLocalizacaoDAO.NAME)
public class NatCatFluxoLocalizacaoDAO extends DAO<NatCatFluxoLocalizacao> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "natCatFluxoLocalizacaoDAO";

    public void deleteByNatCatFluxoAndLocalizacao(NaturezaCategoriaFluxo ncf, Localizacao l) throws DAOException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(QUERY_PARAM_NAT_CAT_FLUXO, ncf);
        parameters.put(QUERY_PARAM_LOCALIZACAO, l);
        executeNamedQueryUpdate(DELETE_BY_NAT_CAT_FLUXO_AND_LOCALIZACAO, parameters);
    }

    public NatCatFluxoLocalizacao getByNatCatFluxoAndLocalizacao(NaturezaCategoriaFluxo ncf, Localizacao l) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(QUERY_PARAM_NAT_CAT_FLUXO, ncf);
        parameters.put(QUERY_PARAM_LOCALIZACAO, l);
        return getNamedSingleResult(GET_NAT_CAT_FLUXO_LOCALIZACAO_BY_LOC_NCF, parameters);
    }

    public boolean existsNatCatFluxoLocalizacao(NaturezaCategoriaFluxo ncf, Localizacao l) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(QUERY_PARAM_NAT_CAT_FLUXO, ncf);
        parameters.put(QUERY_PARAM_LOCALIZACAO, l);
        Long resultCount = getNamedSingleResult(COUNT_NCF_LOCALIZACAO_BY_LOC_NCF, parameters);
        return resultCount != null && resultCount > 0;
    }

    public List<NaturezaCategoriaFluxoItem> listByLocalizacaoAndPapel(Localizacao localizacao, Papel papel) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<NaturezaCategoriaFluxoItem> cq = cb.createQuery(NaturezaCategoriaFluxoItem.class);
        Root<NatCatFluxoLocalizacao> natCatFluxoLocalizacao = cq.from(NatCatFluxoLocalizacao.class);
        Join<NatCatFluxoLocalizacao, NaturezaCategoriaFluxo> natCatFluxo = natCatFluxoLocalizacao.join(NatCatFluxoLocalizacao_.naturezaCategoriaFluxo, JoinType.INNER);
        Join<NaturezaCategoriaFluxo, Categoria> categoria = natCatFluxo.join(NaturezaCategoriaFluxo_.categoria, JoinType.INNER);
        Join<Categoria, CategoriaItem> categoriaItem = categoria.join(Categoria_.categoriaItemList, JoinType.LEFT);
        Join<CategoriaItem, Item> item = categoriaItem.join(CategoriaItem_.item, JoinType.LEFT);
        cq.select(cb.construct(NaturezaCategoriaFluxoItem.class, natCatFluxo, item));
        
        Subquery<Integer> subquery = cq.subquery(Integer.class);
        subquery.select(cb.literal(1));
        Root<Fluxo> fluxo = subquery.from(Fluxo.class);
        Join<Fluxo, FluxoPapel> fluxoPapel = fluxo.join(Fluxo_.fluxoPapelList, JoinType.INNER);
        subquery.where(
            cb.equal(fluxoPapel.get(FluxoPapel_.papel).get(Papel_.idPapel), papel.getIdPapel()),
            cb.equal(natCatFluxo.get(NaturezaCategoriaFluxo_.fluxo).get(Fluxo_.idFluxo), fluxo.get(Fluxo_.idFluxo))
        );
        
        cq.where(
            cb.equal(natCatFluxoLocalizacao.get(NatCatFluxoLocalizacao_.localizacao).get(Localizacao_.idLocalizacao), localizacao.getIdLocalizacao()),
            cb.exists(subquery)
        );
        return getEntityManager().createQuery(cq).getResultList();
    }

    public List<NatCatFluxoLocalizacao> listByNaturezaCategoriaFluxo(NaturezaCategoriaFluxo ncf) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(QUERY_PARAM_NAT_CAT_FLUXO, ncf);
        return getNamedResultList(LIST_BY_NAT_CAT_FLUXO, parameters);
    }

}
