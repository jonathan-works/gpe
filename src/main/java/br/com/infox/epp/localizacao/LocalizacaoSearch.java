package br.com.infox.epp.localizacao;

import java.text.MessageFormat;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.access.entity.Estrutura;
import br.com.infox.epp.access.entity.Estrutura_;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Localizacao_;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioLogin_;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.access.entity.UsuarioPerfil_;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class LocalizacaoSearch {

	public Localizacao getLocalizacaoByCodigo(String codigoLocalizacao) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Localizacao> cq = cb.createQuery(Localizacao.class);
		Root<Localizacao> estrutura = cq.from(Localizacao.class);
		Predicate codigoIgual = cb.equal(estrutura.get(Localizacao_.codigo), codigoLocalizacao);
		Predicate ativo = cb.isTrue(estrutura.get(Localizacao_.ativo));
		cq = cq.select(estrutura).where(cb.and(codigoIgual, ativo));
        try {
            return getEntityManager().createQuery(cq).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
	}
	
    public List<Localizacao> getLocalizacoesExternasWithDescricaoLike(Localizacao localizacaoRaiz, String descricao) {
        CriteriaQuery<Localizacao> query = createQueryLocalizacaoExternaByRaizDescricao(localizacaoRaiz, descricao);
        return getEntityManager().createQuery(query).getResultList();
    }
    
    private CriteriaQuery<Localizacao> createQueryLocalizacaoExternaByRaizDescricao(Localizacao localizacaoRaiz, String descricao) {
    	CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    	CriteriaQuery<Localizacao> query = cb.createQuery(Localizacao.class);
    	Root<Localizacao> from = query.from(Localizacao.class);
    	query.where(cb.isNull(from.get(Localizacao_.estruturaPai)),
    			cb.isTrue(from.get(Localizacao_.ativo)),
    			cb.like(cb.lower(from.get(Localizacao_.localizacao)), "%"+descricao.toLowerCase()+"%"));
    	if (localizacaoRaiz != null) {
    		query.where(query.getRestriction(),
    				cb.like(from.get(Localizacao_.caminhoCompleto), localizacaoRaiz.getCaminhoCompleto() + "%"));
    	}
    	query.orderBy(cb.asc(from.get(Localizacao_.caminhoCompleto)));
    	return query;
    }
    
    @SuppressWarnings("unchecked")
	public List<Localizacao> getLocalizacoesByRaizWithDescricaoLike(Localizacao localizacaoRaiz, String descricao, Integer maxResults) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    	CriteriaQuery<Localizacao> query = createQueryLocalizacaoExternaByRaizDescricao(localizacaoRaiz, descricao);
        Root<Localizacao> localizacao = (Root<Localizacao>) query.getRoots().iterator().next();
        query.where(query.getRestriction(),
        		cb.isNotNull(localizacao.get(Localizacao_.estruturaFilho)));
        query.orderBy(cb.asc(localizacao.get(Localizacao_.localizacao)));
        return getEntityManager().createQuery(query).setMaxResults(maxResults).getResultList();
    }
    
    @SuppressWarnings("unchecked")
    public List<Localizacao> getLocalizacoesByRaizWithDescricaoLikeContendoUsuario(Localizacao localizacaoRaiz, String descricao, Integer maxResults) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Localizacao> query = createQueryLocalizacaoExternaByRaizDescricao(localizacaoRaiz, descricao);
        Root<Localizacao> localizacao = (Root<Localizacao>) query.getRoots().iterator().next();
        
        Subquery<Integer> destinatario = query.subquery(Integer.class);
        Root<UsuarioPerfil> up = destinatario.from(UsuarioPerfil.class);
        Join<UsuarioPerfil, UsuarioLogin> ul = up.join(UsuarioPerfil_.usuarioLogin, JoinType.INNER);
        destinatario.where(cb.equal(up.get(UsuarioPerfil_.localizacao), localizacao),
                cb.isNotNull(ul.get(UsuarioLogin_.pessoaFisica)),
                cb.isTrue(up.get(UsuarioPerfil_.ativo)));
        destinatario.select(cb.literal(1));
        
        query.where(query.getRestriction(),
                cb.isNotNull(localizacao.get(Localizacao_.estruturaFilho)),
                cb.exists(destinatario));
        query.orderBy(cb.asc(localizacao.get(Localizacao_.localizacao)));
        return getEntityManager().createQuery(query).setMaxResults(maxResults).getResultList();
    }

    public Localizacao getLocalizacaoByPaiAndEstrutura(Localizacao locPai, String nomeEstrutura) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Localizacao> cq = cb.createQuery(Localizacao.class);
        Root<Localizacao> loc = cq.from(Localizacao.class);
        Join<Localizacao, Estrutura> estrutura = loc.join(Localizacao_.estruturaFilho);
        cq.select(loc);
        cq.where(cb.equal(loc.get(Localizacao_.localizacaoPai), locPai),
                cb.equal(estrutura.get(Estrutura_.nome), nomeEstrutura));

        try {
            return getEntityManager().createQuery(cq).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Localizacao> retrieveLocalizacaoByEstruturaFilho(Estrutura _estrutura){
        return retrieveLocalizacaoByEstruturaFilho(_estrutura, null);
    }
    
    public List<Localizacao> retrieveLocalizacaoByEstruturaFilho(Estrutura _estrutura, String query){
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Localizacao> cq = cb.createQuery(Localizacao.class);
        Root<Localizacao> loc = cq.from(Localizacao.class);
        Join<?, Estrutura> estrutura = loc.join(Localizacao_.estruturaFilho);
        
        Predicate restrictions = cb.equal(estrutura, _estrutura);
        restrictions = cb.and(restrictions, cb.isTrue(loc.get(Localizacao_.ativo)));
        if (!StringUtil.isEmpty(query)){
            String formattedQuery = MessageFormat.format("%{0}%", query.toLowerCase());
            restrictions = cb.and(restrictions, 
                cb.like(cb.lower(loc.get(Localizacao_.localizacao)), formattedQuery)
            );
        }
        
        cq.select(loc);
        cq.where(restrictions);
        return getEntityManager().createQuery(cq).getResultList();
    }
    
	private EntityManager getEntityManager() {
		return EntityManagerProducer.getEntityManager();
	}
}
