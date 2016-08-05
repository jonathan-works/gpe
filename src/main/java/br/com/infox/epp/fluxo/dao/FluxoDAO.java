package br.com.infox.epp.fluxo.dao;

import static br.com.infox.epp.fluxo.query.FluxoQuery.COUNT_FLUXO_BY_CODIGO;
import static br.com.infox.epp.fluxo.query.FluxoQuery.COUNT_FLUXO_BY_DESCRICAO;
import static br.com.infox.epp.fluxo.query.FluxoQuery.COUNT_PROCESSOS_ATRASADOS;
import static br.com.infox.epp.fluxo.query.FluxoQuery.COUNT_PROCESSOS_BY_FLUXO;
import static br.com.infox.epp.fluxo.query.FluxoQuery.FLUXO_BY_CODIGO;
import static br.com.infox.epp.fluxo.query.FluxoQuery.FLUXO_BY_DESCRICACAO;
import static br.com.infox.epp.fluxo.query.FluxoQuery.FLUXO_BY_NOME;
import static br.com.infox.epp.fluxo.query.FluxoQuery.LIST_ATIVOS;
import static br.com.infox.epp.fluxo.query.FluxoQuery.PARAM_CODIGO;
import static br.com.infox.epp.fluxo.query.FluxoQuery.PARAM_DESCRICAO;
import static br.com.infox.epp.fluxo.query.FluxoQuery.PARAM_FLUXO;
import static br.com.infox.epp.fluxo.query.FluxoQuery.PARAM_NOME;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.Fluxo_;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo_;
import br.com.infox.epp.fluxo.entity.Natureza_;

@Stateless
@AutoCreate
@Name(FluxoDAO.NAME)
public class FluxoDAO extends DAO<Fluxo> {

    private static final long serialVersionUID = -4180114886888382915L;
    public static final String NAME = "fluxoDAO";

    public List<Fluxo> getFluxosAtivosList() {
        return getNamedResultList(LIST_ATIVOS, null);
    }
    
    public List<Fluxo> getFluxosPrimariosAtivos() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Fluxo> cq = cb.createQuery(Fluxo.class);
        Root<NaturezaCategoriaFluxo> ncf = cq.from(NaturezaCategoriaFluxo.class);
        Join<NaturezaCategoriaFluxo, Fluxo> fluxo = ncf.join(NaturezaCategoriaFluxo_.fluxo);
        Path<Natureza> natureza = ncf.join(NaturezaCategoriaFluxo_.natureza);
        
        cq.select(fluxo);
        cq.distinct(true);
        cq.where(
        		cb.equal(fluxo.get(Fluxo_.ativo), true),
        		cb.equal(natureza.get(Natureza_.primaria), true)
		);
        
        return getEntityManager().createQuery(cq).getResultList();
    }
    

    public Long quantidadeProcessosAtrasados(Fluxo fluxo) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(PARAM_FLUXO, fluxo);
        return getNamedSingleResult(COUNT_PROCESSOS_ATRASADOS, map);
    }

    public Fluxo getFluxoByDescricao(String descricao) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_DESCRICAO, descricao);
        return getNamedSingleResult(FLUXO_BY_DESCRICACAO, parameters);
    }
    
    public Fluxo getFluxoByCodigo(String codigo) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_CODIGO, codigo);
        return getNamedSingleResult(FLUXO_BY_CODIGO, parameters);
    }

    public Fluxo getFluxoByName(String nomeFluxo) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_NOME, nomeFluxo);
        return getNamedSingleResult(FLUXO_BY_NOME, parameters);
    }

    public Long getQuantidadeDeProcessoAssociadosAFluxo(Fluxo fluxo) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(PARAM_FLUXO, fluxo);
        return getNamedSingleResult(COUNT_PROCESSOS_BY_FLUXO, parameters);
    }

    public boolean existeFluxoComCodigo(String codigo) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(PARAM_CODIGO, codigo);
        return (Long) getNamedSingleResult(COUNT_FLUXO_BY_CODIGO, parameters) > 0;
    }

    public boolean existeFluxoComDescricao(String descricao) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(PARAM_DESCRICAO, descricao);
        return (Long) getNamedSingleResult(COUNT_FLUXO_BY_DESCRICAO, parameters) > 0;
    }
}
