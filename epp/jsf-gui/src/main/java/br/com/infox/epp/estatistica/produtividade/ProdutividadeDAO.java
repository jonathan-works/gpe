package br.com.infox.epp.estatistica.produtividade;

import static br.com.infox.epp.estatistica.produtividade.ProdutividadeQuery.PARAM_COUNT;
import static br.com.infox.epp.estatistica.produtividade.ProdutividadeQuery.PARAM_DATA_FIM;
import static br.com.infox.epp.estatistica.produtividade.ProdutividadeQuery.PARAM_DATA_INICIO;
import static br.com.infox.epp.estatistica.produtividade.ProdutividadeQuery.PARAM_FLUXO;
import static br.com.infox.epp.estatistica.produtividade.ProdutividadeQuery.PARAM_TAREFA;
import static br.com.infox.epp.estatistica.produtividade.ProdutividadeQuery.PARAM_START;
import static br.com.infox.epp.estatistica.produtividade.ProdutividadeQuery.PARAM_USUARIO;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Localizacao_;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.Papel_;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioLogin_;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.Fluxo_;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo_;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.Processo_;
import br.com.infox.epp.tarefa.entity.ProcessoTarefa;
import br.com.infox.epp.tarefa.entity.ProcessoTarefa_;
import br.com.infox.epp.tarefa.entity.Tarefa;
import br.com.infox.epp.tarefa.entity.Tarefa_;
import br.com.infox.ibpm.task.entity.UsuarioTaskInstance;
import br.com.infox.ibpm.task.entity.UsuarioTaskInstance_;

