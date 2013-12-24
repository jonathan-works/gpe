package br.com.infox.epp.fluxo.dao;

import static br.com.infox.epp.fluxo.query.NatCatFluxoLocalizacaoQuery.COUNT_NCF_LOCALIZACAO_BY_LOC_NCF;
import static br.com.infox.epp.fluxo.query.NatCatFluxoLocalizacaoQuery.DELETE_BY_NAT_CAT_FLUXO_AND_LOCALIZCAO;
import static br.com.infox.epp.fluxo.query.NatCatFluxoLocalizacaoQuery.GET_NAT_CAT_FLUXO_LOCALIZACAO_BY_LOC_NCF;
import static br.com.infox.epp.fluxo.query.NatCatFluxoLocalizacaoQuery.LIST_BY_LOCALIZACAO_AND_PAPEL;
import static br.com.infox.epp.fluxo.query.NatCatFluxoLocalizacaoQuery.LIST_BY_NAT_CAT_FLUXO;
import static br.com.infox.epp.fluxo.query.NatCatFluxoLocalizacaoQuery.QUERY_PARAM_LOCALIZACAO;
import static br.com.infox.epp.fluxo.query.NatCatFluxoLocalizacaoQuery.QUERY_PARAM_NAT_CAT_FLUXO;
import static br.com.infox.epp.fluxo.query.NatCatFluxoLocalizacaoQuery.QUERY_PARAM_PAPEL;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.fluxo.entity.NatCatFluxoLocalizacao;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;

@Name(NatCatFluxoLocalizacaoDAO.NAME)
@AutoCreate
public class NatCatFluxoLocalizacaoDAO extends GenericDAO {

    private static final long serialVersionUID = -4306540893162078405L;
    public static final String NAME = "natCatFluxoLocalizacaoDAO";

    public void deleteByNatCatFluxoAndLocalizacao(NaturezaCategoriaFluxo ncf,
            Localizacao l) {
        Query q = getEntityManager().createQuery(DELETE_BY_NAT_CAT_FLUXO_AND_LOCALIZCAO);
        q.setParameter(QUERY_PARAM_NAT_CAT_FLUXO, ncf);
        q.setParameter(QUERY_PARAM_LOCALIZACAO, l);
        q.executeUpdate();
    }

    public NatCatFluxoLocalizacao getByNatCatFluxoAndLocalizacao(
            NaturezaCategoriaFluxo ncf, Localizacao l) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(QUERY_PARAM_NAT_CAT_FLUXO, ncf);
        map.put(QUERY_PARAM_LOCALIZACAO, l);
        NatCatFluxoLocalizacao ncfl = getNamedSingleResult(GET_NAT_CAT_FLUXO_LOCALIZACAO_BY_LOC_NCF, map);
        return ncfl;
    }

    public boolean existsNatCatFluxoLocalizacao(NaturezaCategoriaFluxo ncf,
            Localizacao l) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(QUERY_PARAM_NAT_CAT_FLUXO, ncf);
        map.put(QUERY_PARAM_LOCALIZACAO, l);
        Long resultCount = getNamedSingleResult(COUNT_NCF_LOCALIZACAO_BY_LOC_NCF, map);
        return resultCount != null && resultCount > 0;
    }

    public List<NaturezaCategoriaFluxo> listByLocalizacaoAndPapel(
            Localizacao l, Papel p) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(QUERY_PARAM_LOCALIZACAO, l);
        map.put(QUERY_PARAM_PAPEL, p);
        List<NaturezaCategoriaFluxo> ncflList = getNamedResultList(LIST_BY_LOCALIZACAO_AND_PAPEL, map);
        return ncflList;
    }

    public List<NatCatFluxoLocalizacao> listByNaturezaCategoriaFluxo(
            NaturezaCategoriaFluxo ncf) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(QUERY_PARAM_NAT_CAT_FLUXO, ncf);
        return getNamedResultList(LIST_BY_NAT_CAT_FLUXO, map);
    }

}
