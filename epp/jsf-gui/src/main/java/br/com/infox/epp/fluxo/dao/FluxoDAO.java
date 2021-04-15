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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jbpm.taskmgmt.def.Task;
import br.com.infox.core.dao.DAO;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Localizacao_;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.Papel_;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.FluxoPapel;
import br.com.infox.epp.fluxo.entity.FluxoPapel_;
import br.com.infox.epp.fluxo.entity.Fluxo_;
import br.com.infox.epp.fluxo.entity.NatCatFluxoLocalizacao;
import br.com.infox.epp.fluxo.entity.NatCatFluxoLocalizacao_;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo_;
import br.com.infox.epp.fluxo.entity.Natureza_;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.Processo_;

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

        Subquery<Integer> sq = cq.subquery(Integer.class);
        Root<NaturezaCategoriaFluxo> ncf = sq.from(NaturezaCategoriaFluxo.class);
        Join<NaturezaCategoriaFluxo, Fluxo> fluxo = ncf.join(NaturezaCategoriaFluxo_.fluxo);
        Path<Natureza> natureza = ncf.join(NaturezaCategoriaFluxo_.natureza);

        sq.select(fluxo.get(Fluxo_.idFluxo));
        sq.distinct(true);
        sq.where(
        		cb.equal(fluxo.get(Fluxo_.ativo), true),
        		cb.equal(natureza.get(Natureza_.primaria), true)
		);

        Root<Fluxo> fluxo2 = cq.from(Fluxo.class);
        cq.where(cb.in(fluxo2.get(Fluxo_.idFluxo)).value(sq));

        return getEntityManager().createQuery(cq).getResultList();
    }

    public List<Fluxo> getFluxosPrimarios() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Fluxo> cq = cb.createQuery(Fluxo.class);

        Subquery<Integer> sq = cq.subquery(Integer.class);
        Root<NaturezaCategoriaFluxo> ncf = sq.from(NaturezaCategoriaFluxo.class);
        Join<NaturezaCategoriaFluxo, Fluxo> fluxoSQ = ncf.join(NaturezaCategoriaFluxo_.fluxo);
        Path<Natureza> natureza = ncf.join(NaturezaCategoriaFluxo_.natureza);

        sq.select(fluxoSQ.get(Fluxo_.idFluxo));
        sq.distinct(true);
        sq.where(cb.equal(natureza.get(Natureza_.primaria), true));

        Root<Fluxo> f = cq.from(Fluxo.class);
        cq.where(cb.in(f.get(Fluxo_.idFluxo)).value(sq));
        cq.orderBy(cb.asc(f.get(Fluxo_.fluxo)));

        return getEntityManager().createQuery(cq).getResultList();
    }

    public List<Fluxo> getFluxosByLocalizacaoAndPapel(Localizacao localizacao, Papel papel, String search) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Fluxo> cq = cb.createQuery(Fluxo.class);
        Root<Fluxo> fluxo = cq.from(Fluxo.class);
        Join<Fluxo, FluxoPapel> fluxoPapel = fluxo.join(Fluxo_.fluxoPapelList, JoinType.INNER);

        Subquery<Integer> sq = cq.subquery(Integer.class);
        Root<NatCatFluxoLocalizacao> ncfl = sq.from(NatCatFluxoLocalizacao.class);
        sq.select(cb.literal(1));
        sq.where(
           cb.equal(ncfl.get(NatCatFluxoLocalizacao_.naturezaCategoriaFluxo).get(NaturezaCategoriaFluxo_.fluxo), fluxo),
           cb.equal(ncfl.get(NatCatFluxoLocalizacao_.localizacao).get(Localizacao_.idLocalizacao), localizacao.getIdLocalizacao())
        );

        cq.where(
           cb.like(cb.lower(fluxo.get(Fluxo_.fluxo)), "%" + search.toLowerCase() + "%"),
           cb.equal(fluxoPapel.get(FluxoPapel_.papel).get(Papel_.idPapel), papel.getIdPapel()),
           cb.exists(sq)
        );
        cq.orderBy(cb.asc(fluxo.get(Fluxo_.fluxo)));
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

    public Long getQuantidadeDeProcessosEmAndamento(Fluxo fluxo) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Processo> p = cq.from(Processo.class);
        Join<Processo, NaturezaCategoriaFluxo> ncf = p.join(Processo_.naturezaCategoriaFluxo, JoinType.INNER);
        Join<NaturezaCategoriaFluxo, Fluxo> f = ncf.join(NaturezaCategoriaFluxo_.fluxo, JoinType.INNER);
        cq.select(cb.count(f.get(Fluxo_.idFluxo)));
        cq.where(cb.equal(f.get(Fluxo_.idFluxo), fluxo.getIdFluxo()),
                cb.isNull(p.get(Processo_.dataFim)));
        return getEntityManager().createQuery(cq).getSingleResult();
    }
    
    public List<Task> getListaTaskByFluxo(Fluxo fluxo){
    	CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Task> cq = cb.createQuery(Task.class);
        Root<Task> taskRoot = cq.from(Task.class);
        cq.select(taskRoot);
        
        List<Task> listaTarefa = getEntityManager().createQuery(cq).getResultList();
        Iterator<Task> iteratorTask = listaTarefa.iterator(); 
        while(iteratorTask.hasNext()) {
        	Task taskIt = iteratorTask.next();
        	if(!taskIt.getProcessDefinition().getName().equals(fluxo.getFluxo())) {
        		iteratorTask.remove();
        	} else {
        		for(Task task : listaTarefa) {
        			if(task.getProcessDefinition().getName().equals(taskIt.getProcessDefinition().getName()) && task.getProcessDefinition().getVersion() > taskIt.getProcessDefinition().getVersion()) {
        				iteratorTask.remove();
        				break;
        			}
        		}
        	}
        }
        
        
        return listaTarefa;
    }
}
