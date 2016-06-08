package br.com.infox.epp.entrega.documentos;

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

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento_;
import br.com.infox.epp.entrega.modelo.ClassificacaoDocumentoEntrega;
import br.com.infox.epp.entrega.modelo.ClassificacaoDocumentoEntrega_;
import br.com.infox.epp.entrega.modelo.ModeloEntrega;
import br.com.infox.epp.entrega.modelo.TipoResponsavelEntrega;
import br.com.infox.epp.entrega.modelo.TipoResponsavelEntrega_;
import br.com.infox.epp.pessoa.entity.Pessoa;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaFisica_;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;
import br.com.infox.epp.pessoa.entity.PessoaJuridica_;
import br.com.infox.epp.pessoa.entity.Pessoa_;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.Documento_;
import br.com.infox.epp.processo.partes.entity.TipoParte;
import br.com.infox.epp.processo.partes.entity.TipoParte_;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class EntregaSearch extends PersistenceController {

	public List<ClassificacaoDocumentoEntrega> getClassificacoesDisponiveis(Entrega entrega, boolean obrigatorias) {
		EntityManager entityManager = getEntityManager();
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<ClassificacaoDocumentoEntrega> query = cb.createQuery(ClassificacaoDocumentoEntrega.class);
		Root<ClassificacaoDocumentoEntrega> classificacaoEntrega = query.from(ClassificacaoDocumentoEntrega.class);
		Join<ClassificacaoDocumentoEntrega, ClassificacaoDocumento> classificacao = classificacaoEntrega.join(ClassificacaoDocumentoEntrega_.classificacaoDocumento, JoinType.INNER);
		query.orderBy(cb.asc(classificacao.get(ClassificacaoDocumento_.descricao)));
		
		Subquery<Integer> subquery = query.subquery(Integer.class);
		subquery.select(cb.literal(1));
		Root<Documento> documento = subquery.from(Documento.class);
		subquery.where(
			cb.equal(documento.get(Documento_.pasta), entrega.getPasta()),
			cb.equal(documento.get(Documento_.classificacaoDocumento), classificacao)
		);

		Predicate predicateObrigatorias = obrigatorias ? cb.isTrue(classificacaoEntrega.get(ClassificacaoDocumentoEntrega_.obrigatorio)) 
				: cb.isFalse(classificacaoEntrega.get(ClassificacaoDocumentoEntrega_.obrigatorio));
		
		query.where(
			cb.equal(classificacaoEntrega.get(ClassificacaoDocumentoEntrega_.modeloEntrega), entrega.getModeloEntrega()),
			predicateObrigatorias,
			cb.or(
				cb.isTrue(classificacaoEntrega.get(ClassificacaoDocumentoEntrega_.multiplosDocumentos)),
				cb.exists(subquery).not()
			)
		);
		
		return entityManager.createQuery(query).getResultList();
	}
	
	public List<TipoParte> getTiposParte(Entrega entrega) {
		EntityManager entityManager = getEntityManager();
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<TipoParte> query = cb.createQuery(TipoParte.class);
		Root<TipoResponsavelEntrega> tipoResponsavelEntrega = query.from(TipoResponsavelEntrega.class);
		Join<TipoResponsavelEntrega, TipoParte> tipoParte = tipoResponsavelEntrega.join(TipoResponsavelEntrega_.tipoParte, JoinType.INNER);
		
		query.select(tipoParte);
		query.orderBy(cb.asc(tipoParte.get(TipoParte_.descricao)));
		query.where(cb.equal(tipoResponsavelEntrega.get(TipoResponsavelEntrega_.modeloEntrega), entrega.getModeloEntrega()));
		
		return entityManager.createQuery(query).getResultList();
	}
	
	public List<Pessoa> getPessoasParaEntregaResponsavel(Entrega entrega, TipoParte tipoParte, String query) {
		EntityManager entityManager = getEntityManager();
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Pessoa> cq = cb.createQuery(Pessoa.class);
		Root<Pessoa> pessoa = cq.from(Pessoa.class);
		cq.select(pessoa);
		cq.orderBy(cb.asc(pessoa.get(Pessoa_.nome)));
		
		if (!query.matches("\\d+")) {
			cq.where(cb.like(cb.lower(pessoa.get(PessoaFisica_.nome)), "%" + query.toLowerCase() + "%"));
		} else {
			Subquery<Integer> pessoaFisicaQuery = cq.subquery(Integer.class);
			Root<PessoaFisica> pessoaFisica = pessoaFisicaQuery.from(PessoaFisica.class);
			pessoaFisicaQuery.select(cb.literal(1));
			pessoaFisicaQuery.where(cb.equal(pessoaFisica, pessoa), cb.equal(pessoaFisica.get(PessoaFisica_.cpf), query));
			
			Subquery<Integer> pessoaJuridicaQuery = cq.subquery(Integer.class);
			Root<PessoaJuridica> pessoaJuridica = pessoaJuridicaQuery.from(PessoaJuridica.class);
			pessoaJuridicaQuery.select(cb.literal(1));
			pessoaJuridicaQuery.where(cb.equal(pessoaJuridica, pessoa), cb.equal(pessoaJuridica.get(PessoaJuridica_.cnpj), query));
			
			cq.where(cb.or(cb.exists(pessoaFisicaQuery), cb.exists(pessoaJuridicaQuery)));
		}
		
		return entityManager.createQuery(cq).setMaxResults(10).getResultList();
	}
	
	public List<EntregaResponsavel> getResponsaveisDisponiveisVinculacao(EntregaResponsavel entregaResponsavel) {
		EntityManager entityManager = getEntityManager();
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<EntregaResponsavel> query = cb.createQuery(EntregaResponsavel.class);
		Root<EntregaResponsavel> root = query.from(EntregaResponsavel.class);
		Join<EntregaResponsavel, Pessoa> pessoa = root.join(EntregaResponsavel_.pessoa, JoinType.INNER);
		query.where(cb.equal(root.get(EntregaResponsavel_.entrega), entregaResponsavel.getEntrega()));
		if (entityManager.contains(entregaResponsavel)) {
			query.where(query.getRestriction(), cb.notEqual(root, entregaResponsavel));
		}
		query.orderBy(cb.asc(pessoa.get(Pessoa_.nome)));
		return entityManager.createQuery(query).getResultList();
	}
	
	public List<Documento> getDocumentosEntrega(Entrega entrega) {
		EntityManager entityManager = getEntityManager();
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Documento> query = cb.createQuery(Documento.class);
		Root<Documento> documento = query.from(Documento.class);
		query.where(cb.equal(documento.get(Documento_.pasta), entrega.getPasta()));
		return entityManager.createQuery(query).getResultList();
	}
	
	public List<EntregaResponsavel> getResponsaveisEntrega(Entrega entrega) {
		EntityManager entityManager = getEntityManager();
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<EntregaResponsavel> query = cb.createQuery(EntregaResponsavel.class);
		Root<EntregaResponsavel> root = query.from(EntregaResponsavel.class);
		query.where(cb.equal(root.get(EntregaResponsavel_.entrega), entrega));
		return entityManager.createQuery(query).getResultList();
	}
	
	public boolean existeDocumentoComClassificacao(Entrega entrega, ClassificacaoDocumento classificacaoDocumento) {
		EntityManager entityManager = getEntityManager();
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = cb.createQuery(Long.class);
		Root<Documento> documento = query.from(Documento.class);
		query.where(
			cb.equal(documento.get(Documento_.pasta), entrega.getPasta()),
			cb.equal(documento.get(Documento_.classificacaoDocumento), classificacaoDocumento)
		);
		query.select(cb.count(documento));
		return entityManager.createQuery(query).getSingleResult() > 0;
	}
	
	public List<ClassificacaoDocumento> getClassificacoesObrigatorias(Entrega entrega) {
		EntityManager entityManager = getEntityManager();
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<ClassificacaoDocumento> query = cb.createQuery(ClassificacaoDocumento.class);
		Root<ClassificacaoDocumentoEntrega> classificacaoEntrega = query.from(ClassificacaoDocumentoEntrega.class);
		Join<ClassificacaoDocumentoEntrega, ClassificacaoDocumento> classificacao = classificacaoEntrega.join(ClassificacaoDocumentoEntrega_.classificacaoDocumento, JoinType.INNER);
		query.where(
			cb.isTrue(classificacaoEntrega.get(ClassificacaoDocumentoEntrega_.obrigatorio)), 
			cb.equal(classificacaoEntrega.get(ClassificacaoDocumentoEntrega_.modeloEntrega), entrega.getModeloEntrega())
		);
		
		query.select(classificacao);
		return entityManager.createQuery(query).getResultList();
	}
	
	public List<TipoParte> getTiposParteObrigatorios(Entrega entrega) {
		EntityManager entityManager = getEntityManager();
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<TipoParte> query = cb.createQuery(TipoParte.class);
		Root<TipoResponsavelEntrega> tipoResponsavelEntrega = query.from(TipoResponsavelEntrega.class);
		Join<TipoResponsavelEntrega, TipoParte> tipoParte = tipoResponsavelEntrega.join(TipoResponsavelEntrega_.tipoParte, JoinType.INNER);
		query.where(
			cb.isTrue(tipoResponsavelEntrega.get(TipoResponsavelEntrega_.obrigatorio)), 
			cb.equal(tipoResponsavelEntrega.get(TipoResponsavelEntrega_.modeloEntrega), entrega.getModeloEntrega())
		);
		
		query.select(tipoParte);
		return entityManager.createQuery(query).getResultList();
	}
	
	public boolean existeResponsavelComTipoParte(Entrega entrega, TipoParte tipoParte) {
		EntityManager entityManager = getEntityManager();
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = cb.createQuery(Long.class);
		Root<EntregaResponsavel> entregaResponsavel = query.from(EntregaResponsavel.class);
		query.where(
			cb.equal(entregaResponsavel.get(EntregaResponsavel_.entrega), entrega),
			cb.equal(entregaResponsavel.get(EntregaResponsavel_.tipoParte), tipoParte)
		);
		query.select(cb.count(entregaResponsavel));
		return entityManager.createQuery(query).getSingleResult() > 0;
	}

	public boolean existeEntregaModeloLocalizacao(ModeloEntrega modeloEntrega, Localizacao localizacao){
		EntityManager entityManager = getEntityManager();
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Integer> query =  cb.createQuery(Integer.class);
		query.select(cb.literal(1));
		Root<Entrega> entrega = query.from(Entrega.class);
		query.where(cb.equal(entrega.get(Entrega_.localizacao), localizacao),cb.equal(entrega.get(Entrega_.modeloEntrega), modeloEntrega));
		try {
            Integer result = getEntityManager().createQuery(query).getSingleResult();
            return result == 1;
        } catch (NoResultException nre) {
            return false;
        }
	}
	
	public ClassificacaoDocumentoEntrega getClassificacaoDocumentoEntrega(ClassificacaoDocumento classificacaoDocumento, ModeloEntrega modeloEntrega) {
		EntityManager entityManager = getEntityManager();
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<ClassificacaoDocumentoEntrega> query = cb.createQuery(ClassificacaoDocumentoEntrega.class);
		Root<ClassificacaoDocumentoEntrega> root = query.from(ClassificacaoDocumentoEntrega.class);
		query.where(cb.equal(root.get(ClassificacaoDocumentoEntrega_.classificacaoDocumento), classificacaoDocumento));
		query.where(query.getRestriction(), cb.equal(root.get(ClassificacaoDocumentoEntrega_.modeloEntrega), modeloEntrega));
		return entityManager.createQuery(query).getSingleResult();
	}
	
	public List<EntregaResponsavel> getResponsaveisEntrega(Entrega entrega, EntregaResponsavel responsavelVinculadoPai) {
		EntityManager entityManager = getEntityManager();
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<EntregaResponsavel> query = cb.createQuery(EntregaResponsavel.class);
		Root<EntregaResponsavel> root = query.from(EntregaResponsavel.class);
		query.where(cb.equal(root.get(EntregaResponsavel_.entrega), entrega));
		if (responsavelVinculadoPai == null) {
			query.where(query.getRestriction(), cb.isNull(root.get(EntregaResponsavel_.responsavelVinculado)));
		} else {
			query.where(query.getRestriction(), cb.equal(root.get(EntregaResponsavel_.responsavelVinculado), responsavelVinculadoPai));
		}
		query.orderBy(cb.asc(root.get(EntregaResponsavel_.nome)));
		return entityManager.createQuery(query).getResultList();
	}
}
