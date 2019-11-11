package br.com.infox.epp.processo.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioLogin_;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo_;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.Processo_;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso_;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.sigilo.entity.SigiloProcesso;
import br.com.infox.epp.processo.sigilo.entity.SigiloProcessoPermissao;
import br.com.infox.epp.processo.sigilo.entity.SigiloProcessoPermissao_;
import br.com.infox.epp.processo.sigilo.entity.SigiloProcesso_;
import br.com.infox.epp.processo.status.entity.StatusProcesso;
import br.com.infox.epp.processo.status.entity.StatusProcesso_;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ProcessoSearch extends PersistenceController {
    
    public List<Processo> getProcessosNaoIniciados(UsuarioLogin usuarioLogin) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Processo> cq = cb.createQuery(Processo.class);
        Root<Processo> processo = cq.from(Processo.class);
        Root<StatusProcesso> statusProcesso = cq.from(StatusProcesso.class);
        Join<Processo, MetadadoProcesso> metaddoProcesso = processo.join(Processo_.metadadoProcessoList, JoinType.INNER);
        cq.select(processo);
        cq.where(
            cb.equal(metaddoProcesso.get(MetadadoProcesso_.metadadoType), cb.literal(EppMetadadoProvider.STATUS_PROCESSO.getMetadadoType())),
            cb.equal(statusProcesso.get(StatusProcesso_.idStatusProcesso).as(String.class), metaddoProcesso.get(MetadadoProcesso_.valor)),
            cb.equal(statusProcesso.get(StatusProcesso_.nome), cb.literal(StatusProcesso.STATUS_NAO_INICIADO)),
            cb.equal(processo.get(Processo_.usuarioCadastro).get(UsuarioLogin_.idUsuarioLogin), cb.literal(usuarioLogin.getIdUsuarioLogin())),
            cb.isNull(processo.get(Processo_.idJbpm)),
            cb.isNull(processo.get(Processo_.dataFim))
        );
        return getEntityManager().createQuery(cq).getResultList();
    }
    
    public static class ValorMetadado {
    	private Class<?> classe;
    	private String valor;
		
    	public ValorMetadado(Class<?> classe, String valor) {
			super();
			this.classe = classe;
			this.valor = valor;
		}

		public Class<?> getClasse() {
			return classe;
		}

		public String getValor() {
			return valor;
		}
    }
    
    public List<Processo> getProcessosContendoMetadados(Map<String, ValorMetadado> metadados) {
    	return getProcessosContendoNaturezaCategoriaMetadados(null, null, metadados);
    }
    
    public List<Processo> getProcessosContendoNaturezaCategoriaMetadados(Natureza natureza, Categoria categoria, Map<String, ValorMetadado> metadados) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Processo> cq = cb.createQuery(Processo.class);
        Root<Processo> processo = cq.from(Processo.class);
        Path<NaturezaCategoriaFluxo> ncf = processo.join(Processo_.naturezaCategoriaFluxo);
        
        List<Predicate> where = new ArrayList<>();
        
        if(natureza != null) {
        	where.add(cb.equal(ncf.get(NaturezaCategoriaFluxo_.natureza), natureza));
        }
        if(categoria != null) {
        	where.add(cb.equal(ncf.get(NaturezaCategoriaFluxo_.categoria), categoria));
        }
        
        for(String nomeMetadado : metadados.keySet()) {
        	ValorMetadado valorMetadado = metadados.get(nomeMetadado);
        	Class<?> classe = valorMetadado.getClasse();
        	String valor = valorMetadado.getValor();
        	
        	Subquery<Integer> subquery = cq.subquery(Integer.class);
        	Root<Processo> processoSubquery = subquery.from(Processo.class);
        	Path<MetadadoProcesso> metadado = processoSubquery.join(Processo_.metadadoProcessoList);
        	
        	subquery.select(cb.literal(1));
        	subquery.where(
        			cb.equal(processo.get(Processo_.idProcesso), processoSubquery.get(Processo_.idProcesso)),
        			cb.equal(metadado.get(MetadadoProcesso_.metadadoType), nomeMetadado),
        			cb.equal(metadado.get(MetadadoProcesso_.classType), classe),
        			cb.equal(metadado.get(MetadadoProcesso_.valor), valor)
        	);
        	
        	where.add(cb.exists(subquery));
        }
        
        cq.where(
        		where.toArray(new Predicate[0])
        );
        return getEntityManager().createQuery(cq).getResultList();
    }

    public Processo find(Integer id) {
        return getEntityManager().find(Processo.class, id);
    }
    
    protected Predicate createPredicateUsuarioPossuiPermissaoSigilo(AbstractQuery<?> query, From<?, Processo> processo, UsuarioLogin usuario){
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        Subquery<Integer> cq = query.subquery(Integer.class);
        Root<SigiloProcessoPermissao> permissao = cq.from(SigiloProcessoPermissao.class);
        Join<SigiloProcessoPermissao, SigiloProcesso> sigilo = permissao.join(SigiloProcessoPermissao_.sigiloProcesso, JoinType.INNER);
        
        Predicate[] restrictions = {
            cb.equal(permissao.get(SigiloProcessoPermissao_.usuario), usuario),
            cb.isTrue(permissao.get(SigiloProcessoPermissao_.ativo)),
            cb.equal(sigilo.get(SigiloProcesso_.processo), processo),
            cb.isTrue(sigilo.get(SigiloProcesso_.ativo)),
            cb.isTrue(sigilo.get(SigiloProcesso_.sigiloso))
        };
        
        cq = cq.select(cb.literal(1)).where(restrictions);
        
        return cb.exists(cq);
    }
    
    protected Predicate createPredicateIsProcessoSigiloso(AbstractQuery<?> query, From<?, Processo> processo){
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        
        Subquery<Integer> cq = query.subquery(Integer.class);
        Root<SigiloProcesso> sigilo = cq.from(SigiloProcesso.class);
        Predicate[] restrictions = {
            cb.equal(sigilo.get(SigiloProcesso_.processo), processo),
            cb.isTrue(sigilo.get(SigiloProcesso_.ativo)),
            cb.isTrue(sigilo.get(SigiloProcesso_.sigiloso))
        };
        cq = cq.select(cb.literal(1)).where(restrictions);
        
        return cb.exists(cq);
    }
    
    protected Predicate createPredicateUsuarioPodeVerProcesso(AbstractQuery<?> query, From<?, Processo> processo,
            UsuarioLogin usuario) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        Predicate processoNaoSigiloso = createPredicateIsProcessoSigiloso(query, processo).not();
        
        if (usuario == null)
            return processoNaoSigiloso;
        else
            return cb.or(processoNaoSigiloso,
                    createPredicateUsuarioPossuiPermissaoSigilo(query, processo, usuario));
    }
    
    protected <T> void addIfNotNull(Collection<T> list, T obj){
        if (obj != null)
            list.add(obj);
    }

}