@Stateless
@AutoCreate
@Name(ProdutividadeDAO.NAME)
public class ProdutividadeDAO extends DAO<ProdutividadeBean> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "produtividadeDAO";

    public List<ProdutividadeBean> listProdutividade(Map<String, Object> params) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ProdutividadeBean> query = cb.createQuery(ProdutividadeBean.class);
        QueryBean queryBean = buildBaseQuery(params, query);
        
        query.select(cb.construct(ProdutividadeBean.class,
                queryBean.tarefa.get(Tarefa_.prazo),
                queryBean.localizacao.get(Localizacao_.localizacao),
                queryBean.papel.get(Papel_.nome),
                queryBean.usuario.get(UsuarioLogin_.nomeUsuario),
                queryBean.tarefa.get(Tarefa_.tarefa),
                queryBean.fluxo.get(Fluxo_.fluxo),
                cb.avg(queryBean.processoTarefa.get(ProcessoTarefa_.tempoGasto)),
                cb.min(queryBean.processoTarefa.get(ProcessoTarefa_.tempoGasto)),
                cb.max(queryBean.processoTarefa.get(ProcessoTarefa_.tempoGasto)),
                cb.count(queryBean.processoTarefa),
                queryBean.tarefa.get(Tarefa_.tipoPrazo)
        ));
        
        return setPaginationParams(params, getEntityManager().createQuery(query)).getResultList();
    }

    public Long totalProdutividades(Map<String, Object> params) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Integer> query = cb.createQuery(Integer.class);
        QueryBean queryBean = buildBaseQuery(params, query);
        query.select(queryBean.tarefa.get(Tarefa_.idTarefa));
        return Long.valueOf(getEntityManager().createQuery(query).getResultList().size());
    }

    private TypedQuery<ProdutividadeBean> setPaginationParams(Map<String, Object> params, TypedQuery<ProdutividadeBean> base) {
        if (params.containsKey(PARAM_START)) {
            base.setFirstResult((int) params.get(PARAM_START));
        }
        if (params.containsKey(PARAM_COUNT)) {
            base.setMaxResults((int) params.get(PARAM_COUNT));
        }
        return base;
    }
    
    private QueryBean buildBaseQuery(Map<String, Object> params, CriteriaQuery<?> query) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        Root<ProcessoTarefa> processoTarefa = query.from(ProcessoTarefa.class);
        Root<UsuarioTaskInstance> usuarioTaskInstance = query.from(UsuarioTaskInstance.class);
        Join<UsuarioTaskInstance, Papel> papel = usuarioTaskInstance.join(UsuarioTaskInstance_.papel, JoinType.INNER);
        Join<UsuarioTaskInstance, UsuarioLogin> usuario = usuarioTaskInstance.join(UsuarioTaskInstance_.usuario, JoinType.INNER);
        Join<UsuarioTaskInstance, Localizacao> localizacao = usuarioTaskInstance.join(UsuarioTaskInstance_.localizacao, JoinType.INNER);
        Join<ProcessoTarefa, Tarefa> tarefa = processoTarefa.join(ProcessoTarefa_.tarefa, JoinType.INNER);
        Join<ProcessoTarefa, Processo> processo = processoTarefa.join(ProcessoTarefa_.processo, JoinType.INNER);
        Join<Processo, NaturezaCategoriaFluxo> ncf = processo.join(Processo_.naturezaCategoriaFluxo, JoinType.INNER);
        Join<NaturezaCategoriaFluxo, Fluxo> fluxo = ncf.join(NaturezaCategoriaFluxo_.fluxo, JoinType.INNER);
        
        query.where(cb.equal(processoTarefa.get(ProcessoTarefa_.taskInstance), usuarioTaskInstance.get(UsuarioTaskInstance_.idTaskInstance)));
        
        if (params.containsKey(PARAM_FLUXO)) {
            query.where(query.getRestriction(), cb.equal(fluxo, params.get(PARAM_FLUXO)));
        }
        
        if (params.containsKey(PARAM_TAREFA)) {
            query.where(query.getRestriction(), cb.equal(tarefa.get(Tarefa_.tarefa), params.get(PARAM_TAREFA)));
        }
        
        if (params.containsKey(PARAM_USUARIO)) {
            query.where(query.getRestriction(), cb.equal(usuario, params.get(PARAM_USUARIO)));
        }
        
        if (params.containsKey(PARAM_DATA_INICIO)) {
            query.where(query.getRestriction(), cb.greaterThanOrEqualTo(processoTarefa.get(ProcessoTarefa_.dataInicio), (Date) params.get(PARAM_DATA_INICIO)));
        }
        
        if (params.containsKey(PARAM_DATA_FIM)) {
            query.where(query.getRestriction(), cb.lessThanOrEqualTo(processoTarefa.get(ProcessoTarefa_.dataFim), (Date) params.get(PARAM_DATA_FIM)));
        }
        
        query.orderBy(cb.asc(usuario.get(UsuarioLogin_.nomeUsuario)));
        query.groupBy(
                tarefa.get(Tarefa_.idTarefa),
                tarefa.get(Tarefa_.prazo),
                localizacao.get(Localizacao_.localizacao),
                papel.get(Papel_.nome),
                usuario.get(UsuarioLogin_.nomeUsuario),
                tarefa.get(Tarefa_.tarefa),
                fluxo.get(Fluxo_.fluxo),
                tarefa.get(Tarefa_.tipoPrazo)
        );
        return new QueryBean(tarefa, fluxo, localizacao, papel, usuario, processoTarefa);
    }
    
    private static class QueryBean {
        private Path<Tarefa> tarefa;
        private Path<Fluxo> fluxo;
        private Path<Localizacao> localizacao;
        private Path<Papel> papel;
        private Path<UsuarioLogin> usuario;
        private Path<ProcessoTarefa> processoTarefa;
        
        public QueryBean(Path<Tarefa> tarefa, Path<Fluxo> fluxo, Path<Localizacao> localizacao, Path<Papel> papel,
                Path<UsuarioLogin> usuario, Path<ProcessoTarefa> processoTarefa) {
            this.tarefa = tarefa;
            this.fluxo = fluxo;
            this.localizacao = localizacao;
            this.papel = papel;
            this.usuario = usuario;
            this.processoTarefa = processoTarefa;
        }
    }
}
