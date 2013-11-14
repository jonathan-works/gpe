package br.com.infox.epp.fluxo.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.fluxo.entity.NatCatFluxoLocalizacao;
import br.com.infox.epp.fluxo.entity.NatCatFluxoLocalizacaoQuery;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.ibpm.entity.Localizacao;

@Name(NatCatFluxoLocalizacaoDAO.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class NatCatFluxoLocalizacaoDAO extends GenericDAO {

	private static final long serialVersionUID = -4306540893162078405L;
	public static final String NAME = "natCatFluxoLocalizacaoDAO";
	
	public void deleteByNatCatFluxoAndLocalizacao
										(NaturezaCategoriaFluxo ncf, Localizacao l) {
		Query q = entityManager.createQuery(NatCatFluxoLocalizacaoQuery.
											DELETE_BY_NAT_CAT_FLUXO_AND_LOCALIZCAO);
		q.setParameter(NatCatFluxoLocalizacaoQuery.QUERY_PARAM_NAT_CAT_FLUXO, ncf);
		q.setParameter(NatCatFluxoLocalizacaoQuery.QUERY_PARAM_LOCALIZACAO, l);
		q.executeUpdate();
	}
	
	public NatCatFluxoLocalizacao getByNatCatFluxoAndLocalizacao
										(NaturezaCategoriaFluxo ncf, Localizacao l) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(NatCatFluxoLocalizacaoQuery.QUERY_PARAM_NAT_CAT_FLUXO, ncf);
		map.put(NatCatFluxoLocalizacaoQuery.QUERY_PARAM_LOCALIZACAO, l);
		NatCatFluxoLocalizacao ncfl = getNamedSingleResult
						(NatCatFluxoLocalizacaoQuery.GET_NAT_CAT_FLUXO_LOCALIZACAO_BY_LOC_NCF, 
						 map);
		return ncfl;
	}
	
	public boolean existsNatCatFluxoLocalizacao(NaturezaCategoriaFluxo ncf, Localizacao l) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(NatCatFluxoLocalizacaoQuery.QUERY_PARAM_NAT_CAT_FLUXO, ncf);
		map.put(NatCatFluxoLocalizacaoQuery.QUERY_PARAM_LOCALIZACAO, l);
		Long resultCount = getNamedSingleResult(NatCatFluxoLocalizacaoQuery.
												COUNT_NCF_LOCALIZACAO_BY_LOC_NCF, map);
		return resultCount != null && resultCount > 0;
	}

	public List<NaturezaCategoriaFluxo> listByLocalizacaoAndPapel(Localizacao l, Papel p) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(NatCatFluxoLocalizacaoQuery.QUERY_PARAM_LOCALIZACAO, l);
		map.put(NatCatFluxoLocalizacaoQuery.QUERY_PARAM_PAPEL, p);
		List<NaturezaCategoriaFluxo> ncflList = getNamedResultList
						(NatCatFluxoLocalizacaoQuery.LIST_BY_LOCALIZACAO_AND_PAPEL, map);
		return ncflList;
	}
	
	public List<NatCatFluxoLocalizacao> listByNaturezaCategoriaFluxo(NaturezaCategoriaFluxo ncf) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(NatCatFluxoLocalizacaoQuery.QUERY_PARAM_NAT_CAT_FLUXO, ncf);
		return getNamedResultList(NatCatFluxoLocalizacaoQuery.LIST_BY_NAT_CAT_FLUXO,map);
	}
	
}